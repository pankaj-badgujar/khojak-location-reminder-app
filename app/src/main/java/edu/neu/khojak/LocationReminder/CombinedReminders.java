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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import edu.neu.khojak.LocationReminder.Adapters.ReminderAdapter;
import edu.neu.khojak.LocationReminder.Adapters.SectionsPagerAdapter;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;
import edu.neu.khojak.LocationReminder.TODOList.LocationActivity;
import edu.neu.khojak.LocationReminder.ViewModel.ReminderViewModel;
import edu.neu.khojak.R;

public class CombinedReminders extends AppCompatActivity implements PersonalRemindersFragment.OnFragmentInteractionListener{

    private AlertDialog personalReminderInputDialog;
    private AlertDialog groupReminderInputDialog;

    private EditText personalReminderTitle;
    private EditText groupReminderTitle;

    private final Context context = this;
    private final static int PERSONAL_REMINDER_REQUEST_CODE = 1;
    private final static int GROUP_REMINDER_REQUEST_CODE = 2;
    private ReminderViewModel reminderViewModel;
    private Location personalReminderLocation;
    private Location groupReminderLocation;

    private final static String emptyText = "";
    private TextView radiusText;
    private SeekBar radiusSeekBar;
    private static int reminderRadius;
    private Spinner spinner;
    private Button setPersonalLocationBtn;
    private Button setGroupLocationBtn;
    private String[] groupNames;



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

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().toString().equals("Group Reminders")) {
                    Util.fetchData();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getText().toString().equals("Group Reminders")) {
                    Util.fetchData();
                }
            }
        });
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

            personalReminderTitle = promptView.findViewById(R.id.urlName);
            setPersonalLocationBtn = promptView.findViewById(R.id.setPersonalLocationBtn);

            setPersonalLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSetLocationPressed(v, PERSONAL_REMINDER_REQUEST_CODE);
                }
            });

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            // set activity_reminder_info.xml to be the layout file of the personalReminderInputDialog builder
            alertDialogBuilder.setView(promptView);

            // setup a dialog window
            alertDialogBuilder.setCancelable(false);

            // create an alert dialog
            personalReminderInputDialog = alertDialogBuilder.create();

            personalReminderInputDialog.show();
            fabMenu.collapse();

        });

        FloatingActionButton addGroupReminderBtn = findViewById(R.id.addGroupReminderBtn);
        addGroupReminderBtn.setOnClickListener(view -> {
            if(Util.groupData.size() < 1){
                Snackbar.make(view,getString(R.string.noGroupsPresentMsg), Snackbar.LENGTH_LONG).show();
            } else{
                openGroupReminderDialog();
            }
            fabMenu.collapse();
        });

    }

    private void openGroupReminderDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.group_reminder_input_dialog, null);

        //initialize fields from dialog box layout
        groupReminderTitle = dialogView.findViewById(R.id.groupReminderTitleEditText);
        setGroupLocationBtn = dialogView.findViewById(R.id.setGroupLocationBtn);
        setGroupLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetLocationPressed(v, GROUP_REMINDER_REQUEST_CODE);
            }
        });
        spinner = dialogView.findViewById(R.id.spinner);



        String[] groupNames = Util.groupData.stream()
                .map(document -> document.get("groupName").toString()).toArray(String[]::new);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, groupNames );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setTitle("Create new group reminder")
        .setNegativeButton("Cancel" , null)
        .setPositiveButton("Create", null);

        groupReminderInputDialog = alertDialogBuilder.create();
        groupReminderInputDialog.show();

        groupReminderInputDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupReminderTitle.getText().toString().trim().isEmpty()){
                    groupReminderTitle.setError(getString(R.string.empty_field_error));
                }
                else if(groupReminderLocation == null){
                    Toast.makeText(CombinedReminders.this, getString(R.string.locationEmptyMsg), Toast.LENGTH_SHORT).show();
                }
                else {
                    createGroupReminder();
                    groupReminderInputDialog.dismiss();
                }

            }
        });

    }



    public void createGroupReminder(){
        Document newGroupReminder = new Document();
        String groupId =  Util.groupData.get(spinner.getSelectedItemPosition()).get("_id")
                .toString();
        newGroupReminder.put("title", groupReminderTitle.getText().toString());
        newGroupReminder.put("longitude",String.valueOf(groupReminderLocation.getLongitude()));
        newGroupReminder.put("latitude",String.valueOf(groupReminderLocation.getLatitude()));
        newGroupReminder.put("groupName",spinner.getSelectedItem().toString());
        newGroupReminder.put("groupId", groupId);

        Util.reminderCollection.insertOne(newGroupReminder).addOnCompleteListener(reminderInsertedTask -> {
            if(!reminderInsertedTask.isSuccessful() || reminderInsertedTask.getResult() == null) {
                return;
            }
            String reminderId = Util
                    .getId(reminderInsertedTask.getResult().getInsertedId());
            Document group = new Document("_id",new ObjectId(groupId));
            Util.groupCollection.findOne(group).addOnCompleteListener(reminderGroup -> {
                if(!reminderGroup.isSuccessful() || reminderGroup.getResult() == null ){
                    return;
                }
                Document groupDocument = reminderGroup.getResult();
                List<String> groupReminderIds = groupDocument.get("reminderIds") != null ?
                        (List<String>) groupDocument.get("reminderIds") : new ArrayList<>();
                groupReminderIds.add(reminderId);
                groupDocument.remove("reminderIds");
                groupDocument.put("reminderIds",groupReminderIds);
                Util.groupCollection.updateOne(group,groupDocument).addOnCompleteListener(updateData -> {
                    if(updateData.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Reminder Added Successfully",Toast.LENGTH_LONG).show();
                    }
                });
            });
        });
    }

    public void clearText(View view) {
        personalReminderTitle.setText(emptyText);
    }

    public void closeActivity(View view) {
        personalReminderInputDialog.dismiss();
    }

    public void onSetLocationPressed(View view, int requestCode) {
        Toast.makeText(context, "starting map activity", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(context, LocationActivity.class), requestCode);
    }

    public void exit(View view) {
        personalReminderInputDialog.dismiss();
    }

    public void createReminder(View view) {
        String reminder = personalReminderTitle.getText().toString();
        if (reminder.isEmpty()) {
            personalReminderTitle.setError(getString(R.string.empty_field_error));
        } else if (personalReminderLocation == null) {
            Toast.makeText(context, R.string.locationEmptyMsg,
                    Toast.LENGTH_LONG).show();
        } else {
            PersonalReminder personalReminder = new PersonalReminder(reminder, personalReminderLocation);
            reminderViewModel.addReminder(personalReminder);
            this.personalReminderLocation = null;
            personalReminderInputDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) {
            return;
        }
        switch (requestCode) {
            case PERSONAL_REMINDER_REQUEST_CODE:
                this.personalReminderLocation = data.getParcelableExtra("location");
                Toast.makeText(context, personalReminderLocation != null ? personalReminderLocation.toString() : "",
                        Toast.LENGTH_LONG).show();
                break;
            case GROUP_REMINDER_REQUEST_CODE:
                this.groupReminderLocation = data.getParcelableExtra("location");
                Toast.makeText(context, groupReminderLocation != null ? groupReminderLocation.toString() : "",
                        Toast.LENGTH_LONG).show();
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

        // set activity_reminder_info.xml to be the layout file of the personalReminderInputDialog builder
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

}