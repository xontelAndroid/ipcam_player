package com.xontel.ipcam_player.dahua;

import static com.company.NetSDK.FinalVar.NET_LOGIN_ERROR_MAXCONNECT;
import static com.company.NetSDK.FinalVar.NET_LOGIN_ERROR_NETWORK;
import static com.company.NetSDK.FinalVar.NET_LOGIN_ERROR_PASSWORD;
import static com.company.NetSDK.FinalVar.NET_LOGIN_ERROR_USER;

import android.content.Context;
import android.util.Log;

import com.company.NetSDK.EM_LOGIN_SPAC_CAP_TYPE;
import com.company.NetSDK.FinalVar;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY;
import com.company.NetSDK.NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY;
import com.company.NetSDK.SDKDEV_CHANNEL_CFG;
import com.xontel.ipcam_player.CamDevice;
import com.xontel.ipcam_player.CamDeviceType;
import com.xontel.ipcam_player.IpCam;
import com.xontel.ipcam_player.R;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;


public class DahuaUtil {
    public static final String TAG = DahuaUtil.class.getSimpleName();


    public static long loginNormalDevice(Context context, CamDevice camDevice) {
                    Integer err = new Integer(0);
                    NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY stuIn = new NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY();
                    System.arraycopy(camDevice.getDomain().getBytes(), 0, stuIn.szIP, 0, camDevice.getDomain().getBytes().length);
                    stuIn.nPort =DahuaPlayer.DEFAULT_Dahua_PORT_NUMBER;
                    System.arraycopy(camDevice.getUserName().getBytes(), 0, stuIn.szUserName, 0, camDevice.getUserName().getBytes().length);
                    System.arraycopy(camDevice.getPassWord().getBytes(), 0, stuIn.szPassword, 0, camDevice.getPassWord().getBytes().length);
                    stuIn.emSpecCap = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_TCP;
                    NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY stuOut = new NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY();
                    long logId = INetSDK.LoginWithHighLevelSecurity(stuIn, stuOut);
                    if (logId == 0L) {
                        int errorValue = INetSDK.GetLastError();
                        String errorMessage;
                        switch (errorValue) {
                            case NET_LOGIN_ERROR_PASSWORD:
                            case NET_LOGIN_ERROR_USER:
                                errorMessage = "wrong_user_password";
                                break;
                            case NET_LOGIN_ERROR_NETWORK:
                                errorMessage = "network_error";
                                break;
                            case NET_LOGIN_ERROR_MAXCONNECT:
                                errorMessage = "max_users";
                                break;
                            default:
                                errorMessage = "error_occurred";

                        }
                        Log.e(TAG, "DAHUA_Login failed : " + errorMessage);
                       return 0;
                    } else {
                        Log.i(TAG, "DAHUA_Login is Successful! " + logId);
                        camDevice.setChannels(stuOut.stuDeviceInfo.nChanNum);
                        return logId;
                    }
                }




    public static Single<List<IpCam>> getChannels(CamDevice camDevice) {
        return Single.create(
                emitter -> {
                    List<IpCam> cams = new ArrayList<>();
                    Log.v(TAG, "analog ================ ");
                    for (int i = 0; i < camDevice.getChannels(); i++) {
                        Log.v(TAG, "channel : " + i);
                        cams.add(new IpCam(i , (int) camDevice.getId(), camDevice.getName(),  CamDeviceType.DAHUA.getValue(), (int) camDevice.getLogId(),false
                        ));
                    }
                    emitter.onSuccess(cams);
                }
        );
    }


    public static Single<String> extractChannelName(IpCam ipCam){
        return Single.create(emitter -> {
            String name = "";
            Integer stRet = new Integer(0);
            SDKDEV_CHANNEL_CFG[] sdk_dev_channel_cfg = new SDKDEV_CHANNEL_CFG[1];
            sdk_dev_channel_cfg[0] = new SDKDEV_CHANNEL_CFG();
            if (!INetSDK.GetDevConfig(ipCam.getLogId(),
                    FinalVar.SDK_DEV_CHANNELCFG,
                    ipCam.getChannel(), sdk_dev_channel_cfg, stRet,  5000)) {
                String errorMessage = "failed to get channel name : " + INetSDK.GetLastError();
                Log.e(TAG, errorMessage);
                emitter.onError(new Throwable(errorMessage));
            } else {
                name = new String(sdk_dev_channel_cfg[0].szChannelName, StandardCharsets.UTF_8).replaceAll("\0", "");
                Log.v(TAG, "channel name is : " + name);
                emitter.onSuccess(name);
            }
        });

    }
}
