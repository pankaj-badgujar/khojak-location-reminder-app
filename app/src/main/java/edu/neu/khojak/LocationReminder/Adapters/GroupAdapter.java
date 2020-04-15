package edu.neu.khojak.LocationReminder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.bson.Document;

import java.util.List;

import edu.neu.khojak.R;

public class GroupAdapter extends ArrayAdapter<Document> {

    List<Document> groupList;
    Context context;
    int resource;

    public GroupAdapter(@NonNull Context context, int resource, @NonNull List<Document> objects) {
        super(context, resource, objects);
        this.context = context;
        groupList = objects;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Document group = groupList.get(position);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        TextView groupTitle = convertView.findViewById(R.id.customListItemTitle);
        groupTitle.setText(group.get("groupName").toString());

        return convertView;
    }
}
