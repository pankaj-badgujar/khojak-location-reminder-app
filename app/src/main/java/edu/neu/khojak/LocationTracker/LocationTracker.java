package edu.neu.khojak.LocationTracker;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import edu.neu.khojak.LocationReminder.TODOList.LocationActivity;
import edu.neu.khojak.LocationReminder.Util;
import edu.neu.khojak.R;

public class LocationTracker extends AppCompatActivity {

    private final int LOCATION_TRACKER_DESTINATION_REQUEST_CODE = 1;
    private Location destinationLocation;
    private EditText userToBeTracked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userToBeTracked = findViewById(R.id.userToBeTracked);
    }

    public void setDestinationLocation(View view){
        Intent intent = new Intent(this, LocationActivity.class);
        startActivityForResult(intent, LOCATION_TRACKER_DESTINATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case LOCATION_TRACKER_DESTINATION_REQUEST_CODE:
                if(data != null) {
                    this.destinationLocation = data.getParcelableExtra("location");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    public void saveDetailsAndStartTracking(View view){
        String userToTrack = userToBeTracked.getText().toString();
        if (userToTrack.isEmpty()) {
            userToBeTracked.setError(getString(R.string.empty_field_error));
        } else if (destinationLocation== null) {
            Toast.makeText(this, "Location cannot be empty",
                    Toast.LENGTH_LONG).show();
        } else {
            // TODO: create tracker
            userToBeTracked.setText("");
            Document document = new Document();
            document.append("username", userToTrack);
            Util.userCollection.findOne(document).addOnCompleteListener(task -> {
                if(!task.isSuccessful() && task.getResult() == null) {
                    userToBeTracked.setError("Username is not correct");
                    return;
                }
                addTrackingDetails(userToTrack);
            });
        }
    }

    private void addTrackingDetails(String userToTrack) {
        Document trackingObject = new Document();
        trackingObject.append("user",userToTrack);
        trackingObject.put("latitude",destinationLocation.getLatitude());
        trackingObject.put("longitude",destinationLocation.getLongitude());
        Util.trackingCollection.insertOne(trackingObject).addOnCompleteListener(insertTask -> {
            if(!insertTask.isSuccessful()) {
                return;
            }
            Util.userCollection.findOne(new Document("username",Util.userName))
                    .addOnCompleteListener(fetchTask -> {
                        if(!fetchTask.isSuccessful() && fetchTask.getResult() == null){
                            return;
                        }
                        Document document = fetchTask.getResult();
                        List<String> _ids = document.get("trackingIds") == null ? new ArrayList<>() :
                                (List<String>) document.get("trackingIds");
                        _ids.add(Util.getId(insertTask.getResult().getInsertedId()));
                        document.remove("trackingIds");
                        document.append("trackingIds",_ids);
                        Util.userCollection.updateOne(new Document("username",Util.userName),document)
                                .addOnCompleteListener( updateTask -> {
                            if(updateTask.isSuccessful()) {
                                //Do something
                            }
                        });
            });
        });
    }
}
