package edu.neu.khojak.LocationReminder.TODOList;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;

import edu.neu.khojak.R;

public class ReminderInfo extends AppCompatActivity {

    private EditText reminderTitle;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_info);
        reminderTitle = findViewById(R.id.reminderTitle);
        this.location = getIntent().getExtras().getParcelable("location");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        getWindow().setLayout((int) (width * 0.95), (int) (height * 0.6));
    }

    public void createReminder(View view) {
        String reminder = reminderTitle.getText().toString();
        if(reminder.isEmpty()) {
            reminderTitle.setError(getString(R.string.empty_field_error));
        } else {
            PersonalReminder personalReminder = new PersonalReminder(reminder, location);
            Intent intent = new Intent();
            intent.putExtra("reminder",personalReminder);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

}
