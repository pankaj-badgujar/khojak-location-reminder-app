package edu.neu.khojak.LocationReminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import edu.neu.khojak.LocationReminder.Adapters.GroupAdapter;
import edu.neu.khojak.R;

public class GroupRemindersFragment extends Fragment {
    private View v;
    private TextView noGroupMsg;
    private EmptyRecyclerView groupRecyclerView;
    public static GroupAdapter groupAdapter;
    private Button createGroupBtn;
    private final int LAUNCH_CREATE_GROUP_REQUEST_CODE = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_group_reminders, container, false);


        groupRecyclerView = v.findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupRecyclerView.setHasFixedSize(true);


        //attach adapter to
        groupAdapter = new GroupAdapter(getContext(), Util.groupData);
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
                Util.groupData.remove(group);
                removeData(group);
                groupAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Group "+group.get("groupName").toString()+" deleted", Toast.LENGTH_SHORT)
                        .show();
            }
        }).attachToRecyclerView(groupRecyclerView);

        groupAdapter.setOnItemClickListener(group -> {
            Intent intent = new Intent(getContext(), GroupDetails.class);
            intent.putExtra("group",group);
            startActivity(intent);
        });

        //setting up 'no group created' message when no groups present
        noGroupMsg = v.findViewById(R.id.noGroupsCreatedMsg);
        groupRecyclerView.setEmptyView(noGroupMsg);
        createGroupBtn = v.findViewById(R.id.createGroupBtn);
        createGroupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateGroup.class);
            startActivityForResult(intent, LAUNCH_CREATE_GROUP_REQUEST_CODE);
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LAUNCH_CREATE_GROUP_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Util.fetchData();
        }
    }

    private void removeData(Document group) {
        Util.groupCollection.deleteOne(group).addOnCompleteListener(deleteTask -> {
            if(!deleteTask.isSuccessful()){
                Util.groupData.add(group);
                groupAdapter.notifyDataSetChanged();
                return;
            }
            ((List<String>) group.get("groupMembers")).forEach(user -> {
                Util.userCollection.findOne(new Document("username",user))
                        .addOnCompleteListener(fetchTask -> {
                            if(fetchTask.isSuccessful()){
                                Document document = fetchTask.getResult();
                                List<String> data = (List<String>) document.get("groupIds");
                                data.remove(group.get("_id").toString());
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

}
