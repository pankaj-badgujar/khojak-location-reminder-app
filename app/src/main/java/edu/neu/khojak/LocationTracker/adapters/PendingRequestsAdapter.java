package edu.neu.khojak.LocationTracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.khojak.R;

public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.PendingRequestViewHolder> {


    List<String> pendingRequests;
    Context context;

    public PendingRequestsAdapter(Context context, List<String> data){
        this.context = context;
        this.pendingRequests = data;
    }

    @NonNull
    @Override
    public PendingRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.reminder_item,parent,false);
        return new PendingRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingRequestViewHolder holder, int position) {
        holder.prTitle.setText(pendingRequests.get(position));
    }

    @Override
    public int getItemCount() {
        return pendingRequests.size();
    }

    public class PendingRequestViewHolder extends RecyclerView.ViewHolder{
        TextView prTitle;
        public PendingRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            prTitle = itemView.findViewById(R.id.customListItemTitle);
        }
    }
}
