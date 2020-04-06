package com.example.khojak.TODOList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.khojak.Adapters.TODOAdapter;
import com.example.khojak.POJO.PersonalReminder;
import com.example.khojak.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class TODOListPersonal extends AppCompatActivity {

    private TODOAdapter adapter;
    private ListView listView;
    private final Context context = this;
    private AlertDialog inputDialog;
    private EditText reminderTitle;
    private final static String emptyText = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_personal);
        Location targetLocation = new Location("");//provider name is unnecessary
        targetLocation.setLatitude(0.0d);//your coords of course
        targetLocation.setLongitude(0.0d);
        PersonalReminder personalReminder = new PersonalReminder("Hey", targetLocation);

        adapter = new TODOAdapter(this);
        listView = findViewById(R.id.personal_todo_list);
        listView.setAdapter(adapter);

        adapter.add(personalReminder);



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

                // set activity_input_promptut_prompt.xml to be the layout file of the inputDialog builder
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


    public void onSetLocationPressed(View view){
        Toast.makeText(this,"starting map activity",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LocationActivity.class));
    }

    public void exit(View view) {
        inputDialog.dismiss();
    }

    public void closeActivity(View view) {
        inputDialog.dismiss();
    }


}
