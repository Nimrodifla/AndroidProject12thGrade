package com.example.finalproject;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class travel implements Serializable {

     private String driverName;
     private String src;
     private String dst;
     private Date travelDate;
     //private Time travelTime;
     private int seatsAvailable;
     private ArrayList<String> users;

    public travel() {
    }

    public travel(String driverName, String src, String dst, Date travelDate, /*Time travelTime, */int seats)
    {
        this.driverName = driverName;
        this.dst = dst;
        this.src = src;
        this.travelDate = travelDate;
        //this.travelTime = travelTime;
        this.seatsAvailable = seats;
        users = new ArrayList<>();
    }

    // getter & setters
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public Date getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(Date travelDate) {
        this.travelDate = travelDate;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public ArrayList<String> getUsers()
    {
        return this.users;
    }

    public void setUsers(ArrayList<String> users) { this.users = users; }

    // adds a user to the array
    public boolean addUser(String username)
    {
        if (this.seatsAvailable <= 0)
        {
            return false;
        }
        else
        {
            this.users.add(username);
            return true;
        }
    }

    // removes a user from the array
    public boolean removeUser(String username)
    {
        boolean check = this.users.remove(username);
        if (check)
        {
            this.seatsAvailable += 1;
            return true;
        }
        else
        {
            return false;
        }
    }
}
