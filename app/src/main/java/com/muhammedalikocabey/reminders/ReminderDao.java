package com.muhammedalikocabey.reminders;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface ReminderDao {
    @Query("SELECT * FROM reminder")
    List<Reminder> getAllReminders();

    @Query("SELECT COUNT(reminder_id) FROM reminder")
    int getAllRemindersCount();


    @Query("SELECT * FROM reminder WHERE reminder_id = (:reminderId)")
    Reminder findReminderById(long reminderId);


    @Query("SELECT MAX(reminder_id) FROM reminder")
    int getLatestReminderID();


    @Query("SELECT * FROM reminder WHERE reminder_created = :today")
    List<Reminder> getRemindersCreatedToday(String today);

    @Query("SELECT COUNT(reminder_id) FROM reminder WHERE reminder_created = :today")
    int getRemindersCreatedTodayCount(String today);


    // When Search add % end and start of parameter
    @Query("SELECT * FROM reminder WHERE (lower(reminder_content) " +
            "LIKE (:reminderSearch) OR lower(reminder_title) LIKE (:reminderSearch))")
    List<Reminder> searchRemindersByContent(String reminderSearch);


    @Query("SELECT * FROM reminder WHERE reminder_date IS NOT NULL ")
    List<Reminder> getTimedReminders();

    @Query("SELECT COUNT(reminder_id) FROM reminder WHERE reminder_date IS NOT NULL")
    int getTimedRemindersCount();


    @Query("SELECT * FROM reminder WHERE reminder_flag = 1")
    List<Reminder> getFlaggedReminders();

    @Query("SELECT COUNT(reminder_id) FROM reminder WHERE reminder_flag = 1")
    int getFlaggedRemindersCount();



    @Query("SELECT * FROM reminder WHERE list_id = (:listId)")
    List<Reminder> findRemindersByListId(long listId);

    @Query("SELECT COUNT(reminder_id) FROM reminder WHERE list_id = (:listId)")
    int getReminderCountByListId(long listId);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOneReminder(Reminder reminder);


    @Update
    void updateOneReminder(Reminder reminder);


    @Delete
    void deleteOneReminder(Reminder reminder);


    @Query("DELETE FROM reminder WHERE list_id = (:listId)")
    void deleteAllReminderFromListByListId(long listId);
}
