package com.muhammedalikocabey.reminders;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ReminderDatabase reminderDB = ReminderDatabase.getDatabase(context);
        ReminderDao reminderDao = reminderDB.reminderDao();


        List<Reminder> reminderLists = reminderDao.getAllReminders();



        for(Reminder reminder : reminderLists) {

            Date reminderDate = reminder.getDate();

            if(reminderDate != null ) {
                if (!reminder.isReminded()) {
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



                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
        }
    }
}