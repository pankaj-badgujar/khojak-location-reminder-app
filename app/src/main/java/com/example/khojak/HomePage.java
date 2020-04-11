package com.example.khojak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.khojak.LocationReminder.LocationReminderUpdated;
import com.example.khojak.LocationTracker.LocationTracker;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void openLocationReminderActivity(View view){
//        startActivity(new Intent(this, TODOListPersonal.class));
        startActivity(new Intent(this, LocationReminderUpdated.class));
    }

    public void openLocationTrackerActivity(View view){
        startActivity(new Intent(this, LocationTracker.class));
    }

}
