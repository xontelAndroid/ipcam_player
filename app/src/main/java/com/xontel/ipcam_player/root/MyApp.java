package com.xontel.ipcam_player.root;

import android.app.Application;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.company.NetSDK.CB_fDisConnect;
import com.company.NetSDK.CB_fHaveReConnect;
import com.company.NetSDK.CB_fMessageCallBack;
import com.company.NetSDK.CB_fSubDisConnect;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_PARAM;
import com.hikvision.netsdk.HCNetSDK;
import com.xontel.ipcam_player.R;


import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {
    public static final String HIK_LOG_FILE_PATH = "/sdcard/hik_log.txt";
    public static final String DAH_LOG_FILE_PATH = "/sdcard/dah_log.txt";
    private static final List<CB_fMessageCallBack> listeners = new ArrayList<>();

    class DeviceDisConnect implements CB_fDisConnect {
        @Override
        public void invoke(long lLoginID, String pchDVRIP, int nDVRPort) {

            return;
        }
    }

    public class DeviceReConnect implements CB_fHaveReConnect {
        @Override
        public void invoke(long lLoginID, String pchDVRIP, int nDVRPort) {

        }
    }

    public class DeviceSubDisConnect implements CB_fSubDisConnect {
        @Override
        public void invoke(int emInterfaceType, boolean bOnline,
                           long lOperateHandle, long lLoginID) {

        }
    }

    public class DVRMessageCallBack implements CB_fMessageCallBack {

        @Override
        public boolean invoke(int lCommand, long lLoginID, Object obj, String pchDVRIP, int nDVRPort) {
            return true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initSdks();
    }






    public void addDAHDVRRealPlayListener(CB_fMessageCallBack cb_fMessageCallBack){
        listeners.add(cb_fMessageCallBack);
    }





    private void initSdks() {
        initHikSDK();
        initDahuaSDK();
    }

    private void initDahuaSDK() {
        // init net sdk
        DeviceDisConnect disConnect = new DeviceDisConnect();
        boolean zRet = INetSDK.Init(disConnect);

        INetSDK.SetConnectTime(1000, 1);
        NET_PARAM stNetParam = new NET_PARAM();
        stNetParam.nWaittime = 1000; // ??????????
        stNetParam.nConnectTime = 5000;
        stNetParam.nSearchRecordTime = 1000; // ?????????????
        stNetParam.nConnectTryNum = 1;

        INetSDK.SetNetworkParam(stNetParam);
        INetSDK.SetDVRMessCallBack((lCommand, lLoginID, obj, pchDVRIP, nDVRPort) -> {
            Log.e("MyApp", lCommand+"");
            for(CB_fMessageCallBack cb_fMessageCallBack : listeners){
                cb_fMessageCallBack.invoke(lCommand, lLoginID, obj, pchDVRIP, nDVRPort);
            }
            return true;
        });

        DeviceReConnect reConnect = new DeviceReConnect();
        INetSDK.SetAutoReconnect(reConnect);

        DeviceSubDisConnect subDisConnect = new DeviceSubDisConnect();
        INetSDK.SetSubconnCallBack(subDisConnect);
//        Utils.createFile(DAH_LOG_FILE_PATH);
//        Utils.openLog(DAH_LOG_FILE_PATH);
    }

    private void initHikSDK() {
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("init_sdk_init_error")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                this.finalize();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();
            return;

        }

        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, HIK_LOG_FILE_PATH, false);
    }
}
