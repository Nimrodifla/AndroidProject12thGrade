package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CreateRideActivity extends MenuActivity {

    EditText src;
    EditText dst;
    EditText seats;
    Time time;
    Date dateChosen;
    EditText date;
    Button createBtn;
    Button cancelBtn;
    Button dateBtn;
    Button timeBtn;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride);

        // link xml elements
        dateBtn = findViewById(R.id.dateBtn);
        timeBtn = findViewById(R.id.timeBtn);
        createBtn = findViewById(R.id.createRideBtn);
        cancelBtn = findViewById(R.id.cancelRideBtn);
        src = findViewById(R.id.src);
        dst = findViewById(R.id.dst);
        seats = findViewById(R.id.editTextNumber);

        username = (String)getIntent().getExtras().get("username");
    }

    public void onClick(View v)
    {
        if (v == createBtn)
        {
            // create ride
            // merge date & time
            Calendar c = new GregorianCalendar(dateChosen.getYear() + (2022 - 122), dateChosen.getMonth() , dateChosen.getDate(), time.getHours(), time.getMinutes());
            travel t = new travel(username, src.getText().toString(), dst.getText().toString(), c.getTime(), Integer.valueOf(seats.getText().toString()));
            createRide(t);
        }
        else if (v == cancelBtn)
        {
            finish();
        }
        else if (v == dateBtn)
        {

            final View dialogView = View.inflate(CreateRideActivity.this, R.layout.date_time_layout, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(CreateRideActivity.this).create();

            dialogView.findViewById(R.id.doneDateBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);

                    Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth());

                    dateChosen = calendar.getTime();
                    alertDialog.dismiss();
                }});
            alertDialog.setView(dialogView);
            alertDialog.show();
        }
        else if (v == timeBtn)
        {
            final View dialogView = View.inflate(CreateRideActivity.this, R.layout.time_layout, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(CreateRideActivity.this).create();

            dialogView.findViewById(R.id.doneTimeBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                    time = new Time(timePicker.getHour(), timePicker.getMinute(), 0);

                    alertDialog.dismiss();
                }});
            alertDialog.setView(dialogView);
            alertDialog.show();
        }
    }

    public void createRide(travel ride)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("rides").child(ride.getDriverName()).setValue(ride);
        for (int i = 0; i < ride.getUsers().size(); i++)
        {
            myRef.child("rides").child(ride.getDriverName()).child("users").child(String.valueOf(i)).setValue(ride.getUsers().get(i));
        }

        // open driver panel
        Intent intent = new Intent(CreateRideActivity.this, DriverPanelActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("username", username);
        b.putSerializable("driver", username);
        intent.putExtras(b);
        startActivity(intent);

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        time = new Time(0, 0, 0);
        dateChosen = new Date();
    }
}