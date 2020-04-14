package edu.neu.khojak.LocationReminder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.tabs.TabLayout;

import edu.neu.khojak.LocationReminder.Adapters.ReminderAdapter;
import edu.neu.khojak.LocationReminder.Adapters.SectionsPagerAdapter;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;
import edu.neu.khojak.LocationReminder.TODOList.LocationActivity;
import edu.neu.khojak.LocationReminder.ViewModel.ReminderViewModel;
import edu.neu.khojak.R;

public class CombinedReminders extends AppCompatActivity implements PersonalRemindersFragment.OnFragmentInteractionListener {


    private AlertDialog inputDialog;
    private EditText reminderTitle;
    private final Context context = this;
    private final static int REQUEST_CODE_1 = 1;
    private ReminderViewModel reminderViewModel;
    private Location location;
    private final static String emptyText = "";
    private TextView radiusText;
    private SeekBar radiusSeekBar;
    private static int reminderRadius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combined_reminders);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting adapter for tabs
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        //setting up tabs
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //initializing adapter
        final ReminderAdapter adapter = new ReminderAdapter();

        //setting up reminderModel
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderViewModel.getAllReminders().observe(this, reminders -> adapter.setReminders(reminders));

        FloatingActionsMenu fabMenu = findViewById(R.id.fabMenu);

        FloatingActionButton addPersonalReminderBtn = findViewById(R.id.addPersonalReminderBtn);
        addPersonalReminderBtn.setOnClickListener(view -> {
            // get activity_reminder_info.xml as prompt
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            final View promptView = layoutInflater.inflate(R.layout.activity_reminder_info, null);
            reminderTitle = promptView.findViewById(R.id.urlName);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            // set activity_reminder_info.xml to be the layout file of the inputDialog builder
            alertDialogBuilder.setView(promptView);

            // setup a dialog window
            alertDialogBuilder.setCancelable(true);

            // create an alert dialog
            inputDialog = alertDialogBuilder.create();

            inputDialog.show();
            fabMenu.collapse();

        });

        FloatingActionButton addGroupReminderBtn = findViewById(R.id.addGroupReminderBtn);
        addGroupReminderBtn.setOnClickListener(view -> {
            Toast.makeText(this,"Group reminder pressed", Toast.LENGTH_SHORT).show();
            fabMenu.collapse();
        });

    }

    public void clearText(View view) {
        reminderTitle.setText(emptyText);
    }

    public void closeActivity(View view) {
        inputDialog.dismiss();
    }

    public void onSetLocationPressed(View view) {
        Toast.makeText(context, "starting map activity", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(context, LocationActivity.class), REQUEST_CODE_1);
    }

    public void exit(View view) {
        inputDialog.dismiss();
    }

    public void createReminder(View view) {
        String reminder = reminderTitle.getText().toString();
        if (reminder.isEmpty()) {
            reminderTitle.setError(getString(R.string.empty_field_error));
        } else if (location == null) {
            Toast.makeText(context, " Location cannot be empty.",
                    Toast.LENGTH_LONG).show();
        } else {
            PersonalReminder personalReminder = new PersonalReminder(reminder, location);
            reminderViewModel.addReminder(personalReminder);
            this.location = null;
            inputDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_1:
                if(data != null) {
                    this.location = data.getParcelableExtra("location");
                    Toast.makeText(context, location != null ? location.toString() : "",
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void updateRadiusText(int newRadius){
        radiusText.setText("Radius (in miles) : " + newRadius);
    }

    SeekBar.OnSeekBarChangeListener radiusSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateRadiusText(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public void openRadiusDialog(){

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View promptView = layoutInflater.inflate(R.layout.radius_slider,null);

        AlertDialog.Builder radiusAlertDialogBuilder = new AlertDialog.Builder(context);

        // set activity_reminder_info.xml to be the layout file of the inputDialog builder
        radiusAlertDialogBuilder.setView(promptView);

        radiusSeekBar = promptView.findViewById(R.id.radiusSeekBar);
        radiusSeekBar.setOnSeekBarChangeListener(radiusSeekBarChangeListener);
        radiusText = promptView.findViewById(R.id.radiusText);
        updateRadiusText(radiusSeekBar.getProgress());

        // setup a dialog window
        radiusAlertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Apply", ((dialog, id) ->{
                    reminderRadius = radiusSeekBar.getProgress();
                    Toast.makeText(this,"Radius set to "+reminderRadius,Toast.LENGTH_SHORT).show();
                }
                ))
                .setNegativeButton("Cancel", ((dialog, id) -> dialog.cancel() ));

        // create an alert dialog
        radiusAlertDialogBuilder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.location_reminder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.locationReminderMenu:
                openRadiusDialog();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void createGroup(View view){
        startActivity(new Intent(this, CreateGroup.class));
        Toast.makeText(context, "create group pressed", Toast.LENGTH_SHORT).show();
    }
}