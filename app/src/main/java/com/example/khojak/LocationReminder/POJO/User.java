package com.example.khojak.LocationReminder.POJO;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String id;
    private String username;
    private double latitude;
    private double longitude;
    private List<Long> groupNames = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<Long> getGroups() {
        return groupNames;
    }

    public void setGroups(@NonNull List<Group> groups) {
        groups.forEach(group -> this.groupNames.add(group.getId()));
    }

    public void setGroupsIds(@NonNull List<Long> groups) {
        this.groupNames.addAll(groups);
    }

    public void setGroup(long group) {
        this.groupNames.add(group);
    }
}
