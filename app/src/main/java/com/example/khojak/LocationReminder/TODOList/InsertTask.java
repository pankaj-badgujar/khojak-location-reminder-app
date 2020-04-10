package com.example.khojak.LocationReminder.TODOList;

import android.os.AsyncTask;

import com.example.khojak.LocationReminder.Database.ReminderDatabase;
import com.example.khojak.LocationReminder.POJO.PersonalReminder;

import java.lang.ref.WeakReference;

public class InsertTask extends AsyncTask<Void, Void, Boolean> {

    private WeakReference<TODOListPersonal> context;
    private PersonalReminder data;
    private ReminderDatabase database;

    public InsertTask(TODOListPersonal context, PersonalReminder dataList) {
        this.context = new WeakReference<>(context);
        this.data = dataList;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            this.database = ReminderDatabase.getInstance(this.context.get());
            database.getReminderDao().insert(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
         // Do something
        }
    }
}
