package edu.neu.khojak.LocationReminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import edu.neu.khojak.R;

public class GroupDetails extends AppCompatActivity {

    TextView groupNameTitle;
    TextView groupIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        groupNameTitle = findViewById(R.id.groupTitleInDetails);
        groupIdText = findViewById(R.id.groupIdText);

        Intent intent = getIntent();

        groupNameTitle.setText(intent.getStringExtra("groupName").toString());
        groupIdText.setText(intent.getStringExtra("groupId").toString());

        //TODO: display list of all in simple list view same like in createGroup activity

    }

}
