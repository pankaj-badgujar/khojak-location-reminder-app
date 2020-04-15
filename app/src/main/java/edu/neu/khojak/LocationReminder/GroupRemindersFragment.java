package edu.neu.khojak.LocationReminder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import edu.neu.khojak.R;

public class GroupRemindersFragment extends Fragment {

    /* List of Object with Document.
     The Document object is as key value pair having following keys
     _id: id of the group,
     groupName: groupName,
     groupMember: ArrayList with all the group members of this group*/
    private List<Document> data = new ArrayList<>();

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
        return inflater.inflate(R.layout.fragment_group_reminders, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
