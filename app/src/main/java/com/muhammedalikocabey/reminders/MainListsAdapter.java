package com.muhammedalikocabey.reminders;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MainListsAdapter extends RecyclerView.Adapter<MainListsAdapter.mainListsCardViewHolder> {

    private Context context;

    private List<ReminderList> listOfReminderList;

    private ReminderDatabase reminderDB;
    private ReminderDao reminderDao;


    public MainListsAdapter(Context context, List<ReminderList> listOfReminderList) {
        this.context = context;
        this.listOfReminderList = listOfReminderList;
        this.reminderDB = ReminderDatabase.getDatabase(context);
        this.reminderDao = reminderDB.reminderDao();
    }


    public class mainListsCardViewHolder extends RecyclerView.ViewHolder {
        public CardView listsCardView;
        public Button listCardButton;
        public TextView listTextView;

        public mainListsCardViewHolder(@NonNull View itemView) {
            super(itemView);
            this.listsCardView = itemView.findViewById(R.id.lists_CardView);
            this.listCardButton = itemView.findViewById(R.id.lists_card_Button);
            this.listTextView = itemView.findViewById(R.id.lists_card_TextView);
        }
    }


    @NonNull
    @Override
    public mainListsCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_lists_card_design, parent, false);

        return new mainListsCardViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull mainListsCardViewHolder holder, int position) {

        ReminderList currentReminderList = listOfReminderList.get(position);

        String reminderListName = currentReminderList.getListName();
        String reminderListElementCount = String.valueOf(reminderDao.getReminderCountByListId(currentReminderList.getListId()));


        holder.listCardButton.setText(reminderListName);
        holder.listTextView.setText(reminderListElementCount);

        holder.listCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToListOfRemindersActivity = new Intent(context, ListRemindersActivity.class);

                goToListOfRemindersActivity.putExtra("from", "reminderList");
                goToListOfRemindersActivity.putExtra("ListId", currentReminderList.getListId());
                context.startActivity(goToListOfRemindersActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfReminderList.size();
    }
}
