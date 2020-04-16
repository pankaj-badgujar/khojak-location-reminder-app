package edu.neu.khojak.LocationReminder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import edu.neu.khojak.R;

public class CreateGroup extends AppCompatActivity {

    private EditText groupMemberName;
    private EditText groupName;
    private ArrayList<String> usernamesList;
    private ListView groupMemberList;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        usernamesList = new ArrayList<>();
        usernamesList.add(Util.userName);
        groupName = findViewById(R.id.groupTitle);
        groupMemberName = findViewById(R.id.newMemberEditText);
        groupMemberList = findViewById(R.id.listOfGroupMembers);

        //ArrayAdapter for member list
        arrayAdapter =
                new ArrayAdapter(this,android.R.layout.simple_list_item_1, usernamesList);

        groupMemberList.setAdapter(arrayAdapter);

        groupMemberList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String usernameToBeRemoved = usernamesList.get(position);
                 new AlertDialog
                         .Builder(CreateGroup.this)
                         .setMessage("Remove "+ usernameToBeRemoved)
                         .setCancelable(false)
                         .setTitle("Delete confirmation")
                         .setNegativeButton("No", null)
                         .setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 usernamesList.remove(usernameToBeRemoved);
                                 arrayAdapter.notifyDataSetChanged();
                                 Toast.makeText(CreateGroup.this,usernameToBeRemoved +" deleted",
                                         Toast.LENGTH_SHORT).show();
                             }
                         }).show();

                 return true;
            }
        });

    }

    public void addGroupMember(View view){
        String usernameToBeAdded = groupMemberName.getText().toString();
        if(usernameToBeAdded.trim().isEmpty()){
            groupMemberName.setError(getString(R.string.empty_field_error));
        } else if (usernamesList.contains(usernameToBeAdded)) {
            Toast.makeText(this, usernameToBeAdded +" already in the group",
                    Toast.LENGTH_SHORT).show();
        } else{
            authenticateUser(usernameToBeAdded);
        }
    }

    private void authenticateUser(String userName) {

        Document document = new Document("username", userName);
        AtomicReference<Task<Document>> fetch = new AtomicReference<>();
        Util.stitchUserTask.addOnCompleteListener(task -> {
            fetch.set(Util.userCollection.findOne(document));
            fetch.get().addOnCompleteListener(fetchTask -> {
                if (fetchTask.isSuccessful() && fetchTask.getResult() != null) {
                    usernamesList.add(userName);
                    arrayAdapter.notifyDataSetChanged();
                    groupMemberName.setText("");
                    Toast.makeText(this, userName +" added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, userName +" does not exist",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void saveDetailsAndCreateGroup(View view){
        String groupNameString = groupName.getText().toString();
        if(groupNameString.trim().isEmpty()){
            groupName.setError(getString(R.string.empty_field_error));
        }
        else if(usernamesList.size()<2){
            Toast.makeText(this, "Group should have minimum 2 members",
                    Toast.LENGTH_LONG).show();
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("groupName",groupNameString);
            map.put("groupMembers",usernamesList);
            addGroup(map);
        }

    }

    private void addGroup(Map data) {
        Util.stitchUserTask.addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Document document = new Document(data);
                Util.groupCollection.insertOne(document).addOnCompleteListener( insertTask -> {
                            if (insertTask.isSuccessful()) {
                                Toast.makeText(this,"Group created", Toast.LENGTH_LONG).show();
                                List<String> users = (List<String>) data.get("groupMembers");
                                users.forEach(user -> {
                                    Util.userCollection.findOne(new Document("username",user))
                                            .addOnCompleteListener(databaseUser -> {
                                                if(databaseUser.isSuccessful()) {
                                                    Document updatedUser = databaseUser.getResult();
                                                    Object groupIds = updatedUser
                                                            .get("groupIds");
                                                    if(groupIds == null) {
                                                        groupIds = new ArrayList<String>();
                                                    }
                                                    ((List<String>) groupIds).add(Util.getId(insertTask.
                                                            getResult().getInsertedId()));
                                                    updatedUser.put("groupIds",groupIds);
                                                    Util.userCollection.updateOne(new Document("username",user), updatedUser).addOnCompleteListener(updateTask -> {
                                                        Intent returnIntent = new Intent();
                                                        if(updateTask.isSuccessful()) {
                                                            Toast.makeText(getApplicationContext(),"user updated",Toast.LENGTH_LONG).show();
                                                            setResult(Activity.RESULT_OK,returnIntent);
                                                        } else{
                                                            setResult(Activity.RESULT_CANCELED,returnIntent);
                                                        }
                                                        finish();
                                                    });
                                                }
                                            });
                                });
                            }
                        }
                );
            }
        });
    }


}
