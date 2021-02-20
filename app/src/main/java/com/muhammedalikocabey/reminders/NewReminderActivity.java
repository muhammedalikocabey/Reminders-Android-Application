package com.muhammedalikocabey.reminders;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class  NewReminderActivity extends AppCompatActivity {

    private Context context = this;

    private Button cancelNewReminderButton;
    private Button addNewReminderButton;

    private TextView activityTitle;

    private EditText newReminderTitleEditText;
    private EditText newReminderContentEditText;
    private EditText newReminderURLEditText;

    private Switch dateSwitch;
    private Button dateSwitchButton;
    private Switch clockSwitch;
    private Button clockSwitchButton;

    private Button priorityButton;
    private Button listButton;

    private Switch flagSwitch;
    private Switch locationSwitch;
    private Switch whileTextingSwitch;
    private TextView selectedDateTextView;
    private TextView selectedClockTextView;
    private TextView priorityTextView;
    private TextView listTextView;

    private boolean isReminderChanged = false;

    private String newReminder_Title = "";
    private String newReminder_Content = "";
    private String newReminder_URL = "";
    private Date newReminder_selectedDate = null;
    private boolean newReminder_isReminded = false;
    private String newReminder_selectedHour = null;
    private boolean newReminder_isFlagged = false;
    private String newReminder_priorityLevel;
    private long newReminder_listID;

    private Reminder editedReminder = null;

    private boolean newReminder = false;
    private boolean editReminder = false;
    private boolean newReminderFromListViews = false;
    private boolean newReminderFromCategoryViews = false;

    private String fromIntentStr = "";

    private FirebaseAuth firebaseAuth;
    private String userUid;
    private DatabaseReference databaseReferenceReminders;
    private DatabaseReference databaseReferenceReminderList;



    public String dateHourDesign(int value) {
        String returnn = String.valueOf(value);

        if (value < 10) {
            returnn = "0" + returnn;
        }

        return returnn;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);


        ReminderDatabase reminderDB = ReminderDatabase.getDatabase(context);
        ReminderDao reminderDao = reminderDB.reminderDao();

        ReminderListDatabase reminderListDB = ReminderListDatabase.getDatabase(context);
        ReminderListDao reminderListDao = reminderListDB.reminderListDao();


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
        userUid = currentFirebaseUser.getUid();



        databaseReferenceReminders = FirebaseDatabase.getInstance().getReference().child("reminder");
        databaseReferenceReminders.keepSynced(true);

        databaseReferenceReminderList = FirebaseDatabase.getInstance().getReference().child("reminder_list");
        databaseReferenceReminderList.keepSynced(true);






        activityTitle = findViewById(R.id.new_reminder_list_TextView);

        newReminderTitleEditText = findViewById(R.id.new_reminder_title_EditText);

        newReminderContentEditText = findViewById(R.id.new_reminder__content_EditText);

        newReminderURLEditText = findViewById(R.id.new_reminder__url_EditText);

        addNewReminderButton = findViewById(R.id.add_new_reminder_list_Button);
        addNewReminderButton.setEnabled(false);
        addNewReminderButton.setTextColor(getResources().getColor(R.color.grey_500));

        cancelNewReminderButton = findViewById(R.id.back_to_main_Button);

        dateSwitch = findViewById(R.id.date_switch);
        dateSwitchButton = findViewById(R.id.date_switch_Button);
        dateSwitchButton.setEnabled(false);

        clockSwitch = findViewById(R.id.clock_switch);
        clockSwitchButton = findViewById(R.id.clock_switch_Button);
        clockSwitchButton.setEnabled(false);

        flagSwitch = findViewById(R.id.flag_switch);

        selectedDateTextView = findViewById(R.id.date_selected_date_textView);

        selectedClockTextView = findViewById(R.id.clock_selected_clock_textView);

        priorityButton = findViewById(R.id.priority_Button);
        priorityTextView = findViewById(R.id.priority_TextView);

        listButton = findViewById(R.id.list_Button);
        listTextView = findViewById(R.id.list_TextView);


        // IN THE NEW VERSION
        locationSwitch = findViewById(R.id.location_switch);
        locationSwitch.setClickable(false);

        whileTextingSwitch = findViewById(R.id.while_texting_switch);
        whileTextingSwitch.setClickable(false);


        Intent fromIntent = getIntent();
        Bundle bundle = fromIntent.getExtras();

        if (bundle != null) {
            fromIntentStr = (String) bundle.get("from");

            if(fromIntentStr.equals("editReminder")){
                // Edit Reminder

                editReminder = true;


                long reminderId = (long) bundle.get("reminderId");

                editedReminder = reminderDao.findReminderById(reminderId);


                activityTitle.setText(context.getString(R.string.text_details));

                addNewReminderButton.setEnabled(true);
                addNewReminderButton.setText(context.getString(R.string.text_update));
                addNewReminderButton.setTextColor(getResources().getColor(R.color.light_blue_A700));


                newReminderTitleEditText.setText(editedReminder.getTitle());
                newReminder_Title = editedReminder.getTitle();


                if (!editedReminder.getContent().equals("")) {
                    newReminderContentEditText.setText(editedReminder.getContent());
                    newReminder_Content = editedReminder.getContent();
                }


                if (!editedReminder.getUrl().equals("")) {
                    newReminderURLEditText.setText(editedReminder.getUrl());
                    newReminder_URL = editedReminder.getUrl();
                }


                if (editedReminder.getDate() != null) {

                    dateSwitch.setChecked(true);
                    dateSwitchButton.setEnabled(true);

                    clockSwitch.setChecked(true);
                    clockSwitchButton.setEnabled(true);


                    Date reminderDate = editedReminder.getDate();

                    String reminderDateStr;
                    String reminderHourMinStr;

                    String selectedHourStr = "";
                    String selectedMinuteStr = "";




                    if (reminderDate.getHours() < 10) {
                        selectedHourStr = "0" + String.valueOf(reminderDate.getHours());
                    }

                    else {
                        selectedHourStr = String.valueOf(reminderDate.getHours());
                    }

                    if (reminderDate.getMinutes() < 10) {
                        selectedMinuteStr = "0" + String.valueOf(reminderDate.getMinutes());
                    }

                    else {
                        selectedMinuteStr = String.valueOf(reminderDate.getMinutes());
                    }


                    reminderHourMinStr = dateHourDesign(reminderDate.getHours()) + ":" + dateHourDesign(reminderDate.getMinutes());

                    reminderDateStr = dateHourDesign(reminderDate.getDate()) + "." +
                            dateHourDesign(reminderDate.getMonth() + 1) + "." + dateHourDesign(reminderDate.getYear());


                    selectedDateTextView.setVisibility(View.VISIBLE);
                    selectedDateTextView.setText(reminderDateStr);
                    newReminder_selectedDate = reminderDate;

                    selectedClockTextView.setVisibility(View.VISIBLE);
                    selectedClockTextView.setText(reminderHourMinStr);
                    newReminder_selectedHour = selectedHourStr + ":" + selectedMinuteStr;
                }


                if (editedReminder.isFlagged) {
                    flagSwitch.setChecked(true);
                    newReminder_isFlagged = true;
                }

                String priorityLevel = editedReminder.getPriorityLevel();
                if (priorityLevel.equals("")) {
                    newReminder_priorityLevel = context.getString(R.string.text_none);
                    priorityTextView.setText(context.getString(R.string.text_none));

                } else if (priorityLevel.equals("!")) {
                    newReminder_priorityLevel = context.getString(R.string.text_low);
                    priorityTextView.setText(context.getString(R.string.text_low));

                } else if (priorityLevel.equals("!!")) {
                    newReminder_priorityLevel = context.getString(R.string.text_medium);
                    priorityTextView.setText(context.getString(R.string.text_medium));

                } else if (priorityLevel.equals("!!!")) {
                    newReminder_priorityLevel = context.getString(R.string.text_high);
                    priorityTextView.setText(context.getString(R.string.text_high));
                }

                newReminder_listID = editedReminder.getListId();
                listTextView.setText(reminderListDao.findReminderListById(newReminder_listID).getListName());
            }

            else if (fromIntentStr.equals("reminderList")){

                newReminderFromListViews = true;

                newReminder_listID = (long) bundle.get("reminderListId");
                listTextView.setText(reminderListDao.findReminderListById(newReminder_listID).getListName());


                newReminder_priorityLevel = context.getString(R.string.text_none);
            }

            else {
                newReminderFromCategoryViews = true;


                dateSwitchButton.setEnabled(true);

                final Calendar calendar = Calendar.getInstance();

                int currentYear = calendar.get(Calendar.YEAR);

                int currentMonth = calendar.get(Calendar.MONTH);

                int currentDay = calendar.get(Calendar.DATE);

                Date currentDate = new Date(currentYear, currentMonth, currentDay);


                if (fromIntentStr.equals(context.getString(R.string.text_today))) {
                    dateSwitch.setChecked(true);
                    dateSwitchButton.setEnabled(true);

                    String reminderDateStr;

                    reminderDateStr = dateHourDesign(currentDate.getDate()) + "." +
                            dateHourDesign(currentDate.getMonth() + 1) + "." + dateHourDesign(currentDate.getYear());


                    selectedDateTextView.setVisibility(View.VISIBLE);
                    selectedDateTextView.setText(reminderDateStr);
                    newReminder_selectedDate = currentDate;
                }

                else if (fromIntentStr.equals(context.getString(R.string.text_timed))) {
                    dateSwitch.setChecked(true);
                    dateSwitchButton.setEnabled(true);



                    String reminderDateStr;

                    reminderDateStr = dateHourDesign(currentDate.getDate()) + "." +
                            dateHourDesign(currentDate.getMonth() + 1) + "." + dateHourDesign(currentDate.getYear());


                    selectedDateTextView.setVisibility(View.VISIBLE);
                    selectedDateTextView.setText(reminderDateStr);
                    newReminder_selectedDate = currentDate;
                }

                else if (fromIntentStr.equals(context.getString(R.string.text_allOf))) {
                    //
                }

                else if (fromIntentStr.equals(context.getString(R.string.text_flagged))) {
                    flagSwitch.setChecked(true);
                    newReminder_isFlagged = true;
                }

                newReminder_priorityLevel = context.getString(R.string.text_none);
            }


        } else {
            newReminder = true;
            newReminder_priorityLevel = context.getString(R.string.text_none);
        }




        if (reminderListDao.getAllReminderList().size() == 0) {

            String firebaseId = databaseReferenceReminderList.push().getKey();

            ReminderList defaultReminderList = new ReminderList(firebaseId, context.getString(R.string.text_reminders));
            reminderListDao.insertOneReminderList(defaultReminderList);


            newReminder_listID = reminderListDao.getLatestReminderListID();


            ReminderListFirebaseObject newReminderListFirebase = new ReminderListFirebaseObject(firebaseId, newReminder_listID, defaultReminderList.getListName());

            databaseReferenceReminderList.child("user-" + userUid).child(firebaseId).setValue(newReminderListFirebase);



            listTextView.setText(defaultReminderList.getListName());

        } else {
            if ((newReminderFromListViews == false) && (editReminder == false)) {
                ReminderList latestReminderList = reminderListDao.getLatestReminderList();
                newReminder_listID = reminderListDao.getLatestReminderListID();
                listTextView.setText(latestReminderList.getListName());
            }
        }




        dateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    selectedDateTextView.setVisibility(View.VISIBLE);

                    dateSwitchButton.setEnabled(true);


                    final Calendar calendar = Calendar.getInstance();

                    int currentYear = calendar.get(Calendar.YEAR);

                    int currentMonth = calendar.get(Calendar.MONTH);

                    int currentDay = calendar.get(Calendar.DATE);

                    Date currentDate = new Date(currentYear, currentMonth, currentDay);
                    Date tomorrowDate = new Date(currentYear, currentMonth, currentDay + 1);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                            String dayStr = "";
                            String monthStr = "";


                            if (selectedDay < 10) {
                                dayStr = "0" + String.valueOf(selectedDay);
                            } else {
                                dayStr = String.valueOf(selectedDay);
                            }

                            if (selectedMonth < 10) {
                                monthStr = "0" + String.valueOf(selectedMonth + 1);
                            } else {
                                monthStr = String.valueOf(selectedMonth + 1);
                            }



                            String selectedDateString = dayStr + "." + monthStr + "." + selectedYear;


                            newReminder_selectedDate = new Date(selectedYear, selectedMonth, selectedDay);



                            if (newReminder_selectedDate.compareTo(currentDate) == 0) {
                                newReminder_selectedDate = currentDate;
                                selectedDateTextView.setText(context.getString(R.string.text_today));
                            } else if (newReminder_selectedDate.compareTo(tomorrowDate) == 0) {
                                newReminder_selectedDate = tomorrowDate;
                                selectedDateTextView.setText(context.getString(R.string.text_tomorrow));
                            } else {
                                selectedDateTextView.setText(selectedDateString);
                                newReminder_selectedDate = new Date(selectedYear, selectedMonth, selectedDay);
                            }

                        }
                    }, currentYear, currentMonth, currentDay);

                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()- 1000);
                    datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, context.getString(R.string.text_select), datePickerDialog);

                    datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, context.getString(R.string.text_cancel), datePickerDialog);
                    datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dateSwitch.setChecked(false);
                        }
                    });


                    datePickerDialog.show();



                } else {

                    dateSwitchButton.setEnabled(false);

                    newReminder_selectedDate = null;

                    selectedDateTextView.setVisibility(View.GONE);

                    clockSwitch.setChecked(isChecked);
                }
            }
        });


        dateSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateSwitch.setChecked(false);
                dateSwitch.setChecked(true);
            }
        });


        clockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    clockSwitchButton.setEnabled(true);

                    dateSwitch.setChecked(isChecked);

                    selectedClockTextView.setVisibility(View.VISIBLE);


                    final Calendar calendar = Calendar.getInstance();


                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                    int currentMinute = calendar.get(Calendar.MINUTE);


                    selectedClockTextView.setText(currentHour + ":" + currentMinute);


                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                            String selectedHourStr = "";
                            String selectedMinuteStr = "";

                            if (selectedHour < 10) {
                                selectedHourStr = "0" + String.valueOf(selectedHour);
                            } else {
                                selectedHourStr = String.valueOf(selectedHour);
                            }

                            if (selectedMinute < 10) {
                                selectedMinuteStr = "0" + String.valueOf(selectedMinute);
                            } else {
                                selectedMinuteStr = String.valueOf(selectedMinute);
                            }

                            newReminder_selectedHour = selectedHourStr + ":" + selectedMinuteStr;

                            selectedClockTextView.setText(newReminder_selectedHour);
                        }
                    }, currentHour, currentMinute, true);

                    timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE, context.getString(R.string.text_select), timePickerDialog);
                    timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, context.getString(R.string.text_cancel), timePickerDialog);
                    timePickerDialog.show();

                } else {

                    clockSwitchButton.setEnabled(false);

                    newReminder_selectedHour = null;

                    selectedClockTextView.setVisibility(View.GONE);
                }
            }
        });


        clockSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clockSwitch.setChecked(false);
                clockSwitch.setChecked(true);
            }
        });




        flagSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newReminder_isFlagged = true;
                } else {
                    newReminder_isFlagged = false;
                }
            }
        });


        priorityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu priorityLevelPopUp = new PopupMenu(NewReminderActivity.this, priorityButton);

                priorityLevelPopUp.getMenuInflater().inflate(R.menu.new_reminder_priority_menu,
                        priorityLevelPopUp.getMenu());
                priorityLevelPopUp.show();

                priorityLevelPopUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        priorityTextView.setText(item.getTitle().toString());

                        newReminder_priorityLevel = item.getTitle().toString();

                        return true;
                    }
                });
            }
        });


        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu reminderListPopUp = new PopupMenu(NewReminderActivity.this, listButton);

                List<ReminderList> allReminderList = reminderListDao.getAllReminderList();


                for (ReminderList reminderList : allReminderList) {
                    reminderListPopUp.getMenu().add((int) reminderList.getListId(), (int) reminderList.getListId(), (int) reminderList.getListId(), reminderList.getListName());
                }


                reminderListPopUp.show();

                reminderListPopUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        listTextView.setText(item.getTitle().toString());
                        newReminder_listID = (long) item.getItemId();

                        return true;
                    }
                });

            }
        });

        newReminderTitleEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String title = newReminderTitleEditText.getText().toString();
                if (title.isEmpty()) {
                    addNewReminderButton.setEnabled(false);
                    addNewReminderButton.setTextColor(getResources().getColor(R.color.grey_500));
                } else {
                    isReminderChanged = true;

                    addNewReminderButton.setEnabled(true);
                    addNewReminderButton.setTextColor(getResources().getColor(R.color.light_blue_A700));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }
        });


        addNewReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newReminder_Title = newReminderTitleEditText.getText().toString();
                newReminder_Content = newReminderContentEditText.getText().toString();
                newReminder_URL = newReminderURLEditText.getText().toString();
                String priorityLevel = "";


                if (newReminder_selectedDate != null) {
                    if (newReminder_selectedHour != null) {
                        String[] parseHourMin = newReminder_selectedHour.split(":");
                        int hour = Integer.parseInt(parseHourMin[0]);
                        int minute = Integer.parseInt(parseHourMin[1]);

                        newReminder_selectedDate.setHours(hour);
                        newReminder_selectedDate.setMinutes(minute);
                    }
                } else {
                    newReminder_isReminded = true;
                }




                if (newReminder_priorityLevel.equals(context.getString(R.string.text_none))) {
                    priorityLevel = "";
                } else if (newReminder_priorityLevel.equals(context.getString(R.string.text_low))) {
                    priorityLevel = "!";
                } else if (newReminder_priorityLevel.equals(context.getString(R.string.text_medium))) {
                    priorityLevel = "!!";
                } else if (newReminder_priorityLevel.equals(context.getString(R.string.text_high))) {
                    priorityLevel = "!!!";
                }


                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                Reminder newReminder = new Reminder(
                        "",
                        currentDate,
                        newReminder_Title,
                        newReminder_Content,
                        newReminder_URL,
                        newReminder_selectedDate,
                        newReminder_isReminded,
                        newReminder_isFlagged,
                        priorityLevel,
                        newReminder_listID);


                Intent goToActivity = new Intent(NewReminderActivity.this, MainActivity.class);

                if(editReminder) {

                    editedReminder.setTitle(newReminder_Title);
                    editedReminder.setContent(newReminder_Content);
                    editedReminder.setUrl(newReminder_URL);

                    editedReminder.setDate(newReminder_selectedDate);



                    if(newReminder_selectedDate == null) {
                        editedReminder.setReminded(true);
                    } else {
                        final Calendar calendar = Calendar.getInstance();

                        int currentYear = calendar.get(Calendar.YEAR);

                        int currentMonth = calendar.get(Calendar.MONTH);

                        int currentDay = calendar.get(Calendar.DATE);

                        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                        int currentMinute = calendar.get(Calendar.MINUTE);

                        Date currentTime = new Date(currentYear, currentMonth, currentDay, currentHour, currentMinute);

                        if(currentTime.after(editedReminder.getDate())) {
                            editedReminder.setReminded(true);
                        } else {
                            editedReminder.setReminded(false);
                        }
                    }
                    editedReminder.setFlagged(newReminder_isFlagged);
                    editedReminder.setPriorityLevel(priorityLevel);
                    editedReminder.setListId(newReminder_listID);


                    // editedReminder.setfirebaseid

                    reminderDao.updateOneReminder(editedReminder);

                    ReminderFirebaseObject  reminderFirebaseObject = new ReminderFirebaseObject();
                    reminderFirebaseObject.reminderToReminderFirebase(editedReminder);


                    Map<String, Object> editedReminderHashMap = reminderFirebaseObject.toMap();


                    databaseReferenceReminders.child("user-" + userUid).child(editedReminder.getFirebaseId())
                            .updateChildren(editedReminderHashMap);








                    Intent alarmManagerIntent = new Intent(context, ReminderAlarmReceiver.class);
                    PendingIntent alarmManagerPendingIntent = PendingIntent.getBroadcast(context, (int) editedReminder.getId(), alarmManagerIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    if(newReminder_selectedDate == null) {
                        alarmManager.cancel(alarmManagerPendingIntent);
                    }

                    else {
                        Calendar setCalendar = Calendar.getInstance();

                        Date reminderDate = editedReminder.getDate();


                        setCalendar.set(Calendar.YEAR, reminderDate.getYear());
                        setCalendar.set(Calendar.MONTH, reminderDate.getMonth());
                        setCalendar.set(Calendar.DAY_OF_MONTH, reminderDate.getDate());
                        setCalendar.set(Calendar.HOUR_OF_DAY, reminderDate.getHours());
                        setCalendar.set(Calendar.MINUTE, reminderDate.getMinutes());
                        setCalendar.set(Calendar.SECOND, 0);


                        Intent reminderAlarmIntent = new Intent(context, ReminderAlarmReceiver.class);


                        int reminderId = (int) editedReminder.getId();
                        reminderAlarmIntent.putExtra("reminderId", reminderId);


                        PendingIntent reminderAlarmPendingIntent = PendingIntent.getBroadcast(context, reminderId,
                                reminderAlarmIntent, PendingIntent.FLAG_ONE_SHOT);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                                    setCalendar.getTimeInMillis(),
                                    reminderAlarmPendingIntent);
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP,
                                    setCalendar.getTimeInMillis(),
                                    reminderAlarmPendingIntent);
                        }
                    }


                    goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                    goToActivity.putExtra("from", "reminderList");
                    goToActivity.putExtra("ListId", newReminder_listID);
                }

                else {
                    String firebaseId = databaseReferenceReminders.push().getKey();
                    newReminder.setFirebaseId(firebaseId);
                    reminderDao.insertOneReminder(newReminder);



                    int latestReminderId = reminderDao.getLatestReminderID();

                    ReminderFirebaseObject newReminderFirebase = new ReminderFirebaseObject(latestReminderId);
                    newReminderFirebase.reminderToReminderFirebase(newReminder);

                    databaseReferenceReminders.child("user-" + userUid).child(firebaseId).setValue(newReminderFirebase);





                    if(newReminderFromListViews) {
                        goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);

                        goToActivity.putExtra("from", fromIntentStr);
                        goToActivity.putExtra("ListId", newReminder_listID);
                    }

                    else if(newReminderFromCategoryViews) {
                        goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                        goToActivity.putExtra("from", fromIntentStr);
                    }


                    // ------------------   Alarm Manager   ------------------
                    if(newReminder.getDate() != null ) {

                        Calendar setCalendar = Calendar.getInstance();

                        Date reminderDate = newReminder.getDate();


                        setCalendar.set(Calendar.YEAR, reminderDate.getYear());
                        setCalendar.set(Calendar.MONTH, reminderDate.getMonth());
                        setCalendar.set(Calendar.DAY_OF_MONTH, reminderDate.getDate());
                        setCalendar.set(Calendar.HOUR_OF_DAY, reminderDate.getHours());
                        setCalendar.set(Calendar.MINUTE, reminderDate.getMinutes());
                        setCalendar.set(Calendar.SECOND, 0);



                        AlarmManager alarmManager =
                                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        Intent reminderAlarmIntent = new Intent(context, ReminderAlarmReceiver.class);


                        int reminderId = reminderDao.getLatestReminderID();
                        reminderAlarmIntent.putExtra("reminderId", reminderId);


                        PendingIntent reminderAlarmPendingIntent = PendingIntent.getBroadcast(context, reminderId,
                                reminderAlarmIntent, PendingIntent.FLAG_ONE_SHOT);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                                    setCalendar.getTimeInMillis(),
                                    reminderAlarmPendingIntent);
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP,
                                    setCalendar.getTimeInMillis(),
                                    reminderAlarmPendingIntent);
                        }
                    }
                    // ------------------ End Of AlarmManager   ------------------

                }


                startActivity(goToActivity);
            }
        });


        cancelNewReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isReminderChanged) {
                    AlertDialog.Builder removeAndCancelNewReminderDialog = new AlertDialog.Builder(context);

                    removeAndCancelNewReminderDialog.setTitle(context.getString(R.string.alert_cancelNewReminder));

                    removeAndCancelNewReminderDialog.setPositiveButton(context.getString(R.string.text_yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent goToActivity = new Intent(NewReminderActivity.this, MainActivity.class);

                                    if(editReminder) {
                                        goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                                        goToActivity.putExtra("from", "reminderList");
                                        goToActivity.putExtra("ListId", newReminder_listID);
                                    }

                                    else if(newReminderFromListViews) {
                                        goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);

                                        goToActivity.putExtra("from", fromIntentStr);
                                        goToActivity.putExtra("ListId", newReminder_listID);
                                    }

                                    else if(newReminderFromCategoryViews) {
                                        goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                                        goToActivity.putExtra("from", fromIntentStr);
                                    }

                                    startActivity(goToActivity);
                                }
                            });

                    removeAndCancelNewReminderDialog.setNegativeButton(context.getString(R.string.text_no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                }
                            });

                    removeAndCancelNewReminderDialog.show();

                } else {
                    Intent goToActivity = new Intent(NewReminderActivity.this, MainActivity.class);

                    if(editReminder) {
                        goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                        goToActivity.putExtra("from", "reminderList");
                        goToActivity.putExtra("ListId", newReminder_listID);
                    }

                    else if(newReminderFromListViews) {
                        goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);

                        goToActivity.putExtra("from", fromIntentStr);
                        goToActivity.putExtra("ListId", newReminder_listID);
                    }

                    else if(newReminderFromCategoryViews) {
                        goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                        goToActivity.putExtra("from", fromIntentStr);
                    }

                    startActivity(goToActivity);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {

        if (isReminderChanged) {
            AlertDialog.Builder removeAndCancelNewReminderDialog = new AlertDialog.Builder(context);

            removeAndCancelNewReminderDialog.setTitle(context.getString(R.string.alert_cancelNewReminder));

            removeAndCancelNewReminderDialog.setPositiveButton(context.getString(R.string.text_yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent goToActivity = new Intent(NewReminderActivity.this, MainActivity.class);

                            if(editReminder) {
                                goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                                goToActivity.putExtra("from", "reminderList");
                                goToActivity.putExtra("ListId", newReminder_listID);
                            }

                            else if(newReminderFromListViews) {
                                goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);

                                goToActivity.putExtra("from", fromIntentStr);
                                goToActivity.putExtra("ListId", newReminder_listID);
                            }

                            else if(newReminderFromCategoryViews) {
                                goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                                goToActivity.putExtra("from", fromIntentStr);
                            }

                            startActivity(goToActivity);
                        }
                    });

            removeAndCancelNewReminderDialog.setNegativeButton(context.getString(R.string.text_no),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });

            removeAndCancelNewReminderDialog.show();

        } else {
            Intent goToActivity = new Intent(NewReminderActivity.this, MainActivity.class);

            if(editReminder) {
                goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                goToActivity.putExtra("from", "reminderList");
                goToActivity.putExtra("ListId", newReminder_listID);
            }

            else if(newReminderFromListViews) {
                goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);

                goToActivity.putExtra("from", fromIntentStr);
                goToActivity.putExtra("ListId", newReminder_listID);
            }

            else if(newReminderFromCategoryViews) {
                goToActivity = new Intent(NewReminderActivity.this, ListRemindersActivity.class);
                goToActivity.putExtra("from", fromIntentStr);
            }

            startActivity(goToActivity);
        }
    }



}

