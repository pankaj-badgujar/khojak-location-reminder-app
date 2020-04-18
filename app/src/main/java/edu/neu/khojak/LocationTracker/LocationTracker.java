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

import com.google.android.gms.tasks.Task;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import edu.neu.khojak.LocationReminder.TODOList.LocationActivity;
import edu.neu.khojak.LocationReminder.Util;
import edu.neu.khojak.R;

public class LocationTracker extends AppCompatActivity {

    private final int LOCATION_TRACKER_DESTINATION_REQUEST_CODE = 1;
    private Location destinationLocation;
    private EditText userToBeTracked;
    private EditText userToSendTrackRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userToBeTracked = findViewById(R.id.userToBeTracked);
        userToSendTrackRequest = findViewById(R.id.userToSendTrackRequest);
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
            userToBeTracked.setText("");
            Document document = new Document();
            document.append("username", userToTrack);
            Util.userCollection.findOne(document).addOnCompleteListener(task -> {
                if(!task.isSuccessful() || task.getResult() == null) {
                    userToBeTracked.setError("Username is not correct");
                    return;
                }
                addTrackingDetails(userToTrack);
            });
        }
    }

    public void sendTrackingRequestPressed(View view){

        String requestee = userToSendTrackRequest.getText().toString();
        if(requestee.trim().isEmpty()){

            userToSendTrackRequest.setError(getString(R.string.empty_field_error));

        } else{
            checkIfUserExist(requestee);
        }

    }

    private void sendTrackingRequestToUser(String requestee) {

        Util.stitchUserTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Util.userCollection.findOne(new Document("username", requestee))
                        .addOnCompleteListener(databaseUser -> {
                            if (databaseUser.isSuccessful()) {
                                Document updatedUser = databaseUser.getResult();
                                Object pendingRequests = updatedUser
                                        .get("pendingRequests");
                                if (pendingRequests == null) {
                                    pendingRequests = new ArrayList<String>();
                                }
                                ((List<String>) pendingRequests).add(Util.userName);
                                updatedUser.put("pendingRequests", pendingRequests);

                                Util.userCollection.updateOne(new Document("username", requestee), updatedUser)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "friend request sent", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "could not send friend request", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
            }
        });

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
                        if(!fetchTask.isSuccessful() || fetchTask.getResult() == null){
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

    private void checkIfUserExist(String userName) {
        Document document = new Document("username", userName);
        AtomicReference<Task<Document>> fetch = new AtomicReference<>();
        Util.stitchUserTask.addOnCompleteListener(task -> {
            fetch.set(Util.userCollection.findOne(document));
            fetch.get().addOnCompleteListener(fetchTask -> {
                if (fetchTask.isSuccessful() && fetchTask.getResult() != null) {

                    sendTrackingRequestToUser(userName);

                } else{
                    Toast.makeText(this,"user " +userName +" does not exist", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void showPendingRequestsPressed(View view) {

        AtomicReference<ArrayList<String>> pendingRequestsFetched = new AtomicReference<>();

        Util.userCollection.findOne(new Document("username", Util.userName))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        Document userDocument = task.getResult();
                        pendingRequestsFetched.set(userDocument.get("pendingRequests") == null
                                ? new ArrayList<>()
                                : (ArrayList<String>)userDocument.get("pendingRequests"));

                        Intent intent = new Intent(this, PendingRequests.class);
                        intent.putStringArrayListExtra("pendingRequests",
                                pendingRequestsFetched.get());
                        startActivity(intent);
                    }
                });

    }
}
