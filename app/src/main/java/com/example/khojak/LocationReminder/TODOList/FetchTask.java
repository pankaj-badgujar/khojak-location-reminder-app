package com.example.khojak.LocationReminder.TODOList;

import android.os.AsyncTask;

import com.example.khojak.LocationReminder.Database.ReminderDatabase;
import com.example.khojak.LocationReminder.POJO.PersonalReminder;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;

public class FetchTask extends AsyncTask<Void, Void, List<PersonalReminder>> {

    private WeakReference<TODOListPersonal> context;
    private ReminderDatabase database;
    private Consumer<List<PersonalReminder>> function;

    public FetchTask(TODOListPersonal activity, Consumer<List<PersonalReminder>> function) {
        context = new WeakReference<>(activity);
        this.function = function;
    }

    @Override
    protected List<PersonalReminder> doInBackground(Void... objs) {
        this.database = ReminderDatabase.getInstance(context.get());
        return database.getReminderDao().getAllURL();
    }

    @Override
    protected void onPostExecute(List<PersonalReminder> data) {
       if(function != null ) {
           this.function.accept(data);
       }
    }
}
