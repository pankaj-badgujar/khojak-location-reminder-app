package edu.neu.khojak.LocationTracker;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.AsyncChangeStream;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import edu.neu.khojak.LocationReminder.TODOList.LocationActivity;
import edu.neu.khojak.LocationReminder.Util;
import edu.neu.khojak.R;
import es.dmoral.toasty.Toasty;

import static edu.neu.khojak.Constants.FRIEND_LIST;
import static edu.neu.khojak.Constants.PENDING_REQUESTS;
import static edu.neu.khojak.Constants.USERNAME;

public class LocationTracker extends AppCompatActivity {

    private final int LOCATION_TRACKER_DESTINATION_REQUEST_CODE = 1;
    private Location destinationLocation;
    private Spinner friendSpinner;
    private EditText userToSendTrackRequest;
    public static ArrayAdapter<String> spinnerDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Util.userCollection.watch().addOnCompleteListener(runnable -> {
            if(runnable.isSuccessful()) {
                AsyncChangeStream<Document, ChangeEvent<Document>> asyncChangeStream = runnable.getResult();
                asyncChangeStream.addChangeEventListener( (data, event) -> {
                    Util.fetchFriendList();
                });
            }
        });

        //initializing friendRequest spinner
        spinnerDataAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Util.friendList);
        spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        friendSpinner = findViewById(R.id.userToBeTracked);
        friendSpinner.setAdapter(spinnerDataAdapter);

        userToSendTrackRequest = findViewById(R.id.userToSendTrackRequest);
    }

    public void setDestinationLocation(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivityForResult(intent, LOCATION_TRACKER_DESTINATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case LOCATION_TRACKER_DESTINATION_REQUEST_CODE:
                if (data != null) {
                    this.destinationLocation = data.getParcelableExtra("location");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    public void saveDetailsAndStartTracking(View view) {
        if (friendSpinner.getSelectedItem() == null) {
            Toasty.error(this, "No friends to track", Toasty.LENGTH_SHORT).show();
        } else if (destinationLocation == null) {
            Toasty.error(this, getString(R.string.locationEmptyMsg),
                    Toast.LENGTH_SHORT).show();
        } else {
            addTrackingDetails(friendSpinner.getSelectedItem().toString());
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        spinnerDataAdapter.notifyDataSetChanged();
    }

    public void sendTrackingRequestPressed(View view) {

        String requestee = userToSendTrackRequest.getText().toString();
        if (requestee.trim().isEmpty()) {
            userToSendTrackRequest.setError(getString(R.string.empty_field_error));

        } else if (requestee.trim().equals(Util.userName)) {
            Toasty.info(getApplicationContext(), "Cannot send request to own self", Toasty.LENGTH_SHORT).show();
        } else {
            checkIfUserExist(requestee);
        }

    }

    private void sendTrackingRequestToUser(String requestee) {

        Util.stitchUserTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                //first check if requestee is already a friend
                Util.userCollection.findOne(new Document(USERNAME, Util.userName)).addOnCompleteListener(findMe -> {
                    if (findMe.isSuccessful()) {
                        Document myDocument = findMe.getResult();
                        Object friendListObject = myDocument.get(FRIEND_LIST);

                        if (friendListObject == null) {
                            friendListObject = new ArrayList<String>();
                        }

                        List<String> friendListCasted = (List<String>) friendListObject;
                        if (friendListCasted.contains(requestee)) {
                            Toasty.warning(getApplicationContext(),
                                    "user " + requestee + " is already a friend !", Toast.LENGTH_SHORT).show();
                        }
                        // if not friend, then check if we already sent him friend request
                        else {
                            Util.userCollection.findOne(new Document(USERNAME, requestee))
                                    .addOnCompleteListener(databaseUser -> {
                                        if (databaseUser.isSuccessful()) {
                                            Document updatedUser = databaseUser.getResult();
                                            Object pendingRequests = updatedUser
                                                    .get(PENDING_REQUESTS);


                                            if (pendingRequests == null) {
                                                pendingRequests = new ArrayList<String>();
                                            }
                                            List<String> pendingRequestsCasted = (List<String>) pendingRequests;
                                            if (pendingRequestsCasted.contains(Util.userName)) {
                                                Toasty.warning(getApplicationContext(), "friend request already sent", Toast.LENGTH_SHORT).show();
                                            }

                                            //if not sent, then send him friend request
                                            else {
                                                pendingRequestsCasted.add(Util.userName);
                                                updatedUser.put(PENDING_REQUESTS, pendingRequests);

                                                Util.userCollection.updateOne(new Document(USERNAME, requestee), updatedUser)
                                                        .addOnCompleteListener(updateTask -> {
                                                            if (updateTask.isSuccessful()) {
                                                                userToSendTrackRequest.setText("");
                                                                Toasty.success(getApplicationContext(), "friend request sent", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toasty.error(getApplicationContext(), "could not send friend request", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }

                                    });
                        }
                    }
                });

            }
        });
    }


    private void addTrackingDetails(String userToTrack) {
        Document trackingObject = new Document();
        trackingObject.append("user", userToTrack);
        trackingObject.put("latitude", destinationLocation.getLatitude());
        trackingObject.put("longitude", destinationLocation.getLongitude());
        Util.trackingCollection.insertOne(trackingObject).addOnCompleteListener(insertTask -> {
            if (!insertTask.isSuccessful()) {
                return;
            }
            Util.userCollection.findOne(new Document("username", Util.userName))
                    .addOnCompleteListener(fetchTask -> {
                        if (!fetchTask.isSuccessful() || fetchTask.getResult() == null) {
                            return;
                        }
                        Document document = fetchTask.getResult();
                        List<String> _ids = document.get("trackingIds") == null ? new ArrayList<>() :
                                (List<String>) document.get("trackingIds");
                        _ids.add(Util.getId(insertTask.getResult().getInsertedId()));
                        document.remove("trackingIds");
                        document.append("trackingIds", _ids);
                        Util.userCollection.updateOne(new Document("username", Util.userName), document)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toasty.success(this,"Tracking started...",Toasty.LENGTH_SHORT).show();
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

                } else {
                    Toasty.error(this, "user " + userName + " does not exist", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void showPendingRequestsPressed(View view) {
        startActivity(new Intent(this, PendingRequests.class));
    }
}
