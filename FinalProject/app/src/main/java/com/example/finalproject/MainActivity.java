package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends MenuActivity {
    ListView lv_travels;
    ArrayList<travel> travelsList;
    TravelAdapter travelAdapter;

    FirebaseDatabase database;
    DatabaseReference myRef;

    boolean searchBtnClearSearch = false;
    Button searchBtn;
    Button signOutBtn;
    Button createBtn;
    EditText fromSearch;
    EditText toSearch;
    static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_travels = findViewById(R.id.lv_travels);
        travelsList = new ArrayList<>();

        searchBtn = (Button) findViewById(R.id.searchBtn);
        fromSearch = findViewById(R.id.fromSearch);
        toSearch = findViewById(R.id.toSearch);
        signOutBtn = findViewById(R.id.signOutBtn);
        createBtn = findViewById(R.id.createBtn);

        // get data from prev activity
        username = (String) getIntent().getExtras().get("username");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        // ListView Item Click Listener
        lv_travels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                travel t = travelsList.get(i);
                // make sure arr is not null, but empty
                if (t.getUsers() == null)
                {
                    t.setUsers(new ArrayList<String>());
                }

                // open rideDetails activity of the selected drive
                Intent intent = new Intent(MainActivity.this, RideDitsActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("travel", t);
                b.putSerializable("username", username);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        // check if the user is currently owning a ride
        // if it is - open driverPanelActivity
        // or, if the user is In a drive (not as a driver)
        Query q2 = myRef.child("rides").orderByKey();
        q2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check for owning a drive
                boolean isOwningDrive = false;
                for (DataSnapshot dst : snapshot.getChildren()) {
                    if (dst.getKey().compareTo(username) == 0)
                    {
                        isOwningDrive = true;
                    }
                }

                // check if IN a drive
                if (isOwningDrive == true)
                {
                    Intent intent = new Intent(MainActivity.this, DriverPanelActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("driver", username);
                    b.putSerializable("username", username);
                    intent.putExtras(b);
                    startActivity(intent);
                }
                else {
                    boolean isInDrive = false;
                    // check if IN a drive
                    String tempDriver = "";
                    for (DataSnapshot dst : snapshot.getChildren()) {
                        ArrayList<String> temp = dst.getValue(travel.class).getUsers();
                        if (temp != null && temp.contains(username))
                        {
                            isInDrive = true;
                            tempDriver = dst.getKey(); // save driver name
                        }
                    }
                    if (isInDrive == true)
                    {
                        Intent intent = new Intent(MainActivity.this, DriverPanelActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("driver", tempDriver);
                        b.putSerializable("username", username);
                        intent.putExtras(b);
                        startActivity(intent);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    protected void onResume()
    {
        super.onResume();
        // automaticly update listview to view current available drives
        Query q = myRef.child("rides").orderByKey();
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                travelsList.clear();
                for (DataSnapshot dst : snapshot.getChildren()) {
                    travel loadedRide = dst.getValue(travel.class);
                    if (loadedRide.getUsers() == null)
                    {
                        loadedRide.setUsers(new ArrayList<String>());
                    }
                    travelsList.add(loadedRide);
                }

                travelAdapter = new TravelAdapter(MainActivity.this,0,0, travelsList);
                lv_travels.setAdapter(travelAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClick(View v)
    {
        if (v == searchBtn)
        {
            if (searchBtnClearSearch) // btn is in clear mode
            {
                // clear search
                fromSearch.setText("");
                toSearch.setText("");

                // update listView to show all
                TravelAdapter travelAdapter = new TravelAdapter(this,0,0,travelsList);
                lv_travels.setAdapter(travelAdapter);

                searchBtnClearSearch = false;
                ((Button)v).setText("Search Rides");
            }
            else // btn IS NOT in clear mode
            {
                String from = fromSearch.getText().toString();
                String to = toSearch.getText().toString();

                ArrayList<travel> searchRes = new ArrayList<>();
                boolean errFlag = false;

                if (to.length() == 0 && from.length() == 0)
                {
                    // empty search fields
                    errFlag = true;
                }
                else if (from.length() == 0)
                {
                    // search by TO
                    for (int i = 0; i < travelsList.size(); i++)
                    {
                        if (travelsList.get(i).getDst().toLowerCase().compareTo(to.toLowerCase()) == 0)
                        {
                            searchRes.add(travelsList.get(i));
                        }
                    }
                }
                else if (to.length() == 0)
                {
                    // search by FROM
                    for (int i = 0; i < travelsList.size(); i++)
                    {
                        if (travelsList.get(i).getSrc().toLowerCase().compareTo(from.toLowerCase()) == 0)
                        {
                            searchRes.add(travelsList.get(i));
                        }
                    }
                }
                else
                {
                    // search by TO & FROM
                    for (int i = 0; i < travelsList.size(); i++)
                    {
                        if (travelsList.get(i).getSrc().toLowerCase().compareTo(from.toLowerCase()) == 0 && travelsList.get(i).getDst().toLowerCase().compareTo(to.toLowerCase()) == 0)
                        {
                            searchRes.add(travelsList.get(i));
                        }
                    }
                }

                if (!errFlag)
                {
                    TravelAdapter travelAdapter = new TravelAdapter(this,0,0,searchRes);
                    lv_travels.setAdapter(travelAdapter);

                    searchBtnClearSearch = true;
                    ((Button)v).setText("Clear Search");
                }
            }
        }
        else if (v == signOutBtn)
        {
            // close screen
            finish();
        }
        else if (v == createBtn)
        {
            // go to createDrive activity
            Intent intent = new Intent(MainActivity.this, CreateRideActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("username", username);
            intent.putExtras(b);
            startActivity(intent);
        }
    }
}