package com.couriertrack.ui.home.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;


import com.couriertrack.R;
import com.couriertrack.utils.AppLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class FindAddress extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "FindAddress";
    double latitude, longitude;
    String locality="";
    String addressFromCatch,pincode;
    Context context;
    FindAddressListener listener;

    public FindAddress(double latitude, double longitude, Context context, FindAddressListener listener) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getAddress(latitude, longitude);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

            AppLog.e(TAG, "Address :: " + null);
            listener.onLocationDetect(addressFromCatch,locality,pincode);

    }

    public interface FindAddressListener
    {
        public void onLocationDetect(String address, String city, String pincode);
    }

    public void getAddress(double lat, double lng) {
        Log.e("getADD", "lat " + lat);
        Log.e("getADD", "lng " + lng);

        try {

            JSONObject jsonObj = getJSONfromURL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + ","
                    + lng + "&sensor=true"+"&key="+context.getString(R.string.mapApi)); //"&key="+context.getString(R.string.map_key)
            AppLog.e("Response :: ",jsonObj.toString());
            String Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                JSONArray address_components = zero.getJSONArray("address_components");
                addressFromCatch = zero.getString("formatted_address");

                AppLog.e(TAG, addressFromCatch);

                for (int i = 0; i < address_components.length(); i++) {
                    try {
                        JSONObject zero2 = address_components.getJSONObject(i);
                        String long_name = zero2.getString("long_name");
                        JSONArray mtypes = zero2.getJSONArray("types");

                        String Type = mtypes.getString(0);

                        if (Type.equalsIgnoreCase("locality")) {
                            locality = long_name;
                        }
                        if (Type.equalsIgnoreCase("postal_code")) {
                            pincode = long_name;
                        }
                        if(!TextUtils.isEmpty(locality) && !TextUtils.isEmpty(pincode)){
                            break;
                        }
                    } catch (Exception e) {

                        AppLog.e(TAG, "Error: " + e.getLocalizedMessage());
                        e.printStackTrace();


                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e("getADD", "error is " + e.toString());
        }

    }

    public JSONObject getJSONfromURL(String urlString) {

        // initialize
        InputStream is = null;
        String result = "";
        JSONObject jObject = null;

        // http post
        URL url;
        HttpURLConnection urlConnection = null;
        //JSONArray response = new JSONArray();
        Log.e("URL :: ",urlString);
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = readStream(urlConnection.getInputStream());
                Log.v("CatalogClient", result);
                //response = new JSONArray(responseString);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // convert response to string
            /*try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
            } catch (Exception e) {
                printLog("log_tag", "Error converting result " + e.toString());
            }*/

        // try parse the string to a JSON object
        try {
            jObject = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jObject;
    }

    public static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
