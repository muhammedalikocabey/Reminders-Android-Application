package com.muhammedalikocabey.reminders;


import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NewReminderListActivity extends AppCompatActivity {

    private Context context = this;
    private Button addNewReminderListButton;
    private Button cancelNewReminderListButton;
    private ImageView reminderListIconImageView;
    private TextView newReminderListNameEditText;


    String newReminderListName = "";

    private FirebaseAuth firebaseAuth;
    private String userUid;
    private DatabaseReference databaseReferenceReminderList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder_list);



        ReminderListDatabase reminderListDB = ReminderListDatabase.getDatabase(getApplicationContext());
        ReminderListDao reminderListDao = reminderListDB.reminderListDao();



        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
        userUid = currentFirebaseUser.getUid();



        databaseReferenceReminderList = FirebaseDatabase.getInstance().getReference("reminder_list");
        databaseReferenceReminderList.keepSynced(true);




        addNewReminderListButton = findViewById(R.id.add_new_reminder_list_Button);

        cancelNewReminderListButton = findViewById(R.id.back_to_main_Button);


        reminderListIconImageView = findViewById(R.id.reminder_list_icon_ImageView);


        newReminderListNameEditText = findViewById(R.id.new_reminder_list_name_EditText);





        addNewReminderListButton.setEnabled(false);
        addNewReminderListButton.setTextColor(getResources().getColor(R.color.grey_500));





        newReminderListNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
                newReminderListName = newReminderListNameEditText.getText().toString();
                if (newReminderListName.isEmpty()) {
                    addNewReminderListButton.setEnabled(false);
                    addNewReminderListButton.setTextColor(getResources().getColor(R.color.grey_500));
                } else {
                    addNewReminderListButton.setEnabled(true);
                    addNewReminderListButton.setTextColor(getResources().getColor(R.color.light_blue_A700));
                }
            }
        });


        addNewReminderListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newReminderListName = newReminderListNameEditText.getText().toString();


                String firebaseId = databaseReferenceReminderList.push().getKey();

                ReminderList newReminderList = new ReminderList(firebaseId, newReminderListName);

                reminderListDao.insertOneReminderList(newReminderList);




                int latestReminderListId = reminderListDao.getLatestReminderListID();

                ReminderListFirebaseObject newReminderListFirebase = new ReminderListFirebaseObject(newReminderList.getFirebaseId(), latestReminderListId, newReminderList.getListName());

                databaseReferenceReminderList.child("user-" + userUid).child(firebaseId).setValue(newReminderListFirebase);


                Intent goToMainActivity = new Intent(NewReminderListActivity.this, MainActivity.class);
                startActivity(goToMainActivity);
            }
        });


        cancelNewReminderListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newReminderListName = newReminderListNameEditText.getText().toString();

                if (newReminderListName.isEmpty()) {
                    Intent goToMainActivity = new Intent(NewReminderListActivity.this, MainActivity.class);
                    startActivity(goToMainActivity);
                } else {

                    AlertDialog.Builder removeAndCancelNewReminderListDialog = new AlertDialog.Builder(context);

                    removeAndCancelNewReminderListDialog.setTitle(context.getString(R.string.alert_cancelNewReminder));

                    removeAndCancelNewReminderListDialog.setPositiveButton(context.getString(R.string.text_yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent goToMainActivity = new Intent(NewReminderListActivity.this, MainActivity.class);
                                    startActivity(goToMainActivity);
                                }
                            });

                    removeAndCancelNewReminderListDialog.setNegativeButton(context.getString(R.string.text_no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                }
                            });
                    removeAndCancelNewReminderListDialog.show();
                }
            }
        });

    }




    @Override
    public void onBackPressed() {
        newReminderListName = newReminderListNameEditText.getText().toString();

        if (newReminderListName.isEmpty()) {
            Intent goToMainActivity = new Intent(NewReminderListActivity.this, MainActivity.class);
            startActivity(goToMainActivity);

        } else {
            AlertDialog.Builder removeAndCancelNewReminderListDialog = new AlertDialog.Builder(context);

            removeAndCancelNewReminderListDialog.setTitle(context.getString(R.string.alert_cancelNewReminder));

            removeAndCancelNewReminderListDialog.setPositiveButton(context.getString(R.string.text_yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent goToMainActivity = new Intent(NewReminderListActivity.this, MainActivity.class);
                            startActivity(goToMainActivity);
                        }
                    });

            removeAndCancelNewReminderListDialog.setNegativeButton(context.getString(R.string.text_no),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });

            removeAndCancelNewReminderListDialog.show();
        }
    }
}