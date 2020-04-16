package edu.neu.khojak.LocationReminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.neu.khojak.LocationReminder.Adapters.GroupReminderAdapter;
import edu.neu.khojak.R;

public class GroupDetails extends AppCompatActivity {


    private List<String> reminderIds;
    private List<Document> reminders = new ArrayList<>();
    private EmptyRecyclerView groupReminderRecyclerView;
    private GroupReminderAdapter groupReminderAdapter;
    private TextView groupNameTitle;
    private TextView groupMemberCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Map document = (Map) intent.getSerializableExtra("group");

        groupNameTitle = findViewById(R.id.groupTitleInDetails);
        groupMemberCount = findViewById(R.id.groupMemberCountText);

        groupNameTitle.setText(document.get("groupName").toString());
        groupMemberCount.setText("Group members: " + ((List)document.get("groupMembers")).size());


        groupReminderRecyclerView = findViewById(R.id.groupRemindersRecyclerView);
        groupReminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupReminderRecyclerView.setHasFixedSize(true);


        /** Code to populate reminders by fetching data from database **/


        reminderIds = document.get("reminders") == null ? new ArrayList<>() :
                (List<String>) document.get("reminders");

        reminderIds.forEach(id -> {
            Util.reminderCollection.findOne(new Document("_id",new ObjectId(id))).addOnCompleteListener(fetchTask -> {
                if(fetchTask.isSuccessful() && fetchTask.getResult() != null) {
                    reminders.add(fetchTask.getResult());
                }
            });
        });

//        arrayAdapter  = new ArrayAdapter(this,android.R.layout.simple_list_item_1, reminders.stream().map(data ->
//            ((String) data.get("title"))).collect(Collectors.toList()));
//
//        reminderList.setAdapter(arrayAdapter);
//
//        reminderList.setOnItemClickListener((adapterView, view, i, l) -> {
//            Intent nextActivity = new Intent(this, ReminderLocationView.class);
//            nextActivity.putExtra("reminder",new PersonalReminder(reminders.get(i)));
//        });

        List<String> reminderTitles =  reminders.stream().map(data ->
            ((String) data.get("title"))).collect(Collectors.toList());

        groupReminderAdapter = new GroupReminderAdapter(this, reminders);
        groupReminderRecyclerView.setAdapter(groupReminderAdapter);
        groupReminderAdapter.notifyDataSetChanged();

    }

}
