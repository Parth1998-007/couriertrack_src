package com.couriertrack.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {
    private static final String TAG = "DateTimeHelper";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";


    public static String convertFormat(String strDate, String format, String convertFormat)
    {
        SimpleDateFormat sdf=new SimpleDateFormat(format, Locale.getDefault());
        SimpleDateFormat sdfNew=new SimpleDateFormat(convertFormat, Locale.getDefault());

        try {
            Date date=sdf.parse(strDate);
            return sdfNew.format(date);
        } catch (ParseException e) {
            Log.e(TAG,e.toString());
            return "";
        }
    }
    public static String convertFormat(Date date,  String convertFormat)
    {
        SimpleDateFormat sdfNew=new SimpleDateFormat(convertFormat, Locale.getDefault());
        return sdfNew.format(date);
    }
    public static String getDate(String format)
    {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat(format, Locale.getDefault());
            Date date=new Date();
            return sdf.format(date);
        }catch (IllegalArgumentException e)
        {
            Log.e(TAG,e.toString());
            return "";
        }
    }

    public static String getAge(String strDate)
    {
        SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(strDate));

            int diff  = Calendar.getInstance().get(Calendar.YEAR) -  calendar.get(Calendar.YEAR);
            return  diff+"";
        } catch (ParseException e) {
            Log.e(TAG,e.toString());
            return "";
        }
    }
}