package com.example.finalproject;

import static android.view.FrameMetrics.ANIMATION_DURATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends MenuActivity {
    NoInternetReciever reciever = new NoInternetReciever();
    Button signInBtn;
    Button registerBtn;
    EditText usernameET;
    EditText passwordET;
    TextView bigTitle;
    FirebaseDatabase database;
    DatabaseReference myRef;
    boolean exists;
    boolean correctPass;
    String u;
    String p;
    Intent notification;
    int timesTheScreenWasOn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //registerReceiver(reciever, filter);

        signInBtn = findViewById(R.id.signin);
        registerBtn = findViewById(R.id.register);
        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);
        bigTitle = findViewById(R.id.bigTitle);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    protected void onResume()
    {
        super.onResume();
        timesTheScreenWasOn++;

        notification = new Intent(StartActivity.this, Firebase_Notification.class);
        startService(notification);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(reciever, filter);

        if (timesTheScreenWasOn != 1) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(bigTitle, "translationY", 0f);
            //animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(0);
            animator.start();
        }

    }

    public void RegisterUser(String username, String password) {
        Query q = myRef.child("users").orderByKey();
        exists = false;
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dst : snapshot.getChildren())
                {
                    User u = dst.getValue(User.class);
                    if (u.getUsername().equals(username))
                    {
                        exists = true;
                    }
                }
                // check user doesnt exist
                if (exists == false) {
                    // Write a message to the database
                    myRef.child("users").child(username).child("password").setValue(password);
                    myRef.child("users").child(username).child("username").setValue(username);

                    SignInUser(u, p);
                }
                else
                {
                    Toast.makeText(StartActivity.this, "Couldn't register...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void SignInUser(String username, String password) {
        Query q = myRef.child("users").orderByKey();
        exists = false;
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dst : snapshot.getChildren())
                {
                    User u = dst.getValue(User.class);
                    if (u.getUsername().equals(username))
                    {
                        exists = true;
                    }
                }
                // check user exists
                if (exists) {
                    // check password is correct
                    Query q = myRef.child("users").orderByKey();
                    correctPass = false;
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dst : snapshot.getChildren())
                            {
                                User u = dst.getValue(User.class);
                                if (u.getUsername().equals(username) && u.getPassword().equals(password))
                                {
                                    correctPass = true;
                                }
                            }

                            if (correctPass)
                            {
                                int time = 1000;
                                ObjectAnimator animator = ObjectAnimator.ofFloat(bigTitle, "translationY", -500f);
                                animator.setInterpolator(new AccelerateInterpolator());
                                animator.setDuration(time);
                                animator.start();
                                new CountDownTimer(time * 2, time + 1) {
                                    @Override
                                    public void onTick(long l) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        OpenMainScreen();
                                    }
                                }.start();
                            }
                            else
                            {
                                Toast.makeText(StartActivity.this, "Sign in failed...", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    Toast.makeText(StartActivity.this, "Sign in failed...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void OnClick(View v) {
        u = String.valueOf(usernameET.getText());
        p = String.valueOf(passwordET.getText());

        if (v == registerBtn) {
            RegisterUser(u, p);
        } else if (v == signInBtn) {
            SignInUser(u, p);
        }
    }

    public void OpenMainScreen()
    {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("username", u);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(reciever);
    }
}