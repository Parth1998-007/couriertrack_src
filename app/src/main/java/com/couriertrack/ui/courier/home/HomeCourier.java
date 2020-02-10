package com.couriertrack.ui.courier.home;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couriertrack.R;
import com.couriertrack.api_model.CheckVerifyModel;
import com.couriertrack.databinding.ActivityHomeCourierBinding;
import com.couriertrack.network.service.BackgroundLocationService;
import com.couriertrack.ui.Base;
import com.couriertrack.ui.courier.user_profile.UserProfile;
import com.couriertrack.ui.courier.wallet.WalletActivity;
import com.couriertrack.ui.home.support.AboutUs;
import com.couriertrack.ui.home.support.HelpSupportActivity;
import com.couriertrack.ui.home.support.Pricing;
import com.couriertrack.ui.webview.Webview;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HomeCourier extends Base implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    int[] tabIcon = new int[2];
    String[] tabLable = new String[2];
    ActivityHomeCourierBinding binding;
    String TAG = "HomeCourier";
    private static final int REQUEST_LOCATION = 1;
    /* For Google Fused API */
    protected GoogleApiClient mGoogleApiClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    Dialog dialog_con;
    NotiCntReciever notiCntReciever;
    boolean serviceStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_courier);
        init();
        setToolbar();
        setTabLayout();

        AppLog.e(TAG, "key : " + appPref.getString(AppPref.API_KEY));
    }

    private class NotiCntReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AppLog.e(TAG," msgCount onReceive()");
            invalidUser();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    void init()
    {
        tabIcon[0] = R.drawable.rectangle_tab;
        tabIcon[1] = R.drawable.rectangle_tab;
        tabLable[0] = "New Orders";
        tabLable[1] = "Accepted Orders";

        serviceStarted = false;
        //startService(new Intent(this, BackgroundLocationService.class));
    }

    void invalidUser()
    {
        Log.e(TAG, " USER STATUS : " + appPref.getString(AppPref.USERSTATUS, ""));
        if (appPref.getString(AppPref.USERSTATUS, "").equals("not_verified"))
        {
            checkVerifyUser();
        }
        else if (appPref.getString(AppPref.USERSTATUS, "").equals("rejected"))
        {
            checkVerifyUser();
        }
        else
        {
            if(!serviceStarted)
            {
                AppLog.e(TAG , "googleApi is null on start");
                buildGoogleApiClient();
            }
        }
    }

    private void showConfirmationDialog(final String msg, String btn_cancel, String btn_okay)
    {

        dialog_con = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog_con.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_con.setContentView(R.layout.dialog_confirmation);
        dialog_con.setCancelable(false);
        dialog_con.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView con_msg = dialog_con.findViewById(R.id.msg_con);
        con_msg.setText(msg);

        final TextView useremail = dialog_con.findViewById(R.id.lbl_title_value);
        useremail.setText(appPref.getString(AppPref.EMAIL));

        final TextView title = dialog_con.findViewById(R.id.lbl_title);
        title.setText("Email");

        Button pauseOrderOkay = dialog_con.findViewById(R.id.btPauseOrderOkay);
        pauseOrderOkay.setText(btn_okay);
        pauseOrderOkay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog_con.dismiss();
                logout();
            }
        });
        Button pauseOrderCancel = dialog_con.findViewById(R.id.btPauseOrderCancel);
        pauseOrderCancel.setText(btn_cancel);
        pauseOrderCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gotoActivity(UserProfile.class,null,false);
            }
        });

        if(dialog_con.isShowing())
            dialog_con.cancel();
        dialog_con.show();
    }


    private void showLocationDialog(final String msg, String btn_cancel, String btn_okay)
    {

        final Dialog dialog_con = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog_con.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_con.setContentView(R.layout.dialog_confirmation);
        dialog_con.setCancelable(false);
        dialog_con.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView con_msg = dialog_con.findViewById(R.id.msg_con);
        con_msg.setText(msg);
        Button pauseOrderOkay = dialog_con.findViewById(R.id.btPauseOrderOkay);
        pauseOrderOkay.setText(btn_okay);
        pauseOrderOkay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog_con.dismiss();
                buildGoogleApiClient();
            }
        });
        Button pauseOrderCancel = dialog_con.findViewById(R.id.btPauseOrderCancel);
        pauseOrderCancel.setText(btn_cancel);
        pauseOrderCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog_con.dismiss();
                stopService(new Intent(HomeCourier.this, BackgroundLocationService.class));
                HomeCourier.this.finish();
            }
        });

        dialog_con.show();
    }


    void createTabs()
    {
        for (int i = 0; i < tabIcon.length; i++) {
            View view = getLayoutInflater().inflate(R.layout.tab_layout_courier, null);
            ImageView tab_imageview = view.findViewById(R.id.tabImg);
            TextView tab_lable = view.findViewById(R.id.tabLabel);
            tab_imageview.setImageResource(tabIcon[i]);
            tab_imageview.setVisibility(View.GONE);
            tab_lable.setText(tabLable[i]);
            binding.tabLayout.getTabAt(i).setCustomView(view);
        }
    }

    void setTabLayout()
    {

        binding.tabLayout.addTab(binding.tabLayout.newTab());
        binding.tabLayout.addTab(binding.tabLayout.newTab());
        createTabs();
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Bundle bundle = getIntent().getExtras();
        int posiotion;
        if (bundle != null) {
            posiotion = bundle.getInt("tab_faragment");
            binding.tabLayout.getTabAt(posiotion).select();
        } else
            posiotion = 0;
        changeTab(posiotion);
    }

    void changeTab(int position)
    {
        clearFragment();
        switch (position)
        {
            case 0:
                changeFrag(NewPickupFragment.newInstance(null), true, true, binding.fragment.getId());
                break;
            case 1:
                AppLog.e(TAG, "changeTab");
                changeFrag(MyOrderCourierFragment.newInstance(null), false, false, binding.fragment.getId());
                break;
        }
    }


    void initDrawer()
    {

        TextView toolbar_title = toolbar.findViewById(R.id.tvTitle);
        toolbar_title.setText("Dashboard");
        toolbar.setNavigationIcon(R.drawable.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                binding.drawer.openDrawer(Gravity.LEFT);
            }
        });
        View view = getLayoutInflater().inflate(R.layout.drawer_custome_layout, null);

        TextView tv_mobile_no = view.findViewById(R.id.tv_mobile_no);
        tv_mobile_no.setText(appPref.getString(AppPref.MOBILE));
        TextView tv_my_profile = view.findViewById(R.id.tv_my_profile);
        tv_my_profile.setOnClickListener(this);

        TextView tv_user_name = view.findViewById(R.id.tv_user_name);
        tv_user_name.setText(appPref.getString(AppPref.NAME));
        LinearLayout ll_logout = view.findViewById(R.id.llLogout);
        ll_logout.setOnClickListener(this);

        TextView tv_help_support = view.findViewById(R.id.tv_help_support);
        tv_help_support.setOnClickListener(this);

        TextView myEarnings = view.findViewById(R.id.tv_my_orders);
        myEarnings.setText(getResources().getString(R.string.drawer_myearning));
        myEarnings.setOnClickListener(this);

        TextView tvpricing = view.findViewById(R.id.tv_pricing);
        tvpricing.setVisibility(View.GONE);
        tvpricing.setOnClickListener(this);

        TextView tvaboutus = view.findViewById(R.id.tv_about_us);
        tvaboutus.setOnClickListener(this);

        TextView tv_turnoffservice = view.findViewById(R.id.tv_turnoffservice);

        tv_turnoffservice.setOnClickListener(this);


        TextView tvmobileno = view.findViewById(R.id.tv_mobile_no);
        tvmobileno.setOnClickListener(this);

        binding.navView.addView(view);
    }

    @Override
    public void onClick(View v)
    {
        AppLog.e(TAG, "Id : " + v.getId());
        switch (v.getId())
        {
            case R.id.llLogout:
                binding.drawer.closeDrawer(Gravity.START);
                stopService(new Intent(this, BackgroundLocationService.class));
                logout();

                break;
            case R.id.tv_help_support:
                binding.drawer.closeDrawer(Gravity.START);
                Bundle b = new Bundle();
                b.putString("url",getResources().getString(R.string.help_support));
                b.putString("title","Help And Support");
                gotoActivity(Webview.class,b,false);
              //  gotoActivity(HelpSupportActivity.class,null,false);

                break;
            case R.id.tv_my_profile:
                binding.drawer.closeDrawer(Gravity.START);
                gotoActivity(UserProfile.class,null,false);
                break;

            case R.id.tv_my_orders :
                //MY EARNINGS...
                binding.drawer.closeDrawer(Gravity.START);
                gotoActivity(WalletActivity.class,null,false);

            case R.id.tv_turnoffservice:

                stopService(new Intent(HomeCourier.this, BackgroundLocationService.class));
                binding.drawer.closeDrawer(Gravity.START);

                break;
            case R.id.tv_pricing:
                binding.drawer.closeDrawer(Gravity.START);
                gotoActivity(Pricing.class, null, false);
                break;
            case R.id.tv_about_us:
                binding.drawer.closeDrawer(Gravity.START);
                Bundle bundle = new Bundle();
                bundle.putString("url",getResources().getString(R.string.about_us));
                bundle.putString("title","About Us");
                gotoActivity(Webview.class,bundle,false);
                //gotoActivity(AboutUs.class, null, false);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }


            } else {
                // Permission was denied or request was cancelled
            }
        }

        // canGetLocation();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }
        invalidUser();
        initDrawer();

        notiCntReciever=new NotiCntReciever();
        registerReceiver(notiCntReciever,new IntentFilter(getPackageName()+"."+AppPref.SCREEN_OPEN_COURIER_HOME));
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if(notiCntReciever!=null){
                unregisterReceiver(notiCntReciever);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    private void checkVerifyUser()
    {
        int user_type;

        if (appPref.getString(AppPref.USER_TYPE).equals("courier"))
            user_type = 2;
        else
            user_type = 1;

        verifyUser(user_type);
    }

    private void verifiedUser(int user_type, final Dialog dialog) {
        showLoading();
        apiService.checkVerifyUser(user_type, Integer.valueOf(appPref.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CheckVerifyModel.checkVerifiedUserRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<CheckVerifyModel.checkVerifiedUserRes> pickUpOrderListResResponse) {
                        AppLog.e(TAG, "Responce : " + pickUpOrderListResResponse);
                        AppLog.e(TAG, "Responce : " + pickUpOrderListResResponse.body().getStatus());

                        appPref.set(AppPref.USERSTATUS, pickUpOrderListResResponse.body().getStatus());

                        if (appPref.getString(AppPref.USERSTATUS, "").equals("verified")) {
                            dialog.dismiss();
                        }
                       // showToast(pickUpOrderListResResponse.body().getStatus());
                    }

                    @Override
                    public void onError(Throwable e) {
                        onFailure(e);
                    }

                    @Override
                    public void onComplete() {
                        onDone();
                    }
                });
    }


    private void verifyUser(int user_type)
    {
        showLoading();
        apiService.checkVerifyUser(user_type, Integer.valueOf(appPref.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CheckVerifyModel.checkVerifiedUserRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<CheckVerifyModel.checkVerifiedUserRes> pickUpOrderListResResponse) {
                        AppLog.e(TAG, "Responce : " + pickUpOrderListResResponse);
                        AppLog.e(TAG, "Responce : " + pickUpOrderListResResponse.body().getStatus());

                        appPref.set(AppPref.USERSTATUS, pickUpOrderListResResponse.body().getStatus());

                        checkStatusAndShowDialog();

                    }

                    @Override
                    public void onError(Throwable e) {
                        onFailure(e);
                    }

                    @Override
                    public void onComplete() {
                        onDone();
                    }
                });
    }

    private void checkStatusAndShowDialog()
    {
        if (appPref.getString(AppPref.USERSTATUS, "").equals("not_verified"))
        {
            showConfirmationDialog("Your Account Has Not Been Verified Yet !", "My Profile", "LOGOUT");

        }
        else if (appPref.getString(AppPref.USERSTATUS, "").equals("rejected"))
        {
            showConfirmationDialog("Your Account Has Been Rejected !", "My Profile", "LOGOUT");
        }
        else
        {
            dialog_con.dismiss();
            buildGoogleApiClient();
        }
    }


    public void onFailure(Throwable e) {
        AppLog.e(TAG, "onError() : " + e.getMessage());
        showToast(getString(R.string.msg_try_again));
        hideLoading();
    }

    public void onDone() {
        AppLog.e(TAG, "onComplete()");
        hideLoading();
    }








    //////////// GOOGLE API CLIENT FOR LOCTION

    protected synchronized void buildGoogleApiClient()
    {
        mSettingsClient = LocationServices.getSettingsClient(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        connectGoogleClient();

    }

    private void connectGoogleClient()
    {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS)
        {
            mGoogleApiClient.connect();
        }
        else
        {
            showLocationDialog("Please Update Play Services To Continue " , "Exit" ,"Retry ");
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>()
                {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse)
                    {
                        Log.e(TAG, "GPS Success");
                        startService(new Intent(HomeCourier.this, BackgroundLocationService.class));
                        serviceStarted = true;
                    }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try
                        {
                            int REQUEST_CHECK_SETTINGS = 214;
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult((AppCompatActivity) HomeCourier.this, REQUEST_CHECK_SETTINGS);
                        }
                        catch (IntentSender.SendIntentException sie)
                        {
                            Log.e(TAG, "Unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                }
            }
        }).addOnCanceledListener(new OnCanceledListener()
        {
            @Override
            public void onCanceled() {
                Log.e(TAG, "checkLocationSettings -> onCanceled");
                showLocationDialog("You must enable location services to continue" , "Cancel" , "Allow Location");
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case 214:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        System.out.println("test user has turned the gps back on");
                        buildGoogleApiClient();
                        break;
                    case Activity.RESULT_CANCELED:

                        Log.e(TAG ,"test user has denied the gps to be turned on");
                        showLocationDialog("You must enable location services to continue" , "Cancel" , "Allow Location");
                        break;
                }
                break;
        }
    }
}
