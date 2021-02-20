package com.muhammedalikocabey.reminders;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "reminder_list")
public class ReminderList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reminder_list_id")
    public long listId;

    @ColumnInfo(name = "reminder_list_firebase_id")
    public String firebaseId;

    @ColumnInfo(name = "reminder_list_name")
    public String listName;


    public ReminderList(String firebaseId, String listName) {
        this.firebaseId = firebaseId;
        this.listName = listName;
    }


    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }


    @Override
    public String toString() {
        return "ReminderList{" +
                "listId=" + listId +
                ", firebaseId='" + firebaseId + '\'' +
                ", listName='" + listName + '\'' +
                '}';
    }
}
