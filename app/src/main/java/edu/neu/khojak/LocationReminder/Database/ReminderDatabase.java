package edu.neu.khojak.LocationReminder.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.neu.khojak.LocationReminder.DAO.ReminderDAO;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;

@Database(entities = {PersonalReminder.class}, version = 1)
public abstract class ReminderDatabase extends RoomDatabase {

    public abstract ReminderDAO getReminderDao();

    private static ReminderDatabase noteDB;

    public static ReminderDatabase getInstance(Context context) {
        if (null == noteDB) {
            noteDB = databaseInstance(context);
        }
        return noteDB;
    }

    private static ReminderDatabase databaseInstance(Context context) {
        return Room.databaseBuilder(context,
                ReminderDatabase.class,
                "ReminderDatabase").build();
    }

    public void cleanUp(){
        noteDB = null;
    }
}
