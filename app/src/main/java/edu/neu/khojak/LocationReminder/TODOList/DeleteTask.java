package edu.neu.khojak.LocationReminder.TODOList;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import edu.neu.khojak.LocationReminder.Database.ReminderDatabase;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;

public class DeleteTask extends AsyncTask<Void, Void, Boolean> {

    private WeakReference<NotificationService> context;
    private PersonalReminder data;
    private ReminderDatabase database;

    public DeleteTask(NotificationService context, PersonalReminder data) {
        this.context = new WeakReference<>(context);
        this.data = data;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            this.database = ReminderDatabase.getInstance(this.context.get());
            database.getReminderDao().delete(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            // do something
        }
    }
}
