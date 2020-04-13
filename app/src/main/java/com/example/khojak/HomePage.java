package com.example.khojak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.khojak.LocationReminder.LocationReminderUpdated;
import com.example.khojak.LocationTracker.LocationTracker;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!checkPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("This feature needs Location Permission. Do you want to proceed?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            requestPermission(RequestType.NORMAL);
                        }).setNegativeButton("No", ((dialog, which) -> finish()))
                        .setIcon(android.R.drawable.ic_dialog_alert).create().show();
            } else {
                requestPermission(RequestType.NORMAL);
            }
        }

        if (!checkPermission()) {
            new AlertDialog.Builder(this)
                    .setTitle("This feature needs Location Permission. Do you want to proceed?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        requestPermission(RequestType.SPECIAL);
                    }).setNegativeButton("No", ((dialog, which) -> finish()))
                    .setIcon(android.R.drawable.ic_dialog_alert).create().show();
        }
    }

    private boolean checkPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(RequestType request) {
        switch (request) {
            case NORMAL:
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        44
                );
                break;
            case SPECIAL:
                createIntent();
                break;
        }
    }

    private void createIntent() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + this.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        this.startActivity(i);
        finish();
    }

    private enum RequestType {
        NORMAL, SPECIAL
    }

    public void openLocationReminderActivity(View view){
//        startActivity(new Intent(this, TODOListPersonal.class));
        startActivity(new Intent(this, LocationReminderUpdated.class));
    }

    public void openLocationTrackerActivity(View view){
        startActivity(new Intent(this, LocationTracker.class));
    }

}
