package com.example.khojak.TODOList;

import android.location.Location;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.khojak.Adapters.TODOAdapter;
import com.example.khojak.POJO.PersonalReminder;
import com.example.khojak.R;

public class TODOListPersonal extends AppCompatActivity {

    private TODOAdapter adapter;
    private ListView listView;

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
    }
}
