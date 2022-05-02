package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class RideDitsActivity extends MenuActivity {
    travel ride;
    String username;

    TextView driver;
    TextView course;
    TextView time;
    TextView seats;
    TextView users;
    Button joinBtn;
    Button backBtn;

    boolean added = false;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_dits);

        driver = findViewById(R.id.usernameLabel);
        course = findViewById(R.id.courseLabel);
        time = findViewById(R.id.timeLabel);
        seats = findViewById(R.id.seatsLabel);
        users = findViewById(R.id.usersLabel);
        joinBtn = findViewById(R.id.button);
        backBtn = findViewById(R.id.backBtn);

        ride = (travel)getIntent().getExtras().get("travel");
        username = (String)getIntent().getExtras().get("username");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        if (ride == null)
        {
            Toast.makeText(RideDitsActivity.this, "ERROR", Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            driver.setText("User " + ride.getDriverName() + " Drives");
            course.setText("From " + ride.getSrc() + " to " + ride.getDst());
            time.setText("At: " + ride.getTravelDate().toString());
            seats.setText("With " + ride.getSeatsAvailable() + " Free Seats");
            ArrayList<String> arr = ride.getUsers();
            String usersStr = "Users already in the ride:\n";
            for (int i = 0; i < arr.size(); i++)
            {
                usersStr += arr.get(i) + "\n";
            }

            if (arr.size() == 0)
            {
                usersStr += "Ride's empty at the moment :)";
            }
            users.setText(usersStr);
        }

    }

    public void onClick(View v)
    {
        if (v == joinBtn)
        {
            // join ride on db
            Query q = myRef.child("rides").child(ride.getDriverName()).orderByKey();
            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!added)
                    {
                        added = true;
                        travel r = snapshot.getValue(travel.class);
                        if (r.getUsers() == null)
                            r.setUsers(new ArrayList<String>());
                        if (!r.getUsers().contains(username))
                        {
                            // add me
                            ArrayList<String> temp = r.getUsers();
                            temp.add(username);
                            r.setUsers(temp);
                            myRef.child("rides").child(r.getDriverName()).setValue(r);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // go to panel screen
            Intent itent = new Intent(RideDitsActivity.this, DriverPanelActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("username", username);
            b.putSerializable("driver", ride.getDriverName());
            itent.putExtras(b);
            startActivity(itent);
            finish();
        }
        else if (v == backBtn)
        {
            finish();
        }
    }
}
