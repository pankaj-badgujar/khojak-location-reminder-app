package edu.neu.khojak.LocationReminder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        usernamesList.add("pankaj");
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
        }
        usernamesList.add(usernameToBeAdded);
        arrayAdapter.notifyDataSetChanged();
        Toast.makeText(this, usernameToBeAdded+" added", Toast.LENGTH_SHORT).show();
        groupMemberName.setText("");
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
            Toast.makeText(this,"Group created", Toast.LENGTH_LONG).show();
            JSONObject jsonObject = new JSONObject();
            JSONArray members = new JSONArray();
            usernamesList.forEach(username -> members.put(username) );
            try {
                jsonObject.put("groupName",groupNameString);
                jsonObject.put("groupMembers", members);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(jsonObject.toString());
            finish();
        }

    }


}
