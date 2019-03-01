package org.oz.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Random;

public class BtDev implements Parcelable, Serializable, Cloneable {

    private int uuid = new Random().nextInt(20);

    private String name;

    private String rssi;

    private String mac;

    private String type;

    public BtDev() {
    }

    public BtDev(String name, String rssi, String mac, String type) {
        this.name = name;
        this.rssi = rssi;
        this.mac = mac;
        this.type = type;
    }

    protected BtDev(Parcel in) {
        uuid = in.readInt();
        name = in.readString();
        rssi = in.readString();
        mac = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uuid);
        dest.writeString(name);
        dest.writeString(rssi);
        dest.writeString(mac);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BtDev> CREATOR = new Creator<BtDev>() {
        @Override
        public BtDev createFromParcel(Parcel in) {
            return new BtDev(in);
        }

        @Override
        public BtDev[] newArray(int size) {
            return new BtDev[size];
        }
    };

    public int getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BtDev clone() throws CloneNotSupportedException {
        return (BtDev) super.clone();
    }
}
