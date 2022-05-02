package com.example.finalproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Firebase_Notification extends Service {

    FirebaseDatabase database = FirebaseDatabase.getInstance();;
    DatabaseReference myRef = database.getReference();
    Query q;
    NotificationCompat.Builder builder;
    NotificationManager manager;

    public Firebase_Notification() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        builder = new NotificationCompat.Builder(this)
                .setContentTitle("Vroom Vroom!")
                .setContentText("Someone joined or left your ride!")
                .setSmallIcon(R.drawable.notf_icon)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        Intent notification_intent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notification_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Query qMain = myRef.orderByKey();
        qMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    q = myRef.child("rides").child(MainActivity.username).orderByKey();
                    q.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            travel temp = snapshot.getValue(travel.class);
                            if (temp != null) // user does own a drive
                            {
                                int currentLen = 0;
                                if (temp.getUsers() != null) // if null, is 0
                                {
                                    currentLen = temp.getUsers().size();
                                }
                                int prevLen = DriverPanelActivity.numOfUsers;
                                Toast.makeText(getBaseContext(), "numOfUsers: " + prevLen + " currLen: " + currentLen, Toast.LENGTH_LONG).show();
                                if (currentLen != prevLen)
                                {
                                    // ---- SEND NOTIFICATION HERE! ----
                                    DriverPanelActivity.numOfUsers = currentLen; // update prev
                                    manager.notify(0,builder.build());
                                    //Toast.makeText(getBaseContext(), "Equaled! to: " + currentLen, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                catch (Exception error)
                {
                    //Toast.makeText(getBaseContext(), "Catch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return Service.START_STICKY_COMPATIBILITY;
    }
    @Override
    public void onDestroy() {
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    public void onStop(){
    }
}