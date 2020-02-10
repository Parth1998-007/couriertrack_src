package com.couriertrack.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

public class AppPref {
    public static String UPDATESHOWWORKINGMSG = "UPDATESHOWWORKINGMSG";
    public static String WORKINGTIMEMSG = "WORKINGTIMEMSG";
    public static String OTP = "OTP";
    public static String USERSTATUS = "USERSTATUS";
    public static String BACKIMAGE = "BACKIMAGE";
    public static String FRONTIMAGE = "FRONTIMAGE";
    public static String DOCNUMBER = "DOCNUMBER";
    public static String DOCTYPE = "DOCTYPE";
    public static String MOBILE = "MOBILE";
    public static String GENDER = "GENDER";
    public static String API_KEY = "API_KEY";
    public static String FCM_TOKEN = "FCM_TOKEN";
    public static String USER_ID = "USER_ID";
    public static String NAME = "NAME";
    public static String EMAIL = "EMAIL";
    public static String USER_TYPE = "USER_TYPE";
    public static String IS_LOGIN = "IS_LOGIN";
    public static String USER_Latitude = "Latitude";
    public static String USER_Longitude = "Longitude";
    public static String USER_PROFILE = "USER_PROFILE";

    public static String OTPFOR_SIGNUP = "signup";
    public static String OTPFOR_FORGOTPASS = "forgotpassword";
    public static String OTPFOR_SENDER = "senderOTP";
    public static String OTPFOR_RECEIVER = "receiverOTP";

    public static String SCREEN_OPEN_COURIER_HOME = "SCREEN_COURIER_HOME";
    public static String SCREEN_OPEN_HOME = "SCREEN_HOME";
    public static String SCREEN_OPEN_MYORDER = "SCREEN_MYORDER";
    public static String SCREEN_OPEN_ORDERDETAIL = "SCREEN_ORDERDETAIL";

    public static String ACCOUNTNAME = "account_bank";
    public static String ACCOUNTHOLDERNAME = "account_bank";
    public static String ACCOUNTNUMBER = "account_bank_number";
    public static String IFSCCODE = "account_bank_number_IFSC";


    private static AppPref sInstance;
    private static SharedPreferences sPref;
    private static SharedPreferences.Editor sEditor;

    private AppPref(Context context) {
        sPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sEditor = sPref.edit();
    }

    public AppPref() {
    }

    public static AppPref getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppPref(context);
        }
        return sInstance;
    }

    //set methods
    public void set(String key, String value) {
        sEditor.putString(key, value).apply();
    }

    public void set(String key, boolean value) {
        sEditor.putBoolean(key, value).apply();
    }

    public void set(String key, float value) {
        sEditor.putFloat(key, value).apply();
    }

    public void set(String key, int value) {
        sEditor.putInt(key, value).apply();
    }

    public void set(String key, long value) {
        sEditor.putLong(key, value).apply();
    }

    public void set(String key, Set<String> value) {
        sEditor.putStringSet(key, value).apply();
    }

    // get methods
    public int getInt(String key, int defaultVal) {
        return sPref.getInt(key, defaultVal);
    }

    public int getInt(String key) {
        return sPref.getInt(key, 0);
    }

    public String getString(String key, String defaultVal) {
        return sPref.getString(key, defaultVal);
    }

    public String getString(String key) {
        return sPref.getString(key, "");
    }


    public boolean getBoolean(String key, boolean defaultVal) {
        return sPref.getBoolean(key, defaultVal);
    }

    public boolean getBoolean(String key) {
        return sPref.getBoolean(key, false);
    }


    public float getFloat(String key, float defaultVal) {
        return sPref.getFloat(key, defaultVal);
    }

    public float getFloat(String key) {
        return sPref.getFloat(key, 0);
    }

    public long getLong(String key, long defaultVal) {
        return sPref.getLong(key, defaultVal);
    }

    public long getLong(String key) {
        return sPref.getLong(key, 0);
    }

    public Set<String> getStringSet(String key) {
        return sPref.getStringSet(key, null);
    }

    public void clearData() {
        String token = getString(FCM_TOKEN);
        sEditor.clear().apply();
        set(FCM_TOKEN, token);
    }

    public String getUserId() {
        return sPref.getString(USER_ID, "");
    }

    public boolean contains(String key) {
        return sPref.contains(key);
    }

    public void remove(String key) {
        sEditor.remove(key);
    }

    public Map<String, ?> getAll() {
        return sPref.getAll();
    }

    public boolean isLogin() {
        return sPref.getBoolean(IS_LOGIN, false);
    }
}