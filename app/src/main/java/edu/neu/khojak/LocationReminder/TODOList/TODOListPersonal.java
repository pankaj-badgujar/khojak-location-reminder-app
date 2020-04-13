package edu.neu.khojak.LocationReminder.TODOList;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.function.Consumer;

import edu.neu.khojak.LocationReminder.Adapters.TODOAdapter;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;
import edu.neu.khojak.R;

public class TODOListPersonal extends AppCompatActivity {

    private static TODOAdapter adapter;
    private ListView listView;
    private final static int REQUEST_CODE_1 = 1;
    private final Context context = this;
    private AlertDialog inputDialog;
    private EditText reminderTitle;
    private Location location;
    private final static String emptyText = "";
    private final static String errorText = "This field cannot be empty.";

    public static void addToList(List<PersonalReminder> data) {
        adapter.addAll(data);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_personal);

        //Consumer to call addToList Function for the data fetched.
        Consumer<List<PersonalReminder>> function = TODOListPersonal::addToList;
        (new FetchTask(this, function)).execute();
        adapter = new TODOAdapter(this);
        listView = findViewById(R.id.personal_todo_list);
        listView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.new_todo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get activity_reminder_info.xml as prompt
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                final View parentView = view;
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
            }
        });
    }

    public void clearText(View view) {
        reminderTitle.setText(emptyText);
    }


    public void onSetLocationPressed(View view) {
        Toast.makeText(this, "starting map activity", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(this, LocationActivity.class), REQUEST_CODE_1);
    }

    public void exit(View view) {
        inputDialog.dismiss();
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

    public void closeActivity(View view) {
        inputDialog.dismiss();
    }

    public void createReminder(View view) {
        String reminder = reminderTitle.getText().toString();
        if (reminder.isEmpty()) {
            reminderTitle.setError(errorText);
        } else if (location == null) {
            Toast.makeText(this, " Location cannot be empty.",
                    Toast.LENGTH_LONG).show();
        } else {
            PersonalReminder personalReminder = new PersonalReminder(reminder, location);
            adapter.add(personalReminder);
            (new InsertTask(this,personalReminder)).execute();
            this.location = null;
            inputDialog.dismiss();
        }
    }
}
