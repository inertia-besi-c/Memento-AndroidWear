package com.linklab.INERTIA.besi_c;

// Imports
import android.os.Parcel;
import android.os.Parcelable;

/* Special way to log data for the estimote.. (This was moved from Jamie's File and was just used) PLEASE DO NOT REMOVE */
public class Beacon implements Parcelable
{
    private final int id;
    private final int Major;
    private final int Rsi;
    private final String tim;
    private final double distane;

    Beacon(int id, int major, int rsi, double distance, String time)
    {
        this.id = id;
        this.Major=major;
        this.Rsi=rsi;
        this.tim = time;
        this.distane = distance;
    }

    private Beacon(Parcel in)
    {
        this.id = in.readInt();
        this.Major = in.readInt();
        this.Rsi= in.readInt();
        this.tim = in.readString();
        this.distane = in.readDouble();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.id);
        dest.writeInt(this.Major);
        dest.writeInt(this.Rsi);
        dest.writeString(this.tim);
        dest.writeDouble(this.distane);
    }

    public static final Creator CREATOR = new Creator()
    {
        public Beacon createFromParcel(Parcel in)
        {
            return new Beacon(in);
        }
        public Beacon[] newArray(int size)
        {
            return new Beacon[size];
        }
    };
}
