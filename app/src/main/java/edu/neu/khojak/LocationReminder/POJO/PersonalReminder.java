package edu.neu.khojak.LocationReminder.POJO;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.bson.Document;

import edu.neu.khojak.LocationReminder.Util;

@Entity(tableName = "ReminderData")
public class PersonalReminder implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String title;

    public PersonalReminder(Document document) {
        title = (String) document.get("title");
        latitude = Double.parseDouble((String) document.get("latitude"));
        longitude = Double.parseDouble((String) document.get("longitude"));
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @ColumnInfo
    private double latitude;
    @ColumnInfo
    private double longitude;

    public PersonalReminder(Parcel in) {
        this.title = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public PersonalReminder() {
        this.title = "";
    }

    public PersonalReminder(String reminder, Location location) {
        this.title = reminder;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public Location getLocation() {
        Location location = new Location("");
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }

    public void setLocation(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLongitude();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static final Creator<PersonalReminder> CREATOR = new Creator<PersonalReminder>() {
        @Override
        public PersonalReminder createFromParcel(Parcel in) {
            return new PersonalReminder(in);
        }

        @Override
        public PersonalReminder[] newArray(int size) {
            return new PersonalReminder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
