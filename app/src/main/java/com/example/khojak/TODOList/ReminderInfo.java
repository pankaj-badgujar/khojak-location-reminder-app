package com.example.khojak.TODOList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.example.khojak.POJO.PersonalReminder;
import com.example.khojak.R;

public class ReminderInfo extends AppCompatActivity {

    private final static String emptyText = "";
    private final static String errorText = "This field cannot be empty.";
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

    public void closeActivity(View view) {
        finish();
    }

    public void clearText(View view) {
        reminderTitle.setText(ReminderInfo.emptyText);
    }

    public void createReminder(View view) {
        String reminder = reminderTitle.getText().toString();
        if(reminder.isEmpty()) {
            reminderTitle.setError(errorText);
        } else {
            PersonalReminder personalReminder = new PersonalReminder(reminder, location);
            Intent intent = new Intent();
            intent.putExtra("reminder",personalReminder);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void exit(View view) {
        finish();
    }
}
