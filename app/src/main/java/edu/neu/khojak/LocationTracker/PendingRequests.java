package edu.neu.khojak.LocationTracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import edu.neu.khojak.LocationReminder.EmptyRecyclerView;
import edu.neu.khojak.LocationTracker.adapters.PendingRequestsAdapter;
import edu.neu.khojak.R;

public class PendingRequests extends AppCompatActivity {

    private PendingRequestsAdapter pendingRequestsAdapter;
    private EmptyRecyclerView pendingRequestsRecyclerView;
    private List<String> pendingRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pendingRequests = getIntent().getExtras().getStringArrayList("pendingRequests");


        pendingRequestsAdapter = new PendingRequestsAdapter(this, pendingRequests);

        pendingRequestsRecyclerView = findViewById(R.id.pendingRequestsRecyclerView);
        pendingRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendingRequestsRecyclerView.setHasFixedSize(true);

        pendingRequestsRecyclerView.setAdapter(pendingRequestsAdapter);
        pendingRequestsAdapter.notifyDataSetChanged();

    }

}
