package com.example.finalproject;

import java.util.Date;

public class Helper {
    // adds '0' to the start of a string to achieve the desired length of a string
    public static String padder(String str, int targetLength)
    {
        String res = "";
        for (int i = 0; i < targetLength - str.length(); i++){
            res += "0";
        }
        res += str;
        return  res;
    }

    // parse date to a string
    public static String DateToString(Date d)
    {
        String res = "";
        res = d.getDate() + " / " + (d.getMonth() + 1) + " / " + (d.getYear() + 2022 - 122) + " - " + padder("" + d.getHours(), 2) + ":" + padder("" + d.getMinutes(), 2);
        return res;
    }
}
