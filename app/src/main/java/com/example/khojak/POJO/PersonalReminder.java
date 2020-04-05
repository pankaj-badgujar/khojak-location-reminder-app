package com.example.khojak.POJO;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class PersonalReminder implements Parcelable {
    private String title;
    private Location location;

    public PersonalReminder(Parcel in) {
        this.title = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
    }

    public PersonalReminder() {
        this.title = "";
        this.location = null;
    }

    public PersonalReminder(String reminder, Location location) {
        this.title = reminder;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        parcel.writeParcelable(location, i);
    }
}
