package edu.neu.khojak.LocationReminder.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.neu.khojak.LocationReminder.POJO.User;

@Dao
public interface UserDAO {
    @Query("Select * from User")
    List<User> getAllUsers();

    @Insert
    void insert(User data);

    @Update
    void update(User data);

    @Query("Delete from User")
    void delete();

    @Query("SELECT * from User")
    LiveData<List<User>> getAllUser();
}
