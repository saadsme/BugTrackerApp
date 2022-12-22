package com.example.bugtracker;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ApplicationController extends Application {
    public void onCreate() {
        super.onCreate();
        FireStoreServices.initFireStore();
        Log.d("News Reader", "App Started");
        Intent serviceIntent = new Intent(this, MyService.class);
        ContextCompat.startForegroundService(this, serviceIntent); }


    public static ArrayList<Bug> AllBugs= new ArrayList<Bug>();
    public static ArrayList<Bug> newBugList= new ArrayList<Bug>();
    public static ArrayList<Bug> AllActiveBugs= new ArrayList<Bug>();
    public static BugListActive bugListActive;
    public static BugListAll bugListAll;

    public static Bug resolveBug;

    public static Boolean compareLists()
    {
        if(AllActiveBugs.size()!= newBugList.size())
            return false;
        for(int i=0;i<AllActiveBugs.size();i++)
        {
            if(!AllActiveBugs.get(i).getTimeStamp().equals(newBugList.get(i).getTimeStamp()))
            {
                Log.d("NON MATCHING BUG", AllActiveBugs.get(i).toString());
                return false;
            }



        }
        return true;
    }


}
