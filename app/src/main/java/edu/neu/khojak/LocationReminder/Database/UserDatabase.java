package edu.neu.khojak.LocationReminder.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.neu.khojak.LocationReminder.DAO.UserDAO;
import edu.neu.khojak.LocationReminder.POJO.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDAO getUserDao();

    private static UserDatabase noteDB;

    public static UserDatabase getInstance(Context context) {
        if (null == noteDB) {
            noteDB = databaseInstance(context);
        }
        return noteDB;
    }

    private static UserDatabase databaseInstance(Context context) {
        return Room.databaseBuilder(context,
                UserDatabase.class,
                "UserDatabase").build();
    }

    public void cleanUp(){
        noteDB = null;
    }
}
