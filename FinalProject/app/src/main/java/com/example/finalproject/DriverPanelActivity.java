package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DriverPanelActivity extends MenuActivity {

    travel ride;
    String username;

    String driverName;

    FirebaseDatabase database;
    DatabaseReference myRef;

    TextView title;
    TextView srcText;
    TextView dstText;
    TextView dateTimeText;
    TextView usersText;

    Button exitBtn;

    boolean nowClosing = false;
    boolean isDriver = false;

    static int numOfUsers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_panel);

        title = findViewById(R.id.paneltitle);
        srcText = findViewById(R.id.srcTxt);
        dstText = findViewById(R.id.dstTxt);
        dateTimeText = findViewById(R.id.dateTimeTxt);
        usersText = findViewById(R.id.usersInDriveTxt);
        exitBtn = findViewById(R.id.deleteAndExit);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        username = (String)getIntent().getExtras().get("username");
        driverName = (String)getIntent().getExtras().get("driver");
        
        // is the user driver or a passenger
        if (username.compareTo(driverName) == 0)
        {
            isDriver = true;
        }

        if (!isDriver) {
            title.setText("Driver: " + driverName);
            exitBtn.setText("Leave Ride");
        }
        else {
            title.setText("Your Drive");
        }

        // automaticly update dits when drive is changing on DB
        Query q = myRef.child("rides").child(driverName).orderByKey();
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!nowClosing)
                {
                    ride = snapshot.getValue(travel.class);
                    updateDitsOnView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    public void updateDitsOnView()
    {
        if (ride == null)
        {
            // the drives has been deleted by owner, exit!
            Toast.makeText(DriverPanelActivity.this, "Driver closed the drive... Bye!", Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            srcText.setText("Ride from: " + ride.getSrc());
            dstText.setText("Ride to: " + ride.getDst());
            dateTimeText.setText("At: " + Helper.DateToString(ride.getTravelDate()));

            if (ride.getUsers() != null)
            {
                String text = "Users In Your Drive:";
                for (int i = 0; i < ride.getUsers().size(); i++)
                {
                    text += "\n" + (i+1) + ". " + ride.getUsers().get(i);
                }
                numOfUsers = ride.getUsers().size(); // update static numOfUsers

                usersText.setText(text);
            }
            else
            {
                usersText.setText("Currently There Are No Users In Your Drive :)");
            }
        }
    }

    public void onClick(View v)
    {
        if (v == exitBtn)
        {
            if (isDriver)
            {
                nowClosing = true;
                // delete ride
                myRef.child("rides").child(ride.getDriverName()).removeValue();
                // exit
                finish();
            }
            else
            {
                // leave ride on db
                Query q = myRef.child("rides").child(driverName).orderByKey();
                q.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!nowClosing)
                        {
                            ride = snapshot.getValue(travel.class);
                            if (ride.getUsers() == null)
                                ride.setUsers(new ArrayList<String>());
                            else if (ride.getUsers().contains(username))
                            {
                                nowClosing = true;
                                // remove me
                                ArrayList<String> temp = ride.getUsers();
                                temp.remove(username);
                                ride.setUsers(temp);
                                myRef.child("rides").child(driverName).setValue(ride);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // exit
                finish();
            }
        }
    }
}