package edu.neu.khojak.LocationReminder.POJO;

import java.util.List;

public class Group {

    private final long id;
    private long name;
    private List<String> users;
    private List<Long> reminders;

    public Group() {
        this.id = System.currentTimeMillis();
    }

    public Group(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getName() {
        return name;
    }

    public void setName(long name) {
        this.name = name;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void setUser(List<String> users) {
        this.users = users;
    }

    public List<Long> getReminders() {
        return reminders;
    }

    public void setReminders(List<Long> reminders) {
        this.reminders = reminders;
    }
}
