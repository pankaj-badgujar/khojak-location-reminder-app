package edu.neu.khojak.LocationTracker;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.khojak.LocationReminder.EmptyRecyclerView;
import edu.neu.khojak.LocationReminder.Util;
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

        new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                switch (direction){
                    // left case
                    case 4:
                        Util.deletePendingRequest(PendingRequests.this, viewHolder.getAdapterPosition());
                        break;
                    // right case
                    case 8:
                        acceptPendingRequest();
                        Toast.makeText(PendingRequests.this, "Pending request accepted", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:
                        break;
                }



            }
        }).attachToRecyclerView(pendingRequestsRecyclerView);

    }

    private void acceptPendingRequest() {
    }


}
