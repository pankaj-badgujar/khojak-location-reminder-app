package com.example.khojak.LocationReminder.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khojak.LocationReminder.POJO.PersonalReminder;
import com.example.khojak.R;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.LinkHolder> {

    private List<PersonalReminder> reminders = new ArrayList<>();
    private OnLinkItemClickListener listener;

    @NonNull
    @Override
    public LinkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);
        return new LinkHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkHolder holder, int position) {
        PersonalReminder currentReminder = reminders.get(position);
        holder.reminderTitle.setText(currentReminder.getTitle());
        holder.reminderLocation.setText(String.format("Latitude: %1$s \nLongitude: %2$s",
                        currentReminder.getLatitude(),
                        currentReminder.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public void setReminders(List<PersonalReminder> reminders) {
        this.reminders = reminders;
        notifyDataSetChanged();
    }

    public PersonalReminder getLinkAt(int position) {
        return reminders.get(position);
    }

    class LinkHolder extends RecyclerView.ViewHolder {
        private TextView reminderTitle;
        private TextView reminderLocation;

        public LinkHolder(@NonNull View itemView) {
            super(itemView);
            reminderTitle = itemView.findViewById(R.id.reminderTitle);
            reminderLocation = itemView.findViewById(R.id.reminderLocation);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (listener != null && position != RecyclerView.NO_POSITION) {
//                        listener.onLinkItemClick(reminders.get(position));
//                    }
//
//                }
//            });
        }
    }

    public interface OnLinkItemClickListener {
        void onLinkItemClick(PersonalReminder reminder);
    }

    public void setOnItemClickListener(OnLinkItemClickListener listener) {
        this.listener = listener;
    }
}
