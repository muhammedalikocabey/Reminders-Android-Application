package com.muhammedalikocabey.reminders;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class Fragment_main_down_menu extends Fragment {

    private Button newReminderButton;
    private Button newReminderListButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_down_menu, container, false);


        newReminderButton = (Button) rootView.findViewById(R.id.new_reminder_Button);

        newReminderListButton = rootView.findViewById(R.id.new_reminder_list_Button);


        newReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToNewReminderIntent = new Intent(getActivity(), NewReminderActivity.class);
                startActivity(goToNewReminderIntent);
            }
        });


        newReminderListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToNewReminderListIntent = new Intent(getActivity(), NewReminderListActivity.class);
                startActivity(goToNewReminderListIntent);
            }
        });


        return rootView;
    }
}
