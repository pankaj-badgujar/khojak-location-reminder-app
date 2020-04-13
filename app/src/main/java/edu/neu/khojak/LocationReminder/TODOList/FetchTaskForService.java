package edu.neu.khojak.LocationReminder.TODOList;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;

import edu.neu.khojak.LocationReminder.Database.ReminderDatabase;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;

public class FetchTaskForService extends AsyncTask<Void, Void, List<PersonalReminder>> {

    private WeakReference<NotificationService> context;
    private ReminderDatabase database;
    private Consumer<List<PersonalReminder>> function;

    public FetchTaskForService(NotificationService activity, Consumer<List<PersonalReminder>> function) {
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
        if (function != null) {
            this.function.accept(data);
        }
    }
}
