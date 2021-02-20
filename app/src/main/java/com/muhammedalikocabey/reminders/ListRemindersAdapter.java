package com.muhammedalikocabey.reminders;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ListRemindersAdapter extends RecyclerView.Adapter<ListRemindersAdapter.listRemindersCardViewHolder> {

    private Context context;

    private List<Reminder> listOfReminder;

    private ReminderDatabase reminderDB;
    private ReminderDao reminderDao;

    private ReminderListDatabase reminderListDB;
    private ReminderListDao reminderListDao;


    private DatabaseReference databaseReferenceReminders;
    private FirebaseUser currentFirebaseUser;
    private String userUid;


    public ListRemindersAdapter(Context context, List<Reminder> listOfReminder) {
        this.context = context;
        this.listOfReminder = listOfReminder;

        this.reminderDB = ReminderDatabase.getDatabase(context);
        this.reminderDao = reminderDB.reminderDao();

        this.reminderListDB = ReminderListDatabase.getDatabase(context);
        this.reminderListDao = reminderListDB.reminderListDao();

        this.databaseReferenceReminders = FirebaseDatabase.getInstance().getReference("reminder");
        this.databaseReferenceReminders.keepSynced(true);
        this.currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.userUid = this.currentFirebaseUser.getUid();
    }



    public class listRemindersCardViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout LinearLayout;
        public CardView remindersCardView;
        public Button editReminderButton;
        public ImageView reminderFlagImageView;
        public TextView reminderPriorityTextView;
        public TextView reminderTitleTextView;
        public TextView reminderDatesTextView;
        public TextView reminderNotesTextView;
        public Button reminderUrlButton;


        public listRemindersCardViewHolder(@NonNull View itemView) {
            super(itemView);
            this.LinearLayout = itemView.findViewById(R.id.main_LinearLayout);
            this.remindersCardView = itemView.findViewById(R.id.reminders_CardView);
            this.editReminderButton = itemView.findViewById(R.id.reminder_edit_Button);
            this.reminderFlagImageView = itemView.findViewById(R.id.reminder_flag_ImageView);
            this.reminderPriorityTextView = itemView.findViewById(R.id.reminder_priority_TextView);
            this.reminderTitleTextView = itemView.findViewById(R.id.reminder_title_TextView);
            this.reminderDatesTextView = itemView.findViewById(R.id.reminder_dates_TextView);
            this.reminderNotesTextView = itemView.findViewById(R.id.reminder_notes_TextView);
            this.reminderUrlButton = itemView.findViewById(R.id.reminder_url_Button);

        }
    }



    public String dateHourDesign(int value) {
        String returnn = String.valueOf(value);

        if (value < 10) {
            returnn = "0" + returnn;
        }

        return returnn;
    }



    @NonNull
    @Override
    public listRemindersCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_reminders_activity_card_design, parent, false);

        return new listRemindersCardViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull listRemindersCardViewHolder holder, int position) {

        Reminder currentReminder = listOfReminder.get(position);

        String reminderTitle = currentReminder.getTitle();
        String reminderPriority = currentReminder.getPriorityLevel();
        String reminderDateStr = "";
        String reminderNotes = currentReminder.getContent();
        String reminderUrl = currentReminder.getUrl();
        boolean isReminderFlagged = currentReminder.isFlagged();
        Date reminderDate = currentReminder.getDate();
        String reminderHourMinute = "";


        if (reminderDate != null) {

            int date = reminderDate.getDate();
            int month = reminderDate.getMonth() + 1;

            int minute = reminderDate.getMinutes();
            int hour = reminderDate.getHours();


            if((minute == 0) && (hour == 0)) {
                reminderDateStr = String.valueOf(dateHourDesign(date)) + "." +
                        String.valueOf(dateHourDesign(month)) + "." + String.valueOf(reminderDate.getYear());
            } else {
                reminderDateStr = String.valueOf(dateHourDesign(date)) + "." +
                        String.valueOf(dateHourDesign(month)) + "." + String.valueOf(reminderDate.getYear()) +
                        " " + dateHourDesign(hour) + ":" + dateHourDesign(minute);
                reminderHourMinute = hour + ":" + minute ;
            }
        }


        holder.reminderTitleTextView.setText(reminderTitle);


        if (reminderPriority.isEmpty()) {
            holder.reminderPriorityTextView.setVisibility(View.GONE);
        } else {
            holder.reminderPriorityTextView.setText(reminderPriority);
        }


        if (reminderDateStr.isEmpty()) {
            holder.reminderDatesTextView.setVisibility(View.GONE);
        } else {

            final Calendar calendar = Calendar.getInstance();

            int currentYear = calendar.get(Calendar.YEAR);

            int currentMonth = calendar.get(Calendar.MONTH);

            int currentDay = calendar.get(Calendar.DATE);

            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

            int currentMinute = calendar.get(Calendar.MINUTE);

            Date currentDate = new Date(currentYear, currentMonth, currentDay, currentHour, currentMinute);
            Date controlDate = reminderDate;

            if(!reminderHourMinute.isEmpty()) {
                String[] parseHourMin = reminderHourMinute.split(":");
                int hour = Integer.parseInt(parseHourMin[0]);
                int minute = Integer.parseInt(parseHourMin[1]);

                controlDate.setHours(hour);
                controlDate.setMinutes(minute);
            }

            if((currentDate.after(controlDate)) || (currentDate.equals(controlDate))) {
                holder.reminderDatesTextView.setTextColor(Color.parseColor("#FF0000"));
            }
            else {
                holder.reminderDatesTextView.setTextColor(Color.parseColor("#757575"));
            }

            holder.reminderDatesTextView.setText(reminderDateStr);
        }

        if (reminderNotes.isEmpty()) {
            holder.reminderNotesTextView.setVisibility(View.GONE);
        } else {
            holder.reminderNotesTextView.setText(reminderNotes);
        }

        if (reminderUrl.isEmpty()) {
            holder.reminderUrlButton.setVisibility(View.GONE);
        } else {
            String reminderUrlStr = reminderUrl;

            if(reminderUrlStr.startsWith("www.")) {
                reminderUrlStr = "http://" + reminderUrlStr;
            }

            Uri reminderUriUrl = Uri.parse(reminderUrlStr);
            reminderUrlStr = reminderUriUrl.getAuthority();

            if(reminderUrlStr == null) {
                reminderUrlStr = reminderUrl;
            }

            reminderUrlStr = reminderUrlStr.replace("http://", "");
            reminderUrlStr = reminderUrlStr.replace("https://", "");
            reminderUrlStr = reminderUrlStr.replace("www.", "");


            holder.reminderUrlButton.setText(reminderUrlStr);
        }

        if (!isReminderFlagged) {
            holder.reminderFlagImageView.setVisibility(View.GONE);
        }

        holder.editReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu reminderEditPopUpMenu = new PopupMenu(context, holder.editReminderButton);
                MenuInflater menuInflater = reminderEditPopUpMenu.getMenuInflater();
                menuInflater.inflate(R.menu.reminder_list_edit_reminder_menu, reminderEditPopUpMenu.getMenu());
                reminderEditPopUpMenu.show();

                reminderEditPopUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.item_edit_details:
                                Intent goToEditReminderActivity = new Intent(context, NewReminderActivity.class);

                                goToEditReminderActivity.putExtra("from", "editReminder");
                                goToEditReminderActivity.putExtra("reminderId", currentReminder.getId());
                                context.startActivity(goToEditReminderActivity);

                                return true;

                            case R.id.item_delete:

                                Intent alarmManagerIntent = new Intent(context, ReminderAlarmReceiver.class);
                                PendingIntent alarmManagerPendingIntent = PendingIntent.getBroadcast(context, (int)currentReminder.getId(), alarmManagerIntent, 0);
                                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                                alarmManager.cancel(alarmManagerPendingIntent);


                                reminderDao.deleteOneReminder(currentReminder);



                                databaseReferenceReminders.child("user-" + userUid).child(currentReminder.getFirebaseId())
                                        .removeValue();




                                holder.remindersCardView.setVisibility(View.GONE);

                                return true;
                        }

                        return false;
                    }
                });
            }
        });



        holder.reminderUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openLinkIntent = new Intent(Intent.ACTION_VIEW);

                Uri openURL = Uri.parse(reminderUrl);

                if (!reminderUrl.startsWith("http://") && !reminderUrl.startsWith("https://")) {
                    openURL = Uri.parse("http://" + reminderUrl);
                }

                openLinkIntent.setData(openURL);
                context.startActivity(openLinkIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listOfReminder.size();
    }
}
