package edu.neu.khojak.LocationReminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import edu.neu.khojak.LocationReminder.Adapters.GroupAdapter;
import edu.neu.khojak.R;

public class GroupRemindersFragment extends Fragment {

    /* List of Object with Document.
     The Document object is as key value pair having following keys
     _id: id of the group,
     groupName: groupName,
     groupMember: ArrayList with all the group members of this group*/

    //TODO: put notifyDataSetChanged() whenever we are ready with data on startup
    private List<Document> data = new ArrayList<>();
    private View v;
    private TextView noGroupMsg;
    private EmptyRecyclerView groupRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.userCollection.findOne(new Document("username",Util.userName)).addOnCompleteListener(task -> {
            if(! task.isSuccessful()) {
                return;
            }
            Document user = task.getResult();
            Object object = user.get("groupIds");
            if( object == null) {
                return;
            }
            ((List<String>) object).forEach(groupId -> {
                Util.groupCollection.findOne(new Document("_id",new ObjectId(groupId)))
                        .addOnCompleteListener( fetchTask -> {
                            if(fetchTask.isSuccessful()) {
                                data.add(fetchTask.getResult());
                            }
                        });
            });
        });

        v = inflater.inflate(R.layout.fragment_group_reminders, container, false);


        groupRecyclerView = v.findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupRecyclerView.setHasFixedSize(true);


        //attach adapter to
        final GroupAdapter groupAdapter = new GroupAdapter(getContext(), data);
        groupRecyclerView.setAdapter(groupAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Document group = groupAdapter.getGroupAt(viewHolder.getAdapterPosition());


                // TODO: write code to remove group from database
                data.remove(group);
                groupAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Group "+group.get("groupName").toString()+" deleted", Toast.LENGTH_SHORT)
                        .show();
            }
        }).attachToRecyclerView(groupRecyclerView);

        groupAdapter.setOnItemClickListener(new GroupAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClick(Document group) {
                Intent intent = new Intent(getContext(), GroupDetails.class);
                intent.putExtra("groupName",group.get("groupName").toString());
                intent.putExtra("groupId", group.get("_id").toString());

                //TODO: if possible pass entire group object using parcelable class
                startActivity(intent);
            }
        });



        //setting up 'no group created' message when no groups present
        //TODO: just uncomment lines below this after group loading on startup works

//        noGroupMsg = v.findViewById(R.id.noGroupsCreatedMsg);
//        groupRecyclerView.setEmptyView(noGroupMsg);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
