package edu.neu.khojak.LocationReminder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.khojak.LocationReminder.Adapters.ReminderAdapter;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;
import edu.neu.khojak.LocationReminder.TODOList.ReminderLocationView;
import edu.neu.khojak.LocationReminder.ViewModel.ReminderViewModel;
import edu.neu.khojak.R;
import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalRemindersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalRemindersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalRemindersFragment extends Fragment {
    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    private View v;
    private EmptyRecyclerView recyclerView;
    private ReminderViewModel reminderViewModel;

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView noRemindersMsg;

    public PersonalRemindersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalRemindersFragment.
     */
    // Rename and change types and number of parameters
    public static PersonalRemindersFragment newInstance(String param1, String param2) {
        PersonalRemindersFragment fragment = new PersonalRemindersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        v = inflater.inflate(R.layout.fragment_personal_reminders, container, false);

        final ReminderAdapter adapter = new ReminderAdapter();

        recyclerView = v.findViewById(R.id.recycler_view);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        noRemindersMsg = v.findViewById(R.id.noReminderSetMsg);
        recyclerView.setEmptyView(noRemindersMsg);

        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderViewModel.getAllReminders().observe(getViewLifecycleOwner(), reminders -> adapter.setReminders(reminders));


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                PersonalReminder reminder = adapter.getLinkAt(viewHolder.getAdapterPosition());
                reminderViewModel.deleteReminder(reminder);

                Toasty.error(getContext(), "Reminder deleted", Toast.LENGTH_SHORT)
                        .show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(reminder -> {
            Intent intent = new Intent(getContext(), ReminderLocationView.class);
            intent.putExtra("reminder",reminder);
            startActivity(intent);
        });

        return v;
    }


    // Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // Update argument type and name
        void onFragmentInteraction(Uri uri  );
    }
}
