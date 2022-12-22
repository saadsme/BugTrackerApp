package com.example.bugtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BugActivity extends AppCompatActivity implements View.OnClickListener {
    TextView titleTextView;
    TextView priorityTextView;
    TextView statusTextView;
    TextView timeTextView;
    TextView descriptionTextView;
    Button btn_resolve;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug);
        titleTextView = findViewById(R.id.titleTextView);
        priorityTextView = findViewById(R.id.priorityTextView);
        statusTextView = findViewById(R.id.statusTextView);
        timeTextView = findViewById(R.id.timeTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        btn_resolve = findViewById(R.id.btn_resolve);


        Intent i = getIntent();
        title = i.getStringExtra("title");
        String priority = i.getStringExtra("priority");
        String status = i.getStringExtra("status");

        if(status.equals("Resolved")) btn_resolve.setVisibility(View.GONE);
        String time = i.getStringExtra("time");
        Date tStamp = new Date(Long.parseLong(time));
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdfDate.format(tStamp);
        String description = i.getStringExtra("description");

        titleTextView.setText(title);
        priorityTextView .setText(priority);
        statusTextView.setText(status);
        timeTextView.setText(strDate);
        descriptionTextView.setText(description);

        btn_resolve.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        AlertDialog.Builder Builder = new AlertDialog.Builder(BugActivity.this ) ;

        String time = timeTextView.getText().toString();
        Log.d("CMP354", "THE TIME STAMP IS " + time);
        Builder.setTitle("Resolve") ;
        Builder.setMessage("Press Confirm to resolve the following Bug: " + title + ".");
        Builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Bug> allBugs = new ArrayList<Bug>();
                allBugs = ApplicationController.AllActiveBugs;
               // for(int i=0 ;i<allBugs.size();i++){
                 //   if(allBugs.get(i).getTimeStamp().equals(time)){
                        FireStoreServices.resolveBug(ApplicationController.resolveBug, ApplicationController.bugListActive);
                        Log.d("CMP354", "THE BUG WITH TIME" + ApplicationController.resolveBug.getTimeStamp() + "IS NOW WITH STATUS" + ApplicationController.resolveBug.getStatus());

                        Intent intent = new Intent(getApplicationContext(), BugListActive.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
//                        FireStoreServices.getAllBugs(ApplicationController.bugList);
//                        FireStoreServices.getActiveBugs(ApplicationController.bugList);
                    }
                //}
            //}
        });
        Builder.show();

    }
}