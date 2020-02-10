package com.couriertrack.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.couriertrack.ui.Base;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class GpsHelper {
    private static final String TAG = "";
    private static final long INTERVAL = 10000;// in millisecond
    private static final long INTERVAL_FAST = 5000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    GpsHelperListener listener;
    Context context;
    public interface GpsHelperListener
    {
        void onLocationChanged(Location location);
    }
    public GpsHelper(GpsHelperListener listener, Context context) {
        this.listener = listener;
        this.context=context;

        init();
    }

    private  void init()
    {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    AppLog.e(TAG,"my location :"+location.toString());
                    if(listener!=null)
                        listener.onLocationChanged(location);
                }
            }
        };
    }
    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    public void startLocationUpdates() {
        final LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(INTERVAL_FAST);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task= client.checkLocationSettings(builder.build());

        task.addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>()
        {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                AppLog.e(TAG, "onSuccess()");
            }
        });

        task.addOnFailureListener((Activity) context, new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppLog.e(TAG, "onFailure -> " + e.toString());
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a saveDialog.
                    try {
                       /*  Show the saveDialog by calling startResolutionForResul*//*t(),
                       *//*  and check the result in onActivityResult().*/
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult((Activity) context,
                                1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });
        startTacking(mLocationRequest);
    }


    public static void navigateUser(Activity activity, String lat , String lng)
    {
        Log.e(TAG , "GetDirection Called");
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(mapIntent);
        }
        else
        {
            Uri geoUri = Uri.parse("geo:"+lat+","+lng);
            Intent mapGeoIntent = new Intent(Intent.ACTION_VIEW, geoUri);
            mapGeoIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(mapIntent);
            }
            else
            ((Base) activity).showToast("Sorry , Navigation not available in your device");
        }
    }

    private void startTacking(LocationRequest mLocationRequest) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
