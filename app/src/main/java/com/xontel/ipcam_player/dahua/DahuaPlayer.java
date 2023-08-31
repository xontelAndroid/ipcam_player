package com.xontel.ipcam_player.dahua;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.company.NetSDK.CB_fMessageCallBack;
import com.company.NetSDK.CB_fRealDataCallBackEx;
import com.company.NetSDK.DEV_PLAY_RESULT;
import com.company.NetSDK.FinalVar;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.SDK_RealPlayType;
import com.company.PlaySDK.IPlaySDK;
import com.company.PlaySDK.IPlaySDKCallBack;
import com.xontel.ipcam_player.CamPlayer;
import com.xontel.ipcam_player.hikvision.CamPlayerView;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DahuaPlayer extends CamPlayer implements CamPlayerView.SurfaceCallback, IPlaySDKCallBack.fDisplayCBFun, CB_fRealDataCallBackEx, CB_fMessageCallBack {
    public static final String TAG = DahuaPlayer.class.getSimpleName();
    public static final int DEFAULT_Dahua_PORT_NUMBER = 37777;
    private final static int RAW_AUDIO_VIDEO_MIX_DATA = 0;

    private int _curVolume = 8;
    private boolean _isDelayPlay;

    private File videoRecordFile;

    public DahuaPlayer(Context context, boolean playWithSound) {
        super(context, playWithSound);
//        ((MyApp) context.getApplicationContext()).addDAHDVRRealPlayListener(this);
    }


    @Override
    public boolean stopSound() {
        if (isOpenSound) {
            boolean isSuccess = IPlaySDK.PLAYStopSoundShare(m_iPort) != 0;
            if (!isSuccess) {
                Log.e(TAG, "stop sound failed! ");
            }
            isOpenSound = false;
            return isSuccess;
        }
        return true;
    }

    @Override
    public boolean playSound() {
        if (!isOpenSound) {
            boolean isSuccess = IPlaySDK.PLAYPlaySoundShare(m_iPort) != 0;
            if (!isSuccess) {
                Log.e(TAG, "SoundShare Failed");
//                IPlaySDK.PLAYStop(m_iPort);
//                IPlaySDK.PLAYCloseStream(m_iPort);
            }
            if (-1 == _curVolume) {
                _curVolume = IPlaySDK.PLAYGetVolume(m_iPort);
            } else {
                IPlaySDK.PLAYSetVolume(m_iPort, _curVolume);
            }
            isOpenSound = true;
            return isSuccess;
        }
        return true;
    }


    @Override
    public void openStream() {
        new Thread(() -> {
            Log.v(TAG, "in player logid : " + mIpCam.getLogId() + " channel : " + mIpCam.getChannel());
            realPlayId = (int) INetSDK.RealPlayEx(mIpCam.getLogId(), mIpCam.getChannel(),SDK_RealPlayType.SDK_RType_Realplay);
            Log.e(TAG, "handle : "+ realPlayId);
            if (realPlayId == 0) {
                showError(" DAHUA_RealPlay is failed!Err: ");
                return;
            }

            if (realPlayId != 0) {
                INetSDK.SetRealDataCallBackEx(realPlayId, this, 1);
            }


        }).start();


    }


    @Override
    public void configurePlayer(int iDataType, byte[] pDataBuffer, int iDataSize) {
        if (m_iPort >= 0) {
            return;
        }

        m_iPort = IPlaySDK.PLAYGetFreePort();




        if (m_iPort == -1) {
            showError("getPort is failed with: ");
            return;
        }
        Log.i(TAG, "DAH getPort succ with: " + m_iPort);


        boolean isOpened = IPlaySDK.PLAYOpenStream(m_iPort, null, 0, STREAM_BUF_SIZE) != 0;
        if (!isOpened) {
            showError("OpenStream Failed");
        }



        if (!(mCamPlayerView != null && mCamPlayerView.getSurfaceView() != null) || IPlaySDK.PLAYPlay(m_iPort, mCamPlayerView.getSurfaceView()) == 0) {
            IPlaySDK.PLAYCloseStream(m_iPort);
            showError("PLAYPlay Failed");
            return;
        }




        if (playWithSound)
            playSound();
        if (_isDelayPlay) {
            if (IPlaySDK.PLAYSetDelayTime(m_iPort, 500 /*ms*/, 1000 /*ms*/) == 0) {
                Log.e(TAG, "SetDelayTime Failed");
            }
        }

        IPlaySDK.PLAYSetDisplayCallBack(m_iPort, this, mIpCam != null ? mIpCam.getChannel() : 0);
        isConfigured = true;
    }



    @Override
    public void play(byte[] pDataBuffer, int iDataSize) {
        IPlaySDK.PLAYInputData(m_iPort, pDataBuffer, pDataBuffer.length);
    }



    @Override
    public void unConfigurePlay() {
        if (!isConfigured) {
            return;
        }

        mHandler.removeCallbacks(mRunnable);
        mRunnable = null;
        isConfigured = false;
        IPlaySDK.PLAYRigisterDrawFun(m_iPort,0,null,0);
        stopSound();
        IPlaySDK.PLAYCleanScreen(m_iPort,0,0,0,1,0);

        try {
            if(IPlaySDK.PLAYStop(m_iPort) != 0){
                Log.e(TAG, "stop is failed! ");
            }




        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean recordVideo() {
//        if (!isRecording) {
//            try {
//                sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
//                this.isRecording = true;
//                File dir = new File(StorageHelper.getMediaDirectory(context,Environment.DIRECTORY_MOVIES).getAbsolutePath());
//                String date = sDateFormat.format(new Date());
//                File file = new File(dir, date + ".mp4");
//                videoRecordFile = file;
//                IPlaySDK.PLAYStartDataRecord(m_iPort, file.getAbsolutePath(), 1, (i, l) -> {
//                }, 1);
//            }catch (Exception e){
//                Log.i("TAG", "captureFrame: "+e.getMessage());
//            }
//
//            return true;
//        }

        return false;
    }

    @Override
    public boolean stopRecordingVideo() {
        if (isRecording) {
            isRecording = false;
            IPlaySDK.PLAYStopDataRecord(m_iPort);
            MediaScannerConnection.scanFile(context, new String[]{videoRecordFile.getAbsolutePath()}, new String[]{"video/*"}, (s, uri) -> Log.i("TATZ", "onScanCompleted_video: " + uri));
            System.out.println("DAHUA_StopSaveRealData succ!");
            return true;
        }
        return false;
    }


    @Override
    public void stopStream() {
        Log.e(TAG, "stop stream is called");
        if (realPlayId == 0L) {
            Log.e(TAG, "realPlayId = 0");
            return;
        }
        if (IPlaySDK.PLAYCloseStream(m_iPort) != 0) {
            Log.e(TAG, "closeStream is failed!");
        }
        if (INetSDK.StopRealPlayEx(realPlayId)) {
            Log.e(TAG, "StopRealPlay is failed!Err:");
        }

        if (isRecording) {
            if (INetSDK.StopSaveRealData(realPlayId)) {
                Log.e(TAG, "cannot StopSaveRealData ");
            }
        }

        if(IPlaySDK.PLAYReleasePort(m_iPort) != 0){
            Log.e(TAG,"freePort is failed!" + m_iPort);
        }
        m_iPort = -1 ;
        realPlayId = 0;
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void freePort() {
        IPlaySDK.PLAYReleasePort(m_iPort);
    }






    @Override
    public void takeSnapshot() {
//        try {
//            sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
//            File dir = new File(StorageHelper.getMediaDirectory(context,Environment.DIRECTORY_PICTURES).getAbsolutePath());
//            String date = sDateFormat.format(new Date());
//            File file = new File(dir, date + ".png");
//            IPlaySDK.PLAYCatchPic(m_iPort,file.getAbsolutePath());
//            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{"image/png"}, (s, uri) -> Log.i("TATZ", "onScanCompleted_image: " + uri));
//        }catch (Exception e){
//            Log.i("TAG", "captureFrame: "+e.getMessage());
//        }
    }

    public void stopCaptureVideo(){
        IPlaySDK.PLAYStopDataRecord(m_iPort);
    }


    @Override
    public void invoke(long rHandle, int dataType, byte[] buffer, int bufSize, int param) {

        if (RAW_AUDIO_VIDEO_MIX_DATA == dataType) {
            if(!isConfigured){
                configurePlayer(dataType, buffer, bufSize);
            }else {
                play(buffer, buffer.length);
            }
        }
    }


    @Override
    public void invoke(int nPort,byte[] pBuf,int nSize,int nWidth,int nHeight,int nStamp,int nType, long pUserData) {
//        if (lock == 0) {
            mRunnable = () -> {
                if (mCamPlayerView != null ) {
                    mCamPlayerView.showLoading(false);
                    lock = 1;
                }

            };
            mHandler.post(mRunnable);
//        }
    }

    @Override
    public boolean invoke(int lCommand, long lLoginID, Object obj, String pchDVRIP, int nDVRPort) {
        long playHandle = ((DEV_PLAY_RESULT)obj).lPlayHandle;
        if(playHandle == realPlayId && lCommand == FinalVar.SDK_REALPLAY_FAILD_EVENT){
            showError("play failed "+ playHandle);
        }
        return true;
    }
}
