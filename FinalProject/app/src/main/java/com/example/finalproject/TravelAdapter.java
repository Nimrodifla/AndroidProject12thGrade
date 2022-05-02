package com.example.finalproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TravelAdapter extends ArrayAdapter<travel> {
    Context context;
    ArrayList<travel> arr_travels;

    public TravelAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ArrayList<travel> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        arr_travels = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.one_travel, parent, false);

        TextView txt = view.findViewById(R.id.txt);
        TextView time = view.findViewById(R.id.time);

        travel temp = arr_travels.get(position);

        txt.setText(temp.getDriverName() + ": " + temp.getSrc() + " -> " + temp.getDst());
        time.setText(Helper.DateToString(temp.getTravelDate()));

        return view;
    }
}
