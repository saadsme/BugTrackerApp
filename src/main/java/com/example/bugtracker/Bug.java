package com.example.bugtracker;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Bug {
    public String title;
    public String timeStamp;
    public String priority;
    public String description;
    public String status;
    public String raisedBy;

    public FirebaseUser getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
// Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
        }
        return user;
    }

    public String toString(){
        return "Bug{" +
                    "title='" + title + '\'' +
                    ", raisedBy='" + raisedBy + '\'' +
                    ", timeStamp=" + timeStamp +
                    ", priority=" + priority +
                    ", description=" + description +
                    ", status=" + status +
                    '}';

        }
    public Bug() {
        Date date = new Date();
        this.timeStamp = String.valueOf(date.getTime());
    }

    public Bug(String title, String description, String priority, String status){
        this.title= title;
        this.description=description;
        this.priority=priority;
        this.raisedBy=getCurrentUser().getDisplayName();
        this.status=status;
        Date date = new Date();
        this.timeStamp = String.valueOf(date.getTime());



    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
