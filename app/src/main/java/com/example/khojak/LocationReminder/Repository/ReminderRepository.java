package com.example.khojak.LocationReminder.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.khojak.LocationReminder.DAO.ReminderDAO;
import com.example.khojak.LocationReminder.Database.ReminderDatabase;
import com.example.khojak.LocationReminder.POJO.PersonalReminder;

import java.util.List;

public class ReminderRepository {

    private ReminderDAO reminderDAO;
    private LiveData<List<PersonalReminder>> allReminders;

    public ReminderRepository(Application application){
        ReminderDatabase db = ReminderDatabase.getInstance(application);
        reminderDAO = db.getReminderDao();
        allReminders= reminderDAO.getAllReminders();
    }

    public LiveData<List<PersonalReminder>> getAllReminders() {
        return allReminders;
    }

    public void addReminder(PersonalReminder newReminder){
        new InsertReminderAsyncTask(reminderDAO).execute(newReminder);
    }

    public void deleteReminder(PersonalReminder reminder){
        new DeleteReminderAsyncTask(reminderDAO).execute(reminder);
    }

    private static class InsertReminderAsyncTask extends
            AsyncTask<PersonalReminder, Void, Void> {

        private ReminderDAO asyncTaskDao;

        private InsertReminderAsyncTask(ReminderDAO dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PersonalReminder... reminders) {
            asyncTaskDao.insert(reminders[0]);
            return null;
        }
    }

    private static class DeleteReminderAsyncTask extends
            AsyncTask<PersonalReminder, Void, Void> {

        private ReminderDAO asyncTaskDao;

        private DeleteReminderAsyncTask(ReminderDAO dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PersonalReminder... reminders) {
            asyncTaskDao.delete(reminders[0]);
            return null;
        }
    }

}
