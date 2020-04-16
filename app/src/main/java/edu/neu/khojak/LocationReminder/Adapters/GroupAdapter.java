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

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{

    private List<Document> groups;
    private Context context;
    private GroupAdapter.OnGroupClickListener listener;


    public GroupAdapter(Context context, List<Document> groups){
        this.context = context;
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.reminder_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Document group = groups.get(position);
        if(group == null) {
            groups.clear();
            return;
        }
        String titleText = "Group : "+ group.get("groupName").toString();
        String countText = "Group members : " + ((List) group.get("groupMembers")).size();
        holder.groupTitle.setText(titleText);
        holder.groupMemberCount.setText(countText);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public Document getGroupAt(int position) {
        return groups.get(position);
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder{

        TextView groupTitle;
        TextView groupMemberCount;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupTitle = itemView.findViewById(R.id.customListItemTitle);
            groupMemberCount = itemView.findViewById(R.id.customListItemSubTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onGroupClick(groups.get(position));
                    }

                }
            });
        }
    }

    public interface OnGroupClickListener {
        void onGroupClick(Document group);
    }

    public void setOnItemClickListener(GroupAdapter.OnGroupClickListener listener) {
        this.listener = listener;
    }
}
