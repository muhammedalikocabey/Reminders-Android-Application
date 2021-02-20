package com.muhammedalikocabey.reminders;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = ReminderList.class, version = 2)
@TypeConverters({DateConverter.class})
public abstract class ReminderListDatabase extends RoomDatabase {

    public abstract ReminderListDao reminderListDao();

    private static volatile ReminderListDatabase INSTANCE;


    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);



    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE reminder_list ADD COLUMN reminder_list_firebase_id TEXT NOT NULL 'AAA'");
        }
    };


    static ReminderListDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ReminderListDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ReminderListDatabase.class, "reminder_list")
                            .addMigrations(MIGRATION_1_2)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}