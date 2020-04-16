package edu.neu.khojak.LocationReminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.bson.Document;

import java.util.List;
import java.util.Map;

import edu.neu.khojak.R;

public class GroupDetails extends AppCompatActivity {

    private ListView reminderList;
    private ArrayAdapter arrayAdapter;
    private List<String> reminderIds;
    private List<Document> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        reminderList = findViewById(R.id.listOfReminders);
        Intent intent = getIntent();
        Map document = (Map) intent.getSerializableExtra("group");

        /** Code to populate reminders by fetching data from database **/


//        reminderIds = document.get("reminders") == null ? new ArrayList<>() :
//                (List<String>) document.get("reminders");
//
//        reminderIds.forEach(id -> {
//            Util.reminderCollection.findOne(new Document("_id",new ObjectId(id))).addOnCompleteListener(fetchTask -> {
//                if(fetchTask.isSuccessful() && fetchTask.getResult() != null) {
//                    reminders.add(fetchTask.getResult());
//                }
//            });
//        });

        //TODO: If possible create a custom adapter for Document.
//        arrayAdapter  = new ArrayAdapter(this,android.R.layout.simple_list_item_1, reminders.stream().map(data ->
//            ((String) data.get("title"))).collect(Collectors.toList()));
//
//        reminderList.setAdapter(arrayAdapter);
//
//        reminderList.setOnItemClickListener((adapterView, view, i, l) -> {
//            Intent nextActivity = new Intent(this, ReminderLocationView.class);
//            nextActivity.putExtra("reminder",new PersonalReminder(reminders.get(i)));
//        });

    }

}
