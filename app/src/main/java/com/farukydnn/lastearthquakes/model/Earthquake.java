package com.farukydnn.lastearthquakes.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Earthquake implements Parcelable {

    private static final String TAG = "EARTHQUAKE";

    @SerializedName("data")
    private List<EarthquakeData> earthquakeList;
    private EarthquakeData earthquakeData;

    public final List<EarthquakeData> getEarthquakeList() {
        return earthquakeList;
    }


    public static class EarthquakeData implements Parcelable {
        @SerializedName("hissedilensiddedi")
        private String feltSeverity = "";

        @SerializedName("cankaybi")
        private String lifeLoss = "";

        @SerializedName("hasarlibina")
        private String damagedBuilding = "";

        @SerializedName("tarih2")
        private String dateTime2;

        @SerializedName("tarih")
        private String dateTime = "";

        @SerializedName("lokasyon")
        private String location = "";

        @SerializedName("siddeti")
        private double severity = 0;

        @SerializedName("lat")
        private String lat = "";

        @SerializedName("lng")
        private String lng = "";

        private String date = "";
        private String time = "";
        private Date realDate;

        public final String getDateTime() {
            if (dateTime2 != null)
                return dateTime2;
            else
                return dateTime;
        }

        public final String getLocation() {
            return location;
        }

        public final double getSeverity() {
            return severity;
        }

        public final String getFeltSeverity() {
            return feltSeverity;
        }

        public final String getLiseLoss() {
            return lifeLoss;
        }

        public final String getDamagedBuilding() {
            return damagedBuilding;
        }

        public final String getLat() {
            return lat;
        }

        public final String getLng() {
            return lng;
        }

        public final String getDate() {
            return date;
        }

        public final String getTime() {
            return time;
        }

        public final Date getRealDate() {
            return realDate;
        }


        public final void setDate(String date) {
            this.date = date;
        }

        public final void setTime(String time) {
            this.time = time;
        }

        public final void setRealDate(String date, String time) {
            String dateString = date + time;
            String parseFormat = "dd.MM.yyyyHH:mm";

            try {
                realDate = new SimpleDateFormat(parseFormat, Locale.getDefault())
                        .parse(dateString);

            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "setRealDate: Date parse error!");

                dateString = new SimpleDateFormat(parseFormat, Locale.getDefault())
                        .format(new Date());

                try {
                    realDate = new SimpleDateFormat(parseFormat, Locale.getDefault())
                            .parse(dateString);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.feltSeverity);
            dest.writeString(this.lifeLoss);
            dest.writeString(this.damagedBuilding);
            dest.writeString(this.dateTime2);
            dest.writeString(this.dateTime);
            dest.writeString(this.location);
            dest.writeDouble(this.severity);
            dest.writeString(this.lat);
            dest.writeString(this.lng);
            dest.writeString(this.date);
            dest.writeString(this.time);
        }

        private EarthquakeData(Parcel in) {
            this.feltSeverity = in.readString();
            this.lifeLoss = in.readString();
            this.damagedBuilding = in.readString();
            this.dateTime2 = in.readString();
            this.dateTime = in.readString();
            this.location = in.readString();
            this.severity = in.readDouble();
            this.lat = in.readString();
            this.lng = in.readString();
            this.date = in.readString();
            this.time = in.readString();
        }

        public static final Creator<EarthquakeData> CREATOR = new Creator<EarthquakeData>() {
            @Override
            public EarthquakeData createFromParcel(Parcel source) {
                return new EarthquakeData(source);
            }

            @Override
            public EarthquakeData[] newArray(int size) {
                return new EarthquakeData[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.earthquakeList);
        dest.writeParcelable(this.earthquakeData, flags);
    }

    private Earthquake(Parcel in) {
        this.earthquakeList = new ArrayList<>();
        in.readList(this.earthquakeList, EarthquakeData.class.getClassLoader());
        this.earthquakeData = in.readParcelable(EarthquakeData.class.getClassLoader());
    }

    public static final Parcelable.Creator<Earthquake> CREATOR = new Parcelable.Creator<Earthquake>() {
        @Override
        public Earthquake createFromParcel(Parcel source) {
            return new Earthquake(source);
        }

        @Override
        public Earthquake[] newArray(int size) {
            return new Earthquake[size];
        }
    };
}
