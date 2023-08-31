package com.xontel.ipcam_player;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;


import com.xontel.ipcam_player.hikvision.CamPlayerView;

import java.text.SimpleDateFormat;

public abstract class CamPlayer implements CamPlayerView.SurfaceCallback {

    public final static int STREAM_BUF_SIZE = 1024 * 1024 * 2;
    public Context context;
    public SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
    public MutableLiveData<Boolean> isError = new MutableLiveData(false);
    public MutableLiveData<Boolean> isLoading = new MutableLiveData(true);

    public boolean isRecording ;
    public CamPlayerView mCamPlayerView;

    public Toast mToast;

    private boolean isSurfaceCreated;
    public int m_iPort = -1;

    public int realPlayId = -1 ;
    public IpCam mIpCam;
    public boolean isPlaying;
    public boolean isConfigured = false;

    public boolean isOpenSound;

    public boolean playWithSound ;


    public int lock = 0;

    public Handler mHandler = new Handler(Looper.getMainLooper());
    public Runnable mRunnable;

    private Thread stopThread ;



    public CamPlayer(Context context, boolean playWithSound) {
        this.context = context;
        this.playWithSound = playWithSound;
    }


    public void startLiveView() {
        Log.v(getTAG(), "startLiveView___");
        isPlaying = true;
        openStream();
    }

    public void stopLiveView() {

        mCamPlayerView.onDetachedFromPlayer();
        detachView();

        stopThread = new Thread(() -> {
            Log.e(getTAG(), CamPlayer.this.hashCode()+"");
            unConfigurePlay();
            stopStream();
            isPlaying = false;
//            onReady();
        });
        stopThread.start();

    }


    public boolean setSoundEnabled(boolean enabled) {
        if (isPlaying) {
            if (enabled)
                playSound();
            else
                stopSound();
        }
        return false;
    }

    public abstract boolean playSound();

    public abstract boolean stopSound();

    public abstract void openStream();

    public abstract void stopStream();

    public abstract String getTAG();

    public void showError(String logMessage) {
        isPlaying = false;

        if(m_iPort != -1){
            freePort();
        }
        Log.e(getTAG(), logMessage);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mCamPlayerView != null) {
                    mCamPlayerView.showLoading(false);
                    mCamPlayerView.showError(context.getString(R.string.error_while_playing));
                }
            }
        });

    }

    public void showMessage(String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(mToast != null){
                    mToast.cancel();
                }
                mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                mToast.show();
            }
        });

    }


    public abstract void freePort();

    public void attachView(CamPlayerView camPlayerView, IpCam ipCam, boolean playWithSound) {
        this.playWithSound = playWithSound;
        this.mIpCam = ipCam;
        this.mCamPlayerView = camPlayerView;
        mCamPlayerView.onAttachToPlayer(this, hashCode()+"");
        if (mCamPlayerView.isSurfaceCreated() && !isPlaying) {
            Log.v(getTAG(), "first");
            startLiveView();
        }
    }

    public abstract void configurePlayer(int iDataType, byte[] pDataBuffer, int iDataSize);

    public abstract void play(byte[] pDataBuffer, int iDataSize);

    public abstract void unConfigurePlay();



    public abstract boolean recordVideo();

    public abstract boolean stopRecordingVideo();


    public abstract void takeSnapshot();


    public void detachView() {
        mCamPlayerView = null;
        mIpCam = null;
    }

    public IpCam getIpCam() {
        return mIpCam;
    }

    @Override
    public void onSurfaceCreated() {
        isSurfaceCreated = true;
        Log.v(getTAG(), "last");
        startLiveView();
    }

    @Override
    public void onSurfaceDestroyed() {
        isSurfaceCreated = false;
    }
    public void onReady(){
        if(mCamPlayerView != null && mCamPlayerView.isSurfaceCreated()){
            startLiveView();
        }
    }
}
