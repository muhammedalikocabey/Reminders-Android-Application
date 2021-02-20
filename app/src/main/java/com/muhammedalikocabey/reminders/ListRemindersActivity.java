package com.muhammedalikocabey.reminders;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ListRemindersActivity extends AppCompatActivity {

    private Context context = this;

    private Button backToMainButton;
    private Button editButton;
    private TextView reminderCategoryTextView;

    private RecyclerView remindersRecyclerView;
    private ListRemindersAdapter listRemindersAdapter;

    private String activityTitle;

    long listId;

    private Button newReminderButton;
    private Button newReminderListButton;

    private String fromIntentStr = "";


    private FirebaseUser currentFirebaseUser;
    private String userUid;
    private DatabaseReference databaseReferenceReminders;
    private DatabaseReference databaseReferenceReminderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_reminders);


        ReminderListDatabase reminderListDB = ReminderListDatabase.getDatabase(context);
        ReminderListDao reminderListDao = reminderListDB.reminderListDao();

        ReminderDatabase reminderDB = ReminderDatabase.getDatabase(context);
        ReminderDao reminderDao = reminderDB.reminderDao();


        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userUid = currentFirebaseUser.getUid();

        databaseReferenceReminders = FirebaseDatabase.getInstance().getReference("reminder");
        databaseReferenceReminders.keepSynced(true);

        databaseReferenceReminderList = FirebaseDatabase.getInstance().getReference("reminder_list");
        databaseReferenceReminderList.keepSynced(true);


        backToMainButton = findViewById(R.id.back_to_main_Button);

        editButton = findViewById(R.id.edit_Button);
        editButton.setVisibility(View.VISIBLE);

        reminderCategoryTextView = findViewById(R.id.reminders_category_TextView);

        remindersRecyclerView = findViewById(R.id.reminders_RecyclearView);
        remindersRecyclerView.setHasFixedSize(true);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(context));



        Intent fromIntent = getIntent();
        Bundle bundle = fromIntent.getExtras();

        if (bundle != null) {
            fromIntentStr = (String) bundle.get("from");

            if ((fromIntentStr.equals(context.getString(R.string.text_today))) ||
                    (fromIntentStr.equals(context.getString(R.string.text_timed))) ||
                    (fromIntentStr.equals(context.getString(R.string.text_allOf))) ||
                    (fromIntentStr.equals(context.getString(R.string.text_flagged)))) {

                activityTitle = fromIntentStr;

                editButton.setVisibility(View.GONE);

                reminderCategoryTextView.setText(activityTitle);


                if (fromIntentStr.equals(context.getString(R.string.text_today))) {
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    reminderCategoryTextView.setTextColor(Color.parseColor("#64B5F6"));

                    listRemindersAdapter = new ListRemindersAdapter(context, reminderDao.getRemindersCreatedToday(currentDate));

                } else if (fromIntentStr.equals(context.getString(R.string.text_timed))) {
                    reminderCategoryTextView.setTextColor(Color.parseColor("#FF0000"));

                    listRemindersAdapter = new ListRemindersAdapter(context, reminderDao.getTimedReminders());

                } else if (fromIntentStr.equals(context.getString(R.string.text_allOf))) {
                    reminderCategoryTextView.setTextColor(Color.parseColor("#424242"));

                    listRemindersAdapter = new ListRemindersAdapter(context, reminderDao.getAllReminders());

                } else if (fromIntentStr.equals(context.getString(R.string.text_flagged))) {
                    reminderCategoryTextView.setTextColor(Color.parseColor("#FF7043"));

                    listRemindersAdapter = new ListRemindersAdapter(context, reminderDao.getFlaggedReminders());

                }
            } else if (fromIntentStr.equals("reminderList")) {
                listId = (long) bundle.get("ListId");

                activityTitle = reminderListDao.findReminderListById(listId).getListName();

                reminderCategoryTextView.setText(activityTitle);

                listRemindersAdapter = new ListRemindersAdapter(context, reminderDao.findRemindersByListId(listId));
            }
        }

        remindersRecyclerView.setAdapter(listRemindersAdapter);


        newReminderListButton = findViewById(R.id.new_reminder_list_Button);
        newReminderListButton.setVisibility(View.INVISIBLE);

        newReminderButton = findViewById(R.id.new_reminder_Button);

        newReminderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent goToNewReminderIntent = new Intent(context, NewReminderActivity.class);

                if(fromIntentStr.equals("reminderList")) {
                    long reminderListId = (long) bundle.get("ListId");

                    goToNewReminderIntent.putExtra("from", fromIntentStr);
                    goToNewReminderIntent.putExtra("reminderListId", reminderListId);
                }

                else {
                    goToNewReminderIntent.putExtra("from", fromIntentStr);
                }

                startActivity(goToNewReminderIntent);
            }
        });


        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToMainIntent = new Intent(context, MainActivity.class);
                startActivity(goToMainIntent);
            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu listPopUpMenu = new PopupMenu(context, editButton);
                MenuInflater menuInflater = listPopUpMenu.getMenuInflater();
                menuInflater.inflate(R.menu.reminder_list_edit_list_name_menu, listPopUpMenu.getMenu());
                listPopUpMenu.show();


                listPopUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.change_list_name:

                                View changeListNameView = getLayoutInflater().inflate(R.layout.alert_change_reminder_list_name, null);

                                EditText newListName = changeListNameView.findViewById(R.id.new_reminder_list_name_EditText);

                                newListName.setText(activityTitle);


                                AlertDialog.Builder newListNameDialog = new AlertDialog.Builder(context);

                                newListNameDialog.setTitle(context.getString(R.string.text_newListName));

                                newListNameDialog.setView(changeListNameView);

                                newListNameDialog.setPositiveButton(context.getString(R.string.text_change),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                activityTitle = newListName.getText().toString();
                                                reminderCategoryTextView.setText(activityTitle);

                                                ReminderList updatedReminderList = reminderListDao.findReminderListById(listId);
                                                updatedReminderList.setListName(activityTitle);
                                                reminderListDao.updateOneReminderList(updatedReminderList);

                                                ReminderListFirebaseObject reminderListFirebaseObject =
                                                        new ReminderListFirebaseObject(updatedReminderList.getFirebaseId(), updatedReminderList.getListId(), updatedReminderList.getListName());


                                                Map<String, Object> updatedReminderListHashMap = reminderListFirebaseObject.toMap();

                                                databaseReferenceReminderList.child("user-" + userUid).child(updatedReminderList.getFirebaseId())
                                                        .updateChildren(updatedReminderListHashMap);


                                            }
                                        });


                                newListNameDialog.setNegativeButton(context.getString(R.string.text_cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });

                                newListNameDialog.create().show();

                                return true;


                            case R.id.delete_list:
                                AlertDialog.Builder deleteReminderListListDialog = new AlertDialog.Builder(context);

                                String currentLanguage = Locale.getDefault().toString();

                                String messageTitle;
                                if (currentLanguage.equals("tr_TR")) {
                                    messageTitle = '"' + activityTitle + "\" " + context.getString(R.string.alert_deleteReminderList);
                                    deleteReminderListListDialog.setTitle(messageTitle);
                                } else if (currentLanguage.equals("en_US")) {
                                    messageTitle = context.getString(R.string.alert_deleteReminderList) + " \"" + activityTitle + "\"";
                                    deleteReminderListListDialog.setTitle(messageTitle);
                                }

                                deleteReminderListListDialog.setMessage(context.getString(R.string.alert_deleteReminderListMessage));

                                deleteReminderListListDialog.setPositiveButton(context.getString(R.string.text_delete),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                List<Reminder> reminderList = reminderDao.findRemindersByListId(listId);

                                                Intent alarmManagerIntent = new Intent(context, ReminderAlarmReceiver.class);
                                                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                                                for(Reminder r: reminderList) {
                                                    if(r.getDate() != null) {
                                                        PendingIntent alarmManagerPendingIntent = PendingIntent.getBroadcast(context, (int) r.getId(), alarmManagerIntent, 0);
                                                        alarmManager.cancel(alarmManagerPendingIntent);
                                                        alarmManagerPendingIntent = null;
                                                    }

                                                    databaseReferenceReminders.child("user-" + userUid).child(r.getFirebaseId())
                                                            .removeValue();

                                                }



                                                reminderDao.deleteAllReminderFromListByListId(listId);


                                                String deletedReminderListFireBaseId = reminderListDao.findReminderListById(listId).getFirebaseId();

                                                reminderListDao.deleteOneReminderList(reminderListDao.findReminderListById(listId));


                                                databaseReferenceReminderList.child("user-" + userUid).child(deletedReminderListFireBaseId)
                                                        .removeValue();


                                                Intent goToMainIntent = new Intent(context, MainActivity.class);
                                                startActivity(goToMainIntent);
                                            }
                                        });

                                deleteReminderListListDialog.setNegativeButton(context.getString(R.string.text_cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //
                                            }
                                        });

                                deleteReminderListListDialog.show();

                                return true;
                        }
                        return true;
                    }
                });

            }
        });
    }



    @Override
    public void onBackPressed() {
        Intent goToMainIntent = new Intent(context, MainActivity.class);
        startActivity(goToMainIntent);
    }
}