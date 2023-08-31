package com.xontel.ipcam_player;

import android.os.Parcel;
import android.os.Parcelable;



public class IpCam implements Parcelable {
    public static final String TAG = IpCam.class.getSimpleName();


    public long id;

    private long deviceId;

    private String deviceName;

    private int type;

    private int streamType = 1;

    private String name;

    private String fullName;

    private boolean included = true;

    private boolean analog;

    private int channel;


    private boolean soundEnabled;

    private long logId;


    public IpCam(long id, long deviceId, String deviceName, int type, int streamType, String name, String fullName, boolean included, boolean analog, int channel, boolean soundEnabled) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.type = type;
        this.streamType = streamType;
        this.name = name;
        this.fullName = fullName;
        this.included = included;
        this.channel = channel;
        this.analog = analog;
        this.soundEnabled = soundEnabled;
    }

    public IpCam(int channel, long deviceId, String deviceName, int type, long logId, boolean analog) {
        this.channel = channel;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.type = type;
        this.logId = logId;
        this.analog = analog;
    }

    public static final Creator<IpCam> CREATOR = new Creator<IpCam>() {
        @Override
        public IpCam createFromParcel(Parcel in) {
            return new IpCam(in);
        }

        @Override
        public IpCam[] newArray(int size) {
            return new IpCam[size];
        }
    };

    public boolean isAnalog() {
        return analog;
    }

    public void setAnalog(boolean analog) {
        this.analog = analog;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStreamType() {
        return streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        fullName = deviceName + "-" + name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public void toggleStreamType() {
        this.streamType = (streamType == 1) ? 0 : 1;
    }


    protected IpCam(Parcel in) {
        id = in.readLong();
        channel = in.readInt();
        deviceId = in.readInt();
        deviceName = in.readString();
        type = in.readInt();
        streamType = in.readInt();
        logId = in.readInt();
        name = in.readString();
        fullName = in.readString();
        included = in.readByte() != 0;
        analog = in.readByte() != 0;
        soundEnabled = in.readByte() != 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeInt(channel);
        parcel.writeLong(deviceId);
        parcel.writeString(deviceName);
        parcel.writeInt(type);
        parcel.writeInt(streamType);
        parcel.writeLong(logId);
        parcel.writeString(name);
        parcel.writeString(fullName);
        parcel.writeByte((byte) (included ? 1 : 0));
        parcel.writeByte((byte) (analog ? 1 : 0));
        parcel.writeByte((byte) (soundEnabled ? 1 : 0));
    }


}
