package edu.neu.khojak.LocationReminder.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;

@Dao
public interface ReminderDAO {

    @Query("Select * from ReminderData")
    List<PersonalReminder> getAllURL();

    @Insert
    void insert(PersonalReminder data);

    @Update
    void update(PersonalReminder data);

    @Delete
    void delete(PersonalReminder data);

    @Query("SELECT * from ReminderData")
    LiveData<List<PersonalReminder>> getAllReminders();
}
