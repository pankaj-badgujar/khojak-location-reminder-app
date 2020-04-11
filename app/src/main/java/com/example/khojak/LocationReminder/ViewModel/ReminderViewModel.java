package com.example.khojak.LocationReminder.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.khojak.LocationReminder.POJO.PersonalReminder;
import com.example.khojak.LocationReminder.Repository.ReminderRepository;

import java.util.List;

public class ReminderViewModel extends AndroidViewModel {
    private ReminderRepository reminderRepository;
    private LiveData<List<PersonalReminder>> allReminders;

    public ReminderViewModel(@NonNull Application application) {
        super(application);
        reminderRepository = new ReminderRepository(application);
        allReminders = reminderRepository.getAllReminders();
    }

    public void addReminder(PersonalReminder reminder){
        reminderRepository.addReminder(reminder);
    }

    public void deleteReminder(PersonalReminder reminder){
        reminderRepository.deleteReminder(reminder);
    }

    public LiveData<List<PersonalReminder>> getAllReminders() {
        return allReminders;
    }
}
