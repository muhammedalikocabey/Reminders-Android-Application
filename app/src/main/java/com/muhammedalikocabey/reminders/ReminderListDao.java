package com.muhammedalikocabey.reminders;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface ReminderListDao {

    @Query("SELECT * FROM reminder_list")
    List<ReminderList> getAllReminderList();

    @Query("SELECT COUNT(reminder_list_id) FROM reminder_list")
    int getAllReminderListCount();

    @Query("SELECT * FROM reminder_list WHERE reminder_list_id = (:listId)")
    ReminderList findReminderListById(long listId);

    @Query("SELECT * FROM reminder_list WHERE reminder_list_name = (:listName)")
    ReminderList findReminderByName(String listName);

    @Query("SELECT * FROM reminder_list ORDER BY reminder_list_id DESC LIMIT 1")
    ReminderList getLatestReminderList();

    @Query("SELECT MAX(reminder_list_id) FROM reminder_list")
    int getLatestReminderListID();

    @Insert
    void insertOneReminderList(ReminderList reminderList);

    @Update
    void updateOneReminderList(ReminderList reminderList);

    @Delete
    void deleteOneReminderList(ReminderList reminderList);

}
