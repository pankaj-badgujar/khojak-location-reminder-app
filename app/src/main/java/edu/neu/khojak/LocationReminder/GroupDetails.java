package edu.neu.khojak.LocationReminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import edu.neu.khojak.LocationReminder.Adapters.GroupReminderAdapter;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;
import edu.neu.khojak.LocationReminder.TODOList.ReminderLocationView;
import edu.neu.khojak.R;
import es.dmoral.toasty.Toasty;

public class GroupDetails extends AppCompatActivity {

    private List<Document> reminders = new ArrayList<>();
    private EmptyRecyclerView groupReminderRecyclerView;
    private GroupReminderAdapter groupReminderAdapter;
    private TextView groupNameTitle;
    private TextView groupMemberCount;
    private TextView noReminderTextView;

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

        groupReminderAdapter = new GroupReminderAdapter(this, reminders);
        groupReminderRecyclerView.setAdapter(groupReminderAdapter);



        noReminderTextView = findViewById(R.id.noReminderSetMsgForGroup);
        groupReminderRecyclerView.setEmptyView(noReminderTextView);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Document reminder = groupReminderAdapter.getGroupAt(viewHolder.getAdapterPosition());
                reminders.remove(reminder);
                Util.removeReminder(reminder);
                groupReminderAdapter.notifyDataSetChanged();
                Toasty.error(getApplicationContext(), "Reminder deleted", Toast.LENGTH_SHORT)
                        .show();
            }
        }).attachToRecyclerView(groupReminderRecyclerView);

        groupReminderAdapter.setOnItemClickListener(data -> {
            Intent locationActivity = new Intent(this, ReminderLocationView.class);
            locationActivity.putExtra("reminder", new PersonalReminder(data));
            startActivity(locationActivity);
        });
        Util.groupCollection.findOne(new Document("_id",document.get("_id"))).addOnCompleteListener(groupObjectFetch -> {
           if(groupObjectFetch.isSuccessful() && groupObjectFetch.getResult() != null) {
               Object object =
                       groupObjectFetch.getResult().get("reminderIds");
               if( object == null) {
                   return;
               }
               fetchReminders((List<String>) object);
           }
        });
    }

    private void fetchReminders(List<String> reminderIds) {
        List<ObjectId> objectReminderIds = reminderIds.stream().map(ObjectId::new).
                collect(Collectors.toList());
        Document ids = new Document("$in",objectReminderIds);
        Document query = new Document("_id",ids);
        RemoteFindIterable<Document> data = Util.reminderCollection.find(query);
        AtomicBoolean isFetchCalledOnce = new AtomicBoolean(false);
        data.into(new ArrayList<>()).addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null) {
                List<Document> remoteReminders = task.getResult();
                remoteReminders.forEach(remoteReminder -> {
                    if(reminders.stream().anyMatch(reminder -> reminder.get("_id").
                            equals(remoteReminder.get("_id")))) {
                        return;
                    }
                    reminders.add(remoteReminder);
                    if(!isFetchCalledOnce.get()){
                        Util.fetchData();
                        isFetchCalledOnce.set(true);
                    }
                    groupReminderAdapter.notifyDataSetChanged();
                });
            }
        });
    }

}
