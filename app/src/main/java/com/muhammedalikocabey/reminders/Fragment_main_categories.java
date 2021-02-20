package com.muhammedalikocabey.reminders;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Fragment_main_categories extends Fragment {

    private ConstraintLayout mainConstraintLayout;

    private ConstraintLayout todayConstraint;
    private TextView todayDay;
    private TextView todayCount;

    private ConstraintLayout timedConstraint;
    private TextView timedCount;

    private ConstraintLayout allOfConstraint;
    private TextView allOfCount;

    private ConstraintLayout flaggedConstraint;
    private TextView flaggedCount;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.main_reminder_categories, container, false);


        ReminderDatabase db = ReminderDatabase.getDatabase(rootView.getContext());
        ReminderDao reminderDao = db.reminderDao();


        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        mainConstraintLayout = rootView.findViewById(R.id.ConstraintLayout);


        todayConstraint = rootView.findViewById(R.id.today_constraint);
        todayDay = rootView.findViewById(R.id.today_icon);
        todayDay.setText(currentDate.split("-")[0]);
        todayCount = rootView.findViewById(R.id.today_count);
        todayCount.setText(String.valueOf(reminderDao.getRemindersCreatedTodayCount(currentDate)));


        timedConstraint = rootView.findViewById(R.id.timed_constraint);
        timedCount = rootView.findViewById(R.id.timed_count);
        timedCount.setText(String.valueOf(reminderDao.getTimedRemindersCount()));


        allOfConstraint = rootView.findViewById(R.id.all_of_constraint);
        allOfCount = rootView.findViewById(R.id.all_of_count);
        allOfCount.setText(String.valueOf(reminderDao.getAllRemindersCount()));


        flaggedConstraint = rootView.findViewById(R.id.flagged_constraint);
        flaggedCount = rootView.findViewById(R.id.flagged_count);
        flaggedCount.setText(String.valueOf(reminderDao.getFlaggedRemindersCount()));



        todayConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToListOfRemindersActivity = new Intent(getActivity(), ListRemindersActivity.class);

                goToListOfRemindersActivity.putExtra("from", getActivity().getString(R.string.text_today));
                startActivity(goToListOfRemindersActivity);
            }
        });


        timedConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToListOfRemindersActivity = new Intent(getActivity(), ListRemindersActivity.class);

                goToListOfRemindersActivity.putExtra("from", getActivity().getString(R.string.text_timed));
                startActivity(goToListOfRemindersActivity);
            }
        });


        allOfConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToListOfRemindersActivity = new Intent(getActivity(), ListRemindersActivity.class);

                goToListOfRemindersActivity.putExtra("from", getActivity().getString(R.string.text_allOf));
                startActivity(goToListOfRemindersActivity);
            }
        });


        flaggedConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToListOfRemindersActivity = new Intent(getActivity(), ListRemindersActivity.class);

                goToListOfRemindersActivity.putExtra("from", getActivity().getString(R.string.text_flagged));
                startActivity(goToListOfRemindersActivity);
            }
        });


        return rootView;
    }
}