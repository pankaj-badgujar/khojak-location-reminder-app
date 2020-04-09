package com.example.khojak.LocationReminder.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.khojak.LocationReminder.POJO.PersonalReminder;

import java.util.List;

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
}
