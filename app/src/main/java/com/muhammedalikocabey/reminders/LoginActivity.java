package com.muhammedalikocabey.reminders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Context context = this;
    private EditText editTextTextEmailAddress;
    private EditText editTextTextPassword;
    private Button signInButton;

    private Button signUpButton;

    private SharedPreferences sharedPreferences;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceReminders;
    private DatabaseReference databaseReferenceReminderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        firebaseAuth = FirebaseAuth.getInstance();

        if(FirebaseDatabase.getInstance() == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }



        databaseReferenceReminders = FirebaseDatabase.getInstance().getReference("reminder");
        databaseReferenceReminders.keepSynced(true);

        databaseReferenceReminderList = FirebaseDatabase.getInstance().getReference("reminder_list");
        databaseReferenceReminderList.keepSynced(true);




       sharedPreferences = getSharedPreferences("loginOrWithoutSignIn", MODE_PRIVATE);


        if(sharedPreferences.contains("loginOrWithoutSignIn")) {
            if ((sharedPreferences.getBoolean("loginOrWithoutSignIn", true))) {
                Intent goToMainActivity = new Intent(context, MainActivity.class);
                startActivity(goToMainActivity);
            }
        }


        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {

            String fromIntentStr = (String) bundle.get("from");

            if(TextUtils.equals(fromIntentStr, "register")) {
                Toast.makeText(context, context.getString(R.string.alert_verifyEmail), Toast.LENGTH_LONG).show();
            }
        }



        editTextTextEmailAddress = findViewById(R.id.email_EditText);
        editTextTextPassword = findViewById(R.id.password1_EditText);
        signInButton = findViewById(R.id.sign_in_Button);
        signUpButton = findViewById(R.id.sign_up_Button);




        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextTextEmailAddress.getText().toString();
                String password = editTextTextPassword.getText().toString();

                ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                boolean internetConnected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

                if(!internetConnected) {
                    Toast.makeText(context, context.getString(R.string.alert_internetInactive), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(context, context.getString(R.string.alert_emailEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(context, context.getString(R.string.alert_emailFormatWrong), Toast.LENGTH_SHORT).show();
                    return;
                }


                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(context, context.getString(R.string.alert_passwordEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }



                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()) {

                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    String userUid = currentUser.getUid();

                                    if(!currentUser.isEmailVerified()) {
                                        Toast.makeText(context, context.getString(R.string.alert_verifyEmail), Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                        return;
                                    }

                                    sharedPreferences.edit().putBoolean("loginOrWithoutSignIn", true).apply();



                                    // Syncronize Firebase Data to Local DB
                                    ReminderDatabase reminderDB = ReminderDatabase.getDatabase(context);
                                    ReminderDao reminderDao = reminderDB.reminderDao();

                                    ReminderListDatabase reminderListDB = ReminderListDatabase.getDatabase(context);
                                    ReminderListDao reminderListDao = reminderListDB.reminderListDao();


                                    Query userReminderListQuery = databaseReferenceReminderList.child("user-" + userUid);

                                    userReminderListQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()) {

                                                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                                                    ReminderListFirebaseObject reminderListFirebaseObject =
                                                            ds.getValue(ReminderListFirebaseObject.class);

                                                    ReminderList reminderList = new ReminderList(reminderListFirebaseObject.getFirebaseId(), reminderListFirebaseObject.getListName());
                                                    reminderList.setListId(reminderListFirebaseObject.getListId());

                                                    reminderListDao.insertOneReminderList(reminderList);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.e("List:onCancelled", databaseError.toException().toString());
                                        }
                                    });



                                    Query userReminderQuery = databaseReferenceReminders.child("user-" + userUid);

                                   // Query userReminderQuery = databaseReferenceReminderList.child("reminder").orderByChild("userUid").equalTo(userUid);

                                    userReminderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()) {

                                                for(DataSnapshot ds : dataSnapshot.getChildren()) {


                                                    ReminderFirebaseObject reminderFirebaseObject =
                                                            ds.getValue(ReminderFirebaseObject.class);

                                                    Reminder reminder = new Reminder();
                                                    reminder.reminderFirebaseToReminder(reminderFirebaseObject);

                                                    Log.e("LOGİn", "------------------");
                                                    Log.e("reminderONL", reminder.toString());

                                                    reminderDao.insertOneReminder(reminder);


                                                    // Set alarm manager for reminder syncronized from Firebase.
                                                    Date reminderDate = reminder.getDate();

                                                    if(reminderDate != null) {
                                                        if(!reminder.isReminded()) {
                                                            Log.e("Login", "NOT Reminded" + reminder.toString());
                                                            Calendar setCalendar = Calendar.getInstance();

                                                            setCalendar.set(Calendar.YEAR, reminderDate.getYear());
                                                            setCalendar.set(Calendar.MONTH, reminderDate.getMonth());
                                                            setCalendar.set(Calendar.DAY_OF_MONTH, reminderDate.getDate());
                                                            setCalendar.set(Calendar.HOUR_OF_DAY, reminderDate.getHours());
                                                            setCalendar.set(Calendar.MINUTE, reminderDate.getMinutes());
                                                            setCalendar.set(Calendar.SECOND, 0);


                                                            AlarmManager alarmManager =
                                                                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                                                            Intent reminderAlarmIntent = new Intent(context, ReminderAlarmReceiver.class);

                                                            int reminderId = (int) reminder.getId();
                                                            reminderAlarmIntent.putExtra("reminderId", reminderId);

                                                            PendingIntent reminderAlarmPendingIntent = PendingIntent.getBroadcast(context, reminderId,
                                                                    reminderAlarmIntent, PendingIntent.FLAG_ONE_SHOT);



                                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                                                                        setCalendar.getTimeInMillis(),
                                                                        reminderAlarmPendingIntent);
                                                            }

                                                            else {
                                                                alarmManager.set(AlarmManager.RTC_WAKEUP,
                                                                        setCalendar.getTimeInMillis(),
                                                                        reminderAlarmPendingIntent);
                                                            }

                                                        }
                                                    }
                                                    // END OF Set alarm manager


                                                }
                                            }
                                        }




                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.e("Rem:onCancelled", databaseError.toException().toString());
                                        }
                                    });




                                    startActivity(new Intent(context, MainActivity.class));
                                }

                                else {
                                    Toast.makeText(context, context.getString(R.string.alert_loginUnsuccesful), Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        });
            }
        });





        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSignUpActivity = new Intent(context, RegisterActivity.class);
                startActivity(goToSignUpActivity);
            }
        });




    }
}