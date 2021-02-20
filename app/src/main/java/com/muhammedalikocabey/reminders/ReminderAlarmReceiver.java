package com.muhammedalikocabey.reminders;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;


public class ReminderAlarmReceiver extends BroadcastReceiver {

    private NotificationCompat.Builder notificationCompatBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {


        ReminderDatabase reminderDB = ReminderDatabase.getDatabase(context);
        ReminderDao reminderDao = reminderDB.reminderDao();


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = firebaseUser.getUid();
        DatabaseReference databaseReferenceReminders = FirebaseDatabase.getInstance().getReference("reminder");


        Bundle bundle = intent.getExtras();

        int reminderId = 0;

        if (bundle != null) {
            reminderId = (int) bundle.get("reminderId");
        }



        Reminder currentReminder = reminderDao.findReminderById(reminderId);

        Log.e("RECEIVErCURRENT", currentReminder.toString());

        if(currentReminder.isReminded()) {
            return;
        }

        currentReminder.setReminded(true);

        Log.e("RCEIVER", "-------------");
        ReminderFirebaseObject reminderFirebaseObject = new ReminderFirebaseObject();
        reminderFirebaseObject.reminderToReminderFirebase(currentReminder);
        Log.e("FROBJ", reminderFirebaseObject.toString());

        Map<String, Object> editedReminderHashMap = reminderFirebaseObject.toMap();
        Log.e("hashmap", editedReminderHashMap.toString());

        databaseReferenceReminders.child("user-" + userUid).child(currentReminder.getFirebaseId())
                .updateChildren(editedReminderHashMap);




        reminderDao.updateOneReminder(currentReminder);




        boolean reminderLastRepeatDatePass = false;


        Intent goFromNotificationIntent = new Intent(context, NewReminderActivity.class);

        goFromNotificationIntent.putExtra("from", "editReminder");
        goFromNotificationIntent.putExtra("reminderId", (long)reminderId);


        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, reminderId,
                goFromNotificationIntent, PendingIntent.FLAG_ONE_SHOT);



        // ------------------   Notification   ------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "com.muhammedalikocabey.reminders";
            String channelName = "Internal Reminder Notifications";
            String channelDescription = "Internal Reminder Notifications";

            int channelPriority = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);


            if (channel == null) {
                channel = new NotificationChannel(channelId, channelName, channelPriority);
                channel.setDescription(channelDescription);
                notificationManager.createNotificationChannel(channel);
            }

            notificationCompatBuilder = new NotificationCompat.Builder(context, channelId);

            notificationCompatBuilder.setContentTitle(currentReminder.getTitle());
            notificationCompatBuilder.setSmallIcon(R.drawable.ic_baseline_app_icon_256x256);
            notificationCompatBuilder.setAutoCancel(true);

            notificationCompatBuilder.setContentIntent(notificationPendingIntent);
        } else {
            notificationCompatBuilder = new NotificationCompat.Builder(context);

            notificationCompatBuilder.setContentTitle(currentReminder.getTitle());
            notificationCompatBuilder.setSmallIcon(R.drawable.ic_baseline_app_icon_256x256);
            notificationCompatBuilder.setAutoCancel(true);

            notificationCompatBuilder.setPriority(Notification.PRIORITY_HIGH);

            notificationCompatBuilder.setContentIntent(notificationPendingIntent);
        }


        // Vibrate Phone
        notificationCompatBuilder.setVibrate(new long[] {1000, 1000, 1000, 1000, 1000});




        final int seconds = 5;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        if(!isScreenOn) {
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "AppName:com.muhammedalikocabey.reminders");
            wakeLock.acquire(seconds * 1000);
            PowerManager.WakeLock wakeLockCPU = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Appname:MyCpuLock");
            wakeLockCPU.acquire(seconds * 1000);
        }

        notificationManager.notify((int) currentReminder.getId(), notificationCompatBuilder.build());
        // ------------------   End Of Notification   ------------------


    }
}