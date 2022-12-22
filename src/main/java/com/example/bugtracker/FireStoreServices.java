package com.example.bugtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FireStoreServices extends AppCompatActivity{
    public static FirebaseFirestore db;
    public static CollectionReference bugs;

    //call in oncreate of main activity and any other activity where firestore is used
    public static void initFireStore()
    {
        db = FirebaseFirestore.getInstance();
        bugs = db.collection("bugsList");
//        Toast.makeText(getApplicationContext(), "firestone init",
//                Toast.LENGTH_SHORT).show();
        Log.d("test", "init complete");
    }
    
    public static void addBug(Bug bug)
    {

        Map<String, Object> data = new HashMap<>();
        data.put("title", bug.title);
        data.put("raisedBy", bug.raisedBy);
        data.put("timeStamp", bug.timeStamp);
        data.put("priority", bug.priority);
        data.put("description", bug.description);
        data.put("status", bug.status);
        bugs.add(data);
       
    }

    public static void getAllBugs(BugListAll bugListAll) {
        ApplicationController.AllBugs.clear();
        bugs.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               // Log.d("CMP354:", document.getId() + " => " + document.toObject(Bug.class));
                                Bug b = document.toObject(Bug.class);
                                ApplicationController.AllBugs.add(b);
                                Log.d("CMP354:",  "got teh Object" + b);
                            }
                        } else {
                            Log.d("CMP354:", "Error getting documents: ", task.getException());
                        }
                    bugListAll.updateDisplayAll();
                        bugListAll.stopRefreshAll();
                    }
                });

    }
    public static void getActiveBugs(BugListActive bugList) {
        ApplicationController.AllActiveBugs.clear();
        bugs.whereEqualTo("status", "Open").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("CMP354:", document.getId() + " => " + document.toObject(Bug.class));
                                Bug b = document.toObject(Bug.class);
                                ApplicationController.AllActiveBugs.add(b);
                                Log.d("CMP354:",  "got the Object" + b);
                            }
                        } else {
                            Log.d("CMP354:", "Error getting documents: ", task.getException());
                        }
                        bugList.updateDisplay();
                        bugList.stopRefreshActive();
                    }
                });

    }

    public static void getActiveBugsList() {
        ApplicationController.newBugList.clear();
        bugs.whereEqualTo("status", "Open").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("CMP354:", document.getId() + " => " + document.toObject(Bug.class));
                                Bug b = document.toObject(Bug.class);
                                ApplicationController.newBugList.add(b);
                                Log.d("CMP354:",  "got the Object" + b);
                            }
                        } else {
                            Log.d("CMP354:", "Error getting documents: ", task.getException());
                        }


                    }
                });

    }

    public static void resolveBug(Bug bug, BugListActive givenContext){
    bugs.whereEqualTo("timeStamp",bug.getTimeStamp())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("SuspiciousIndentation")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("CMP354:", document.getId() + " => " + document.toObject(Bug.class));
                            Log.d("RESOLVEBUG!", document.getId() + " => " + document.getData());
                            Map<String, Object> data = new HashMap<>();
                            data.put("title", bug.title);
                            data.put("raisedBy", bug.raisedBy);
                            data.put("timeStamp", bug.timeStamp);
                            data.put("priority", bug.priority);
                            data.put("description", bug.description);
                            data.put("status", "Resolved");
                            Log.d("Found Document", bugs.document(document.getId()).toString());
                            bugs.document(document.getId()).set(data);
                          if(ApplicationController.bugListActive != null) ApplicationController.bugListActive.refresh();
                            if(ApplicationController.bugListAll != null) ApplicationController.bugListAll.refresh();
                        }
                    } else {
                        Log.d("CMP354:", "Error getting documents: ", task.getException());
                    }
                }
                
            });
        //ApplicationController.bugListActive.refresh();

//        FireStoreServices.getActiveBugs(givenContext);
//        FireStoreServices.getAllBugs(givenContext);
}

}
