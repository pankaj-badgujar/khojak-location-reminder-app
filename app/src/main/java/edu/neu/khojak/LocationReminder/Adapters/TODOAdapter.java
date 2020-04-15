package edu.neu.khojak.LocationReminder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;
import edu.neu.khojak.R;

public class TODOAdapter extends ArrayAdapter<PersonalReminder> {

    private Context activityContext;
    private static class ViewHolder {
        public static TextView TODOTitle;
        public static TextView Location;
    }

    public TODOAdapter(@NonNull Context context, List<PersonalReminder> data) {
        super(context, R.layout.todo_personal_item, data);
        activityContext = context;
    }

    public TODOAdapter(@NonNull Context context) {
        super(context, R.layout.todo_personal_item, new ArrayList<PersonalReminder>());
        activityContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PersonalReminder personalReminder = getItem(position);
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.todo_personal_item, parent, false);
            ViewHolder.TODOTitle = convertView.findViewById(R.id.TODOTitle);
            ViewHolder.Location = convertView.findViewById(R.id.Location);
        }
        ViewHolder.TODOTitle.setText(personalReminder.getTitle());
        ViewHolder.Location.setText(personalReminder.getLocation()
                .toString());
        return convertView;
    }
}
