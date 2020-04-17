package edu.neu.khojak;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import edu.neu.khojak.LocationReminder.CombinedReminders;
import edu.neu.khojak.LocationReminder.Service.NotificationService;
import edu.neu.khojak.LocationReminder.Util;
import edu.neu.khojak.LocationTracker.LocationTracker;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.fetchData();
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
    }

    public void openLocationReminderActivity(View view){
        startActivity(new Intent(this, CombinedReminders.class));
    }


    public void openLocationTrackerActivity(View view){
        startActivity(new Intent(this, LocationTracker.class));
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // setup a dialog window
        alertDialogBuilder
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes, Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });

        // create an alert dialog
        AlertDialog exitConfirmationDialog = alertDialogBuilder.create();

        exitConfirmationDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.homepage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutOption:
                Toast.makeText(this,"Logout pressed", Toast.LENGTH_SHORT).show();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }
}
