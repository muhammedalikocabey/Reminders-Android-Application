package com.muhammedalikocabey.reminders;

import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReminderFirebaseObject {

    private long reminderId;

    private String firebaseId;

    private String createdDate;

    private String title;

    private String content;

    private String url;

    private long date;

    private boolean isReminded;

    private boolean isFlagged;

    private String priorityLevel;

    public long listId;


    public ReminderFirebaseObject() {

    }


    public ReminderFirebaseObject(long reminderId) {
        this.reminderId = reminderId;
    }


    public long getReminderId() {
        return reminderId;
    }

    public void setReminderId(long reminderId) {
        this.reminderId = reminderId;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isReminded() {
        return isReminded;
    }

    public void setReminded(boolean isReminded) {
        isReminded = isReminded;
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
        return "ReminderFirebaseObject{" +
                "reminderId=" + reminderId +
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


    public ReminderFirebaseObject reminderToReminderFirebase(Reminder reminder) {
        this.reminderId = reminder.getId();
        this.firebaseId = reminder.getFirebaseId();
        this.createdDate = reminder.getCreatedDate();
        this.title = reminder.getTitle();
        this.content = reminder.getContent();
        this.url = reminder.getUrl();

        if (reminder.getDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reminder.getDate());
            this.date = calendar.getTimeInMillis();
        } else {
            this.date = 0;
        }

        this.isReminded = reminder.isReminded;
        this.isFlagged = reminder.isFlagged();
        this.priorityLevel = reminder.getPriorityLevel();
        this.listId = reminder.getListId();


        return this;
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("reminderId", reminderId);
        result.put("firebaseId", firebaseId);
        result.put("createdDate", createdDate);
        result.put("title", title);
        result.put("content", content);
        result.put("url", url);
        result.put("date", date);
        result.put("isReminded", isReminded);
        result.put("reminded", isReminded);
        result.put("isFlagged", isFlagged);
        result.put("priorityLevel", priorityLevel);
        result.put("listId", listId);


        return result;
    }

}
