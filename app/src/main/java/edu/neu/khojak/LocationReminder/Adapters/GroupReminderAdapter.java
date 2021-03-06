package edu.neu.khojak.LocationReminder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.bson.Document;

import java.util.List;

import edu.neu.khojak.R;


public class GroupReminderAdapter extends RecyclerView.Adapter<GroupReminderAdapter.GroupReminderViewHolder>{

    private List<Document> groupReminders;
    private Context context;
    private GroupReminderAdapter.OnReminderClickListener listener;



    public  GroupReminderAdapter(Context context, List<Document> groupReminders){
        this.context = context;
        this.groupReminders = groupReminders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.reminder_item, parent, false);
        return new GroupReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupReminderViewHolder holder, int position) {
        Document groupReminder = groupReminders.get(position);

        holder.groupReminderTitle.setText(groupReminder.get("title").toString());

        String latitude = groupReminder.get("latitude").toString();
        String longitude = groupReminder.get("longitude").toString();
        String locationText = String.format("Latitude: %1$s\nLongitude: %2$s",latitude, longitude);
        holder.groupReminderLocation.setText(locationText);

    }

    @Override
    public int getItemCount() {
        return groupReminders.size();
    }

    public Document getGroupAt(int adapterPosition) {
        return groupReminders.get(adapterPosition);
    }

    public interface OnReminderClickListener {
        void onReminderClick(Document reminder);
    }

    public class GroupReminderViewHolder extends RecyclerView.ViewHolder{

        TextView groupReminderTitle;
        TextView groupReminderLocation;

        public GroupReminderViewHolder(@NonNull View itemView) {
            super(itemView);

            groupReminderTitle = itemView.findViewById(R.id.customListItemTitle);
            groupReminderLocation = itemView.findViewById(R.id.customListItemSubTitle);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onReminderClick(groupReminders.get(position));
                }
            });
        }
    }

    public void setOnItemClickListener(GroupReminderAdapter.OnReminderClickListener listener) {
        this.listener = listener;
    }
}
