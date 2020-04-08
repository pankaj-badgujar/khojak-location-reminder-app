package com.example.khojak.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.khojak.POJO.PersonalReminder;

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
