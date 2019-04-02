package com.linklab.INERTIA.besi_c;


import android.os.Parcel;
import android.os.Parcelable;

public class Beac implements Parcelable {
    private int id;

    private int Major;
    private int Rsi;
    private String tim;
    private double distane;



    // private int imag;


    // constructor


    public Beac(int id, int major, int rsi, double distance, String time) {
        this.id = id;
        this.Major=major;
        this.Rsi=rsi;
        // MacAddress = macAddress;
        // this.Rsi = Rsi;
        this.tim = time;
        this.distane = distance;
        //  this.imag=imageid;
    }

    // setter and getter
    // public int getImag() {
    //   return imag;
    //  }

    // public void setImag(int imag) {
    //      this.imag = imag;
    //  }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRsi() {
        return Rsi;
    }

    public void setRsi(int rsi) {
        Rsi = rsi;
    }

    public int getMajor() {
        return Major;
    }

    public void setMajor(int major) {
        Major = major;
    }

    public String getTim() {
        return tim;
    }

    public void setTim(String tim) {
        this.tim = tim;
    }

    public double getDistane() {
        return distane;
    }

    public void setDistane(double distane) {
        this.distane = distane;
    }

    public Beac(Parcel in){
        //String[] data = new String[5];

        //in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.id = in.readInt();
        this.Major = in.readInt();
        this.Rsi= in.readInt();
        this.tim = in.readString();
        this.distane = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.Major);
        dest.writeInt(this.Rsi);
        dest.writeString(this.tim);
        dest.writeDouble(this.distane);
    }

    public static final Creator CREATOR = new Creator() {
        public Beac createFromParcel(Parcel in) {
            return new Beac(in);
        }

        public Beac[] newArray(int size) {
            return new Beac[size];
        }
    };

}
