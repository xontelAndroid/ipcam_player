package com.xontel.ipcam_player;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;


public class CamDevice implements Parcelable {

    public long id;

    public String name;

    public String domain;

    public String userName;

    public String password;

    public int deviceType;

    public int startDigitalChan;


    public int channels;


    public boolean included;

    public long logId;


    List<IpCam> cams = new ArrayList<>();


    public CamDevice(long id,
                     String name,
                     String domain,
                     String userName,
                     String password,
                     int deviceType,
                     int startDigitalChan,
                     int channels,
                     boolean included) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.userName = userName;
        this.password = password;
        this.deviceType = deviceType;
        this.startDigitalChan = startDigitalChan;
        this.channels = channels;
        this.included = included;
        this.logId = 0;
    }



    public CamDevice(String name, String domain, String userName, String password, int deviceType) {
        this.name = name;
        this.domain = domain;
        this.userName = userName;
        this.password = password;
        this.deviceType = deviceType;
        this.logId = 0;
    }


    public List<IpCam> getCams() {
        return cams;
    }

    public void setCams(List<IpCam> cams) {
        this.cams = cams;
        if(cams != null){
            channels = cams.size();
        }
    }


    protected CamDevice(Parcel in) {
        id = in.readInt();
        name = in.readString();
        domain = in.readString();
        userName = in.readString();
        password = in.readString();
        deviceType = in.readInt();
        startDigitalChan = in.readInt();
        channels = in.readInt();
        logId = in.readLong();
    }

    public static final Creator<CamDevice> CREATOR = new Creator<CamDevice>() {
        @Override
        public CamDevice createFromParcel(Parcel in) {
            return new CamDevice(in);
        }

        @Override
        public CamDevice[] newArray(int size) {
            return new CamDevice[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(domain);
        parcel.writeString(userName);
        parcel.writeString(password);
        parcel.writeInt(deviceType);
        parcel.writeInt(startDigitalChan);
        parcel.writeInt(channels);
        parcel.writeLong(logId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public int getChannels() {
        return channels;
    }
    public void setChannels(int channels) {
        this.channels = channels;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public void setDeviceLoggedOut() {
//        if (CamDeviceType.HIKVISION.getValue() == deviceType)
//            logId = -1;
//        logId = 0;
    }


    public String getDomain() {
        return domain;
    }

    public void setDomain(String ipAddress) {
        this.domain = ipAddress;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return password;
    }

    public void setPassWord(String passWord) {
        this.password = passWord;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }




    @Override
    public String toString() {
        return "CamDevice{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", deviceType=" + deviceType +
                ", channels=" + channels +
                ", logId=" + logId +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return ((CamDevice) obj).id == this.id;
    }

}

