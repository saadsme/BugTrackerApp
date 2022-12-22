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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BugListActive extends AppCompatActivity implements AdapterView.OnItemClickListener {
    FirebaseFirestore db; //TS member variable
    private ListView  itemsListViewActive;
    private Boolean refreshingAll = false;
    private Boolean refreshingActive = false;
    private Boolean searching = false;
    public TextView greeting, prettyView;
    ArrayList<Bug> searchingList = new ArrayList<Bug>();
    EditText searchBar;
    TextView tv_head;
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
    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        greeting = findViewById(R.id.tv_greeting);
//        try{
//            String str = "Hello, " + getCurrentUser().getDisplayName();
//            greeting.setText(str);
//        }
//        catch(Exception e)
//        {
//            greeting.setText("Hello, User");
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buglist);
        FireStoreServices.initFireStore();
        itemsListViewActive = (ListView) findViewById(R.id.itemsListViewActive);
        itemsListViewActive.setOnItemClickListener(this);
        searchBar = findViewById(R.id.searchBar2);
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
        //refresh();
        searchBar.setText("");
        searching=false;
//        updateDisplay();
        super.onResume();

    }
    public void searchAndDisplay(String s){
        searchingList.clear();
        Log.d("Searching status", searching.toString());
        if (ApplicationController.AllActiveBugs.isEmpty()) {
            itemsListViewActive.setAdapter(null);
            return;
        }


        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        for (Bug item : ApplicationController.AllActiveBugs) { //TODO: get list of all the active Bugs and put it inside the Bugs ArrayList

            if(item.description.contains(s) || item.getTitle().contains(s)){
                searchingList.add(item);
                HashMap<String, String> map = new HashMap<String, String>();
                Date tStamp = new Date(Long.parseLong(item.getTimeStamp()));
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdfDate.format(tStamp);
                map.put("time", strDate);
                map.put("title", item.getTitle());
                map.put("priority", item.getPriority());

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
        String[] from = {"time", "title", "priority", "raisedBy"};
        int[] to = {R.id.textViewTime, R.id.textViewTitle, R.id.textViewPriority, R.id.textViewRaisedBy};

        // create and set the adapter
        SimpleAdapter adapter =
                new SimpleAdapter(this, data, resource, from, to);
        itemsListViewActive.setAdapter(adapter);

        Log.d("CMP354", "DISPLAY UPDATED");
    }

    public void updateDisplay() {
        if (ApplicationController.AllActiveBugs.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Active Bugs",
                    Toast.LENGTH_SHORT).show();
            itemsListViewActive.setAdapter(null);

            return;
        }


        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        for (Bug item : ApplicationController.AllActiveBugs) { //TODO: get list of all the active Bugs and put it inside the Bugs ArrayList
            HashMap<String, String> map = new HashMap<String, String>();
            Date tStamp = new Date(Long.parseLong(item.getTimeStamp()));
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdfDate.format(tStamp);
            map.put("time", strDate);
            map.put("title", item.getTitle());
            map.put("priority", item.getPriority());
            map.put("raisedBy", "Raised by: "+ item.getRaisedBy());
            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.list_view_item;
        String[] from = {"time", "title", "priority", "raisedBy"};
        int[] to = {R.id.textViewTime, R.id.textViewTitle, R.id.textViewPriority, R.id.textViewRaisedBy};

        // create and set the adapter
        SimpleAdapter adapter =
                new SimpleAdapter(this, data, resource, from, to);
        itemsListViewActive.setAdapter(adapter);

        Log.d("CMP354", "DISPLAY UPDATED");
    }

    public void stopRefreshActive()
    {
        this.refreshingActive = false;
    }

    public void refresh() {

        if(this.refreshingActive) return;
        FireStoreServices.getActiveBugs(this);


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
                Toast.makeText(this, "Viewing all Bugs",
                        Toast.LENGTH_SHORT).show();
                Intent intentAll = new Intent(this, BugListAll.class);
                startActivity(intentAll);
                return true;
            case R.id.viewActiveBugs:
                Toast.makeText(this, "Already Viewing Active Bugs, Refreshing", Toast.LENGTH_SHORT).show();
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
        Bug bug = ApplicationController.AllActiveBugs.get(position);
        if(searching)
        {
            bug = searchingList.get(position);
            Log.d("Searching status", searching.toString());
        }
        ApplicationController.resolveBug=bug;



        Intent intent = new Intent(this, BugActivity.class);
        ApplicationController.bugListActive = this;
        intent.putExtra("title", bug.getTitle());
        intent.putExtra("priority", bug.getPriority());
        intent.putExtra("status", bug.getStatus());
        intent.putExtra("time", bug.getTimeStamp());
        intent.putExtra("description", bug.getDescription());

        this.startActivity(intent);
    }
}
