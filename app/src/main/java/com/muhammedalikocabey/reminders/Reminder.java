package com.muhammedalikocabey.reminders;


import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;


@Entity(tableName = "reminder")
public class Reminder {


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reminder_id")
    public long id;


    @ColumnInfo(name = "reminder_firebase_id")
    public String firebaseId;


    @ColumnInfo(name = "reminder_created")
    public String createdDate;


    @ColumnInfo(name = "reminder_title")
    public String title;


    @ColumnInfo(name = "reminder_content")
    public String content;


    @ColumnInfo(name = "reminder_url")
    public String url;


    @Nullable
    @TypeConverters(DateConverter.class)
    @ColumnInfo(name = "reminder_date")
    public Date date;

    @ColumnInfo(name = "reminder_is_reminded")
    public boolean isReminded;


    @ColumnInfo(name = "reminder_flag")
    public boolean isFlagged;


    /*
        None
    !   Low
    !!  Medium
    !!! High
    */
    @ColumnInfo(name = "reminder_priority")
    public String priorityLevel;


    @ColumnInfo(name = "list_id")
    public long listId;





    public Reminder() {
    }


    public Reminder(String firebaseId, String createdDate, String title, String content, String url,
                    @Nullable Date date, boolean isReminded, boolean isFlagged, String priorityLevel, long listId) {
        this.firebaseId = firebaseId;
        this.createdDate = createdDate;
        this.title = title;
        this.content = content;
        this.url = url;
        this.date = date;
        this.isReminded = isReminded;
        this.isFlagged = isFlagged;
        this.priorityLevel = priorityLevel;
        this.listId = listId;
    }


    public long getId() {
        return id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Nullable
    public Date getDate() {
        return date;
    }

    public void setDate(@Nullable Date date) {
        this.date = date;
    }

    public boolean isReminded() {
        return isReminded;
    }

    public void setReminded(boolean reminded) {
        isReminded = reminded;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }


    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", firebaseId='" + firebaseId + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", date=" + date +
                ", isReminded=" + isReminded +
                ", isFlagged=" + isFlagged +
                ", priorityLevel='" + priorityLevel + '\'' +
                ", listId=" + listId +
                '}';
    }


    public Reminder reminderFirebaseToReminder(ReminderFirebaseObject frObj) {
        this.id = frObj.getReminderId();
        this.firebaseId = frObj.getFirebaseId();
        this.createdDate = frObj.getCreatedDate();
        this.title = frObj.getTitle();
        this.content = frObj.getContent();
        this.url = frObj.getUrl();

        if (frObj.getDate() != 0) {
            this.date = new Date(frObj.getDate());
        } else {
            this.date = null;
        }

        this.isReminded = frObj.isReminded();
        this.isFlagged = frObj.isFlagged();
        this.priorityLevel = frObj.getPriorityLevel();
        this.listId = frObj.getListId();

        return this;
    }
}

