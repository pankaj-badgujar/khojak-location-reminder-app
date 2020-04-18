package edu.neu.khojak.LocationTracker;

import android.os.Bundle;
import android.widget.TextView;

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
import es.dmoral.toasty.Toasty;

public class PendingRequests extends AppCompatActivity {

    private PendingRequestsAdapter pendingRequestsAdapter;
    private EmptyRecyclerView pendingRequestsRecyclerView;
    private List<String> pendingRequests;
    private TextView noPendingRequestMsg;

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

        noPendingRequestMsg = findViewById(R.id.noPendingRequestMsg);
        pendingRequestsRecyclerView.setEmptyView(noPendingRequestMsg);

        new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String requestToActUpon = pendingRequests.get(viewHolder.getAdapterPosition());
                switch (direction){

                    // left case
                    //TODO: update position after the request above it is deleted (Array Index out of bound)
                    case 4:
                        Util.deletePendingRequest(pendingRequestsAdapter, requestToActUpon);
                        Toasty.error(PendingRequests.this,"Pending request deleted", Toasty.LENGTH_SHORT).show();
                        break;

                    // right case
                    case 8:
                        Util.acceptPendingRequest(pendingRequestsAdapter, requestToActUpon);
                        Toasty.success(PendingRequests.this,"Accepted! "+requestToActUpon+" is now a friend", Toasty.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }).attachToRecyclerView(pendingRequestsRecyclerView);

    }

}
