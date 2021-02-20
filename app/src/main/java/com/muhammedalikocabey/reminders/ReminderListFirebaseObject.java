package com.muhammedalikocabey.reminders;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ReminderListFirebaseObject {

    private long listId;

    private String firebaseId;

    private String listName;


    public ReminderListFirebaseObject() {

    }

    public ReminderListFirebaseObject(String firebaseId, long listId, String listName) {
        this.listId = listId;
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
        return "ReminderListFirebaseObject{" +
                "listId=" + listId +
                ", firebaseId='" + firebaseId + '\'' +
                ", listName='" + listName + '\'' +
                '}';
    }




    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("listId", listId);
        result.put("firebaseId", firebaseId);
        result.put("listName", listName);


        return result;
    }

}
