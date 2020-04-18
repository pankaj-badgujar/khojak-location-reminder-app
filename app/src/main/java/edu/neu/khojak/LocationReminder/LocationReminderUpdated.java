package edu.neu.khojak.LocationReminder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.neu.khojak.LocationReminder.Adapters.ReminderAdapter;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;
import edu.neu.khojak.LocationReminder.TODOList.LocationActivity;
import edu.neu.khojak.LocationReminder.Service.NotificationService;
import edu.neu.khojak.LocationReminder.ViewModel.ReminderViewModel;
import edu.neu.khojak.R;
import es.dmoral.toasty.Toasty;

public class LocationReminderUpdated extends AppCompatActivity {

    private ReminderViewModel reminderViewModel;
    private final static int REQUEST_CODE_1 = 1;
    private final Context context = this;
    private AlertDialog inputDialog;
    private EditText reminderTitle;
    private Location location;
    private final static String emptyText = "";
    private TextView radiusText;
    private SeekBar radiusSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_reminder_updated);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final ReminderAdapter adapter = new ReminderAdapter();
        recyclerView.setAdapter(adapter);

        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderViewModel.getAllReminders().observe(this, adapter::setReminders);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                PersonalReminder reminder = adapter.getLinkAt(viewHolder.getAdapterPosition());
                reminderViewModel.deleteReminder(reminder);

                Toasty.error(LocationReminderUpdated.this, "Reminder deleted", Toast.LENGTH_SHORT)
                        .show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new ReminderAdapter.OnLinkItemClickListener() {
            @Override
            public void onLinkItemClick(PersonalReminder reminder) {
//                Toast.makeText(LocationReminderUpdated.this, "Reminder opened", Toast.LENGTH_SHORT)
//                        .show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.createReminderBtn);
        fab.setOnClickListener(view -> {
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

        });
    }

    public void clearText(View view) {
        reminderTitle.setText(emptyText);
    }

    public void closeActivity(View view) {
        inputDialog.dismiss();
    }

    public void onSetLocationPressed(View view) {
        Toast.makeText(this, "starting map activity", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(this, LocationActivity.class), REQUEST_CODE_1);
    }

    public void exit(View view) {
        inputDialog.dismiss();
    }

    public void createReminder(View view) {
        String reminder = reminderTitle.getText().toString();
        if (reminder.isEmpty()) {
            reminderTitle.setError(getString(R.string.empty_field_error));
        } else if (location == null) {
            Toast.makeText(this, " Location cannot be empty.",
                    Toast.LENGTH_LONG).show();
        } else {
            PersonalReminder personalReminder = new PersonalReminder(reminder, location);
            reminderViewModel.addReminder(personalReminder);
            this.location = null;
            inputDialog.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_1:
                if(data != null) {
                    this.location = data.getParcelableExtra("location");
                    Toast.makeText(this, location != null ? location.toString() : "",
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void updateRadiusText(int newRadius){
        radiusText.setText("Radius (in Kms) : " + newRadius);
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
        radiusSeekBar.setProgress(NotificationService.reminderRadius);
        updateRadiusText(radiusSeekBar.getProgress());

        // setup a dialog window
        radiusAlertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Apply", ((dialog, id) ->{
                    NotificationService.reminderRadius = radiusSeekBar.getProgress();
                    Toast.makeText(this,"Radius set to "+
                            NotificationService.reminderRadius,Toast.LENGTH_SHORT).show();
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
}
