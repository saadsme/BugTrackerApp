package com.example.bugtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddBug extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener{
    EditText et_title;
    EditText et_desc;
    SeekBar priorityBar;
    TextView tv_priorityNum;

    Spinner statusSpinner;

    Button btn_Add, btn_Reset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bug);
        et_title = (EditText) findViewById(R.id.et_title);
        et_desc = (EditText) findViewById(R.id.et_desc);

        priorityBar = (SeekBar) findViewById(R.id.priorityBar);
        priorityBar.setOnSeekBarChangeListener(this);

        tv_priorityNum = (TextView) findViewById(R.id.tv_priorityNum);

        statusSpinner = (Spinner) findViewById(R.id.statusSpinner); // get a reference to the spinner


        btn_Add = findViewById(R.id.btn_Add);
        btn_Add.setOnClickListener(this);


        // create array adapter for specified array and layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        // set the layout for the drop-down list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // set the adapter for the spinner
        statusSpinner.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_Add) {
            Toast.makeText(this, "adding bug",
                    Toast.LENGTH_SHORT).show();
            String Btitle = String.valueOf(et_title.getText());
            String Bdescription = String.valueOf(et_desc.getText());
            String Bpriority = String.valueOf(tv_priorityNum.getText());
            //String BraisedBy = String.valueOf(et_raisedBy.getText());
            String Bstatus = String.valueOf(statusSpinner.getSelectedItem());


            Bug b = new Bug(Btitle, Bdescription, Bpriority, Bstatus);
            FireStoreServices.addBug(b);
            finish();
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch(i){
            case 1: tv_priorityNum.setText("Low"); break;
            case 2: tv_priorityNum.setText("Moderate"); break;
            case 3: tv_priorityNum.setText("Major"); break;
            case 4: tv_priorityNum.setText("High"); break;
            case 5: tv_priorityNum.setText("Critical"); break;
            default: tv_priorityNum.setText("None");
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}