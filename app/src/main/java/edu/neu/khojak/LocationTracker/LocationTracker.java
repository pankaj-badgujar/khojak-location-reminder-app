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

import edu.neu.khojak.LocationReminder.TODOList.LocationActivity;
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
        }

    }
}
