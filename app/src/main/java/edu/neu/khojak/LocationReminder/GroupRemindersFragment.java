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
import java.util.stream.Collectors;

import edu.neu.khojak.LocationReminder.Adapters.GroupAdapter;
import edu.neu.khojak.R;

public class GroupRemindersFragment extends Fragment {

    /* List of Object with Document.
     The Document object is as key value pair having following keys
     _id: id of the group,
     groupName: groupName,
     groupMembers: ArrayList with all the group members of this group*/
    private List<Document> data = new ArrayList<>();
    private View v;
    private TextView noGroupMsg;
    private EmptyRecyclerView groupRecyclerView;
    private GroupAdapter groupAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_group_reminders, container, false);


        groupRecyclerView = v.findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupRecyclerView.setHasFixedSize(true);


        //attach adapter to
        groupAdapter = new GroupAdapter(getContext(), data);
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
                data.remove(group);
                removeData(group);
                groupAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Group "+group.get("groupName").toString()+" deleted", Toast.LENGTH_SHORT)
                        .show();
            }
        }).attachToRecyclerView(groupRecyclerView);

        groupAdapter.setOnItemClickListener(new GroupAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClick(Document group) {
                Intent intent = new Intent(getContext(), GroupDetails.class);
                intent.putExtra("group",group);
                startActivity(intent);
            }
        });

        //setting up 'no group created' message when no groups present

        noGroupMsg = v.findViewById(R.id.noGroupsCreatedMsg);
        groupRecyclerView.setEmptyView(noGroupMsg);
        fetchData(groupAdapter);

        // TODO Add the commented to to the function which is called when activity gets reFocused similar to onResume or onRestart
        // fetchData(groupAdapter)

        // TODO Is it fine if we change the List to a set? if so change it to Set.
        return v;
    }

    private void removeData(Document group) {
        Util.groupCollection.deleteOne(group).addOnCompleteListener(deleteTask -> {
            if(!deleteTask.isSuccessful()){
                data.add(group);
                groupAdapter.notifyDataSetChanged();
                return;
            }
            ((List<String>) group.get("groupMembers")).forEach(user -> {
                Util.userCollection.findOne(new Document("username",user))
                        .addOnCompleteListener(fetchTask -> {
                            if(fetchTask.isSuccessful()){
                                Document document = fetchTask.getResult();
                                List<String> data = (List<String>) document.get("groupIds");
                                data.remove(group.get("_id"));
                                document.remove("groupIds");
                                document.append("groupIds",data);
                                Util.userCollection
                                        .updateOne(new Document("username",user),document)
                                        .addOnCompleteListener(updateTask -> {
                                            if(updateTask.isSuccessful()){
                                                Toast.makeText(getContext(),"User Updated",Toast.LENGTH_LONG);
                                            }
                                        });
                            }
                        });
            });
        });
    }

    private void fetchData(GroupAdapter adapter) {
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
                            if(fetchTask.isSuccessful() && fetchTask.getResult() != null ) {
                                if (data.stream().anyMatch(document ->
                                        document.get("_id") == fetchTask.getResult().get("_id"))) {
                                    return;
                                }
                                data.add(fetchTask.getResult());
                                adapter.notifyDataSetChanged();
                            }
                        });
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
