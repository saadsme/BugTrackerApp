package com.example.bugtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BugListAll extends AppCompatActivity implements AdapterView.OnItemClickListener {

    FirebaseFirestore db; //TS member variable
    private ListView itemsListViewAll;
    private Boolean refreshingAll = false;
    private Boolean refreshingActive = false;
    ArrayList<Bug> searchingList = new ArrayList<Bug>();
    public EditText searchBar;
   TextView greeting;
    Boolean searching = false;
    TextView tv_head;

    @SuppressLint("MissingInflatedId")

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searching=false;
//        greeting = (TextView) findViewById(R.id.tv_greeting_all);
//        try{
//            String str = "Hello, " + getCurrentUser().getDisplayName();
//            greeting.setText(str);
//        }
//        catch(Exception e)
//        {
//            greeting.setText("Hello, User");
//        }

        setContentView(R.layout.activity_bug_list_all);
        FireStoreServices.initFireStore();
        itemsListViewAll = (ListView) findViewById(R.id.itemsListViewAll);
        itemsListViewAll.setOnItemClickListener(this);
        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                searching=true;
                searchAndDisplay(searchBar.getText().toString());

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        //TODO
        //Add a menu that will show us all bug history from the starting (even if they are resolved)
        //Add activity to view all active bugs (i think we can reuse the listview things from newsreader)
        //layout is made for adding a new bug page
        //main activity should be the list of all bugs, listed according to priority
        //we can have a button on the main activity or have in the menu - to enter a new bug.

    }

    protected void onResume() {
        refresh();
        searchBar.setText("");
        searching=false;
//        updateDisplay();
        super.onResume();
    }

    public void searchAndDisplay(String s){
        searchingList.clear();
        Log.d("Searching status", searching.toString());
        if (ApplicationController.AllBugs.isEmpty()) {
            itemsListViewAll.setAdapter(null);
            return;
        }


        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        for (Bug item : ApplicationController.AllBugs) { //TODO: get list of all the active Bugs and put it inside the Bugs ArrayList

            if(item.description.contains(s) || item.getTitle().contains(s)){
                searchingList.add(item);
                HashMap<String, String> map = new HashMap<String, String>();
                Date tStamp = new Date(Long.parseLong(item.getTimeStamp()));
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdfDate.format(tStamp);
                map.put("time", strDate);
                map.put("title", item.getTitle());
                map.put("priority", item.getPriority());
                map.put("status", item.getStatus());
                map.put("raisedBy", "Raised by: "+ item.getRaisedBy());
                data.add(map);
            }

        }
        if(data.isEmpty()){
            Toast.makeText(this, "Search returned no results :(",
                    Toast.LENGTH_SHORT).show();
        }
        // create the resource, from, and to variables
        int resource = R.layout.list_view_item;
        String[] from = {"time", "title", "priority", "raisedBy",  "status"};
        int[] to = {R.id.textViewTime, R.id.textViewTitle, R.id.textViewPriority, R.id.textViewRaisedBy, R.id.statusTextView};

        // create and set the adapter
        SimpleAdapter adapter =
                new SimpleAdapter(this, data, resource, from, to);
        itemsListViewAll.setAdapter(adapter);

        Log.d("CMP354", "DISPLAY UPDATED");
    }


    public void updateDisplayAll() {


        if (ApplicationController.AllBugs.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Bugs to Display",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        for (Bug item : ApplicationController.AllBugs) { //TODO: get list of all the active Bugs and put it inside the Bugs ArrayList
            HashMap<String, String> map = new HashMap<String, String>();
            Date tStamp = new Date(Long.parseLong(item.getTimeStamp()));
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdfDate.format(tStamp);
            map.put("time", strDate);
            map.put("title", item.getTitle());
            map.put("priority", String.valueOf(item.getPriority()));
            map.put("raisedBy", item.getRaisedBy());
            map.put("status", String.valueOf(item.getStatus()));
            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.list_view_item_all;
        String[] from = {"time", "title", "priority", "raisedBy", "status"};
        int[] to = {R.id.textViewTime, R.id.textViewTitle, R.id.textViewPriority, R.id.textViewRaisedBy, R.id.textViewStatus};

        // create and set the adapter
        SimpleAdapter adapter =
                new SimpleAdapter(this, data, resource, from, to);
        itemsListViewAll.setAdapter(adapter);

        Log.d("CMP354", "DISPLAY UPDATED");
    }

    public void stopRefreshAll()
    {
        this.refreshingAll = false;
    }

    public void refresh() {

        if(this.refreshingAll) return;
        FireStoreServices.getAllBugs(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNew:
                Toast.makeText(this, "Opening New Bug page",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AddBug.class);
                startActivity(intent);
                return true;
            case R.id.viewAll:
                Toast.makeText(this, "Already Viewing all Bugs, Refreshing",
                        Toast.LENGTH_SHORT).show();
                Intent intentAll = new Intent(this, BugListAll.class);
                startActivity(intentAll);
                return true;
            case R.id.viewActiveBugs:
                Toast.makeText(this, "Viewing active Bugs", Toast.LENGTH_SHORT).show();
                Intent intentActive = new Intent(this, BugListActive.class);
                startActivity(intentActive);
                return true;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(this, LoginActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Bug bug = ApplicationController.AllBugs.get(position);
        if(searching)
        {
            bug = searchingList.get(position);
            Log.d("Searching status", searching.toString());
        }
        ApplicationController.resolveBug=bug;



        Intent intent = new Intent(this, BugActivity.class);
        ApplicationController.bugListAll = this;
        intent.putExtra("title", bug.getTitle());
        intent.putExtra("priority", bug.getPriority());
        intent.putExtra("status", bug.getStatus());
        intent.putExtra("time", bug.getTimeStamp());
        intent.putExtra("description", bug.getDescription());

        this.startActivity(intent);
    }
}