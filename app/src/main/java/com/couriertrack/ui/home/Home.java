package com.couriertrack.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.databinding.ActivityHomeBinding;
import com.couriertrack.network.service.BackgroundLocationService;
import com.couriertrack.ui.Base;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.ui.courier.user_profile.UserProfile;
import com.couriertrack.ui.home.map.DirectionFinder;
import com.couriertrack.ui.home.map.DirectionFinderListener;
import com.couriertrack.ui.home.map.FindAddress;
import com.couriertrack.ui.home.map.Route;
import com.couriertrack.ui.home.support.AboutUs;
import com.couriertrack.ui.home.support.HelpSupportActivity;
import com.couriertrack.ui.home.support.Pricing;
import com.couriertrack.ui.myorder.MyOrder;
import com.couriertrack.ui.webview.Webview;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Home extends Base implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener, FindAddress.FindAddressListener, PaymentResultListener, View.OnClickListener {

    private static final String TAG = "Home";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 11;
    int REQUEST_CHECK_SETTINGS = 214;
    ActivityHomeBinding binding;
    private GoogleMap googleMap;
    double lt;
    double lg;
    String from, city, postalCode, setAddress = "";
    private static final int REQUEST_LOCATION = 1;
    HomeFragment homeFragment;
    public ReceiverFragment receiverFragment;
    Double senderx, sendery, receiverx, receivery;
    String originloc, destloc;
    private List<Polyline> polylinePaths = new ArrayList<>();
    public String distance, time;
    public int distanceValue, timeValue, cost;
    public static String fragmentName;
    TextView edtSearch;
    Bundle confirmRequestBundle;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private String latitude = "0.0", longitude = "0.0";
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        setToolbar();
        setTitle("HOME");
        init();
        initDrawer();

        initGoogleClient();
        initMapView(savedInstanceState);

        changeFrag(homeFragment, false, false);
    }

    public void setmarkerreceivervisibility(boolean isvisible) {
        if (isvisible) {
            googleMap.clear();
            binding.ivCentermarker.setVisibility(View.VISIBLE);
            binding.ivCentermarker.setImageDrawable(getResources().getDrawable(R.drawable.icon_map_receiver));
        } else {
            binding.ivCentermarker.setVisibility(View.GONE);
        }

    }

    public void setmarkersendervisibility(boolean isvisible) {
        if (isvisible) {
            binding.ivCentermarker.setVisibility(View.VISIBLE);
            binding.ivCentermarker.setImageDrawable(getResources().getDrawable(R.drawable.icon_map_sender));
        } else {
            binding.ivCentermarker.setVisibility(View.GONE);
        }

    }

    private void init() {
        homeFragment = HomeFragment.newInstance(null);
        receiverFragment = ReceiverFragment.newInstance(null);

        edtSearch = (TextView) findViewById(R.id.edtSearch);

        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAutoComplet();
            }
        });

        /**
         * Preload payment resources
         */
        Checkout.preload(getApplicationContext());
    }


    //Initialized Google Client
    private void initGoogleClient() {
        // Create an instance of GoogleAPIClient.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Home.this);
        mSettingsClient = LocationServices.getSettingsClient(Home.this);

        mGoogleApiClient = new GoogleApiClient.Builder(Home.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(Home.this);
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.e(TAG, "Location Received");
                onLocationChanged(locationResult.getLocations().get(0));
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.e(TAG, "Location Availability " + locationAvailability.isLocationAvailable());

                // showNotification(locationAvailability.isLocationAvailable());
            }
        };

    }

    private void initDrawer() {
        toolbar.setNavigationIcon(R.drawable.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        TextView tvpricing = view.findViewById(R.id.tv_pricing);
        tvpricing.setVisibility(View.VISIBLE);
        tvpricing.setOnClickListener(this);

        TextView tvaboutus = view.findViewById(R.id.tv_about_us);
        tvaboutus.setOnClickListener(this);

        TextView tv_myorder = view.findViewById(R.id.tv_my_orders);
        tv_myorder.setOnClickListener(this);

        TextView tv_help_support = view.findViewById(R.id.tv_help_support);
        tv_help_support.setOnClickListener(this);


        TextView tv_turnoffservice = view.findViewById(R.id.tv_turnoffservice);
        tv_turnoffservice.setVisibility(View.GONE);
        tv_turnoffservice.setOnClickListener(this);


        TextView tvmobileno = view.findViewById(R.id.tv_mobile_no);
        tvmobileno.setOnClickListener(this);

        binding.navView.addView(view);
    }


    @Override
    public void onClick(View v) {
        AppLog.e(TAG, "Id : " + v.getId());
        switch (v.getId()) {
            case R.id.llLogout:
                binding.drawer.closeDrawer(Gravity.START);
                //stopService(new Intent(this, BackgroundLocationService.class));
                logout();

                break;
            case R.id.tv_help_support:
                binding.drawer.closeDrawer(Gravity.START);
                Bundle b = new Bundle();
                b.putString("url",getResources().getString(R.string.help_support));
                b.putString("title","Help And Support");
                gotoActivity(Webview.class,b,false);
                //gotoActivity(HelpSupportActivity.class, null, false);
                break;
            case R.id.tv_my_profile:
                binding.drawer.closeDrawer(Gravity.START);
                gotoActivity(UserProfile.class, null, false);
                break;

            case R.id.tv_my_orders:
                binding.drawer.closeDrawer(Gravity.START);
                gotoActivity(MyOrder.class, null, false);
                break;
            case R.id.tv_pricing:
                binding.drawer.closeDrawer(Gravity.START);
                Bundle bundlep = new Bundle();
                bundlep.putString("url",getResources().getString(R.string.pricing));
                bundlep.putString("title","Pricing");
                gotoActivity(Webview.class,bundlep,false);
               // gotoActivity(Pricing.class, null, false);
                break;
            case R.id.tv_about_us:
                binding.drawer.closeDrawer(Gravity.START);
                Bundle bundle = new Bundle();
                bundle.putString("url",getResources().getString(R.string.about_us));
                bundle.putString("title","About Us");
                gotoActivity(Webview.class,bundle,false);
               // gotoActivity(AboutUs.class, null, false);
                break;
        }
    }


    private void initAutoComplet() {

        Places.initialize(getApplicationContext(), getString(R.string.mapApi));

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void onAddressSelect(LatLng latLng) {
        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                Place place = Autocomplete.getPlaceFromIntent(data);
                AppLog.e(TAG, "Place: " + place.getName() + ", " + place.getId());
                onAddressSelect(place.getLatLng());
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR)
            {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                AppLog.e(TAG, status.getStatusMessage());
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // The user canceled the operation.
            }
        }
        else if (requestCode == REQUEST_CHECK_SETTINGS)
        {
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                {
                    // All required changes were successfully made
                    Log.e(TAG, "ResolutionCheck OK");
                    break;
                }
                case Activity.RESULT_CANCELED:
                {
                    // The user was asked to change settings, but chose not to
                    Log.e(TAG, "ResolutionCheck CANCEL");
                    break;
                }
                default:
                {
                    break;
                }
            }
        }


    }

    private void initMapView(Bundle savedInstanceState) {

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                lt = googleMap.getCameraPosition().target.latitude;
                lg = googleMap.getCameraPosition().target.longitude;
                /**/
                getAddress();

            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);
        View locationButton = ((View) binding.mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);rlp.setMargins(0,0,30,30);

    }

    private void getAddress() {

        homeFragment.setadddress("", lt, lg);
        receiverFragment.setadddress("", lt, lg);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // We can now safely use the API we requested access to
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                googleMap.setMyLocationEnabled(true);
                setupLocation();
            } else {
                // Permission was denied or request was cancelled
                showToast("Permission Denied To Get Your Current Location");
            }
        }

    }

    public void setupLocation()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setFastestInterval(10 * 1000);
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
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.e(TAG, "GPS Success");
                        requestLocationUpdate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {

                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(Home.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie)
                        {
                            Log.e(TAG, "Unable to execute request.");
                            showToast("Couldn't Get Your Location");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.e(TAG, "checkLocationSettings -> onCanceled");
            }
        });

    }


    private void requestLocationUpdate()
    {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        AppLog.d(TAG, "Connected");
        setupLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

            AppLog.d(TAG, "Suspend");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onLocationChanged(Location location)
    {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        AppLog.d(TAG, "Lat :: " + location.getLatitude());
        AppLog.d(TAG, "Lng :: " + location.getLongitude());

        if (googleMap != null)
        {
            // googleMap.clear();
            //googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }



    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        /**
         * Add your logic here for a successful payment response
         */
        AppLog.e(TAG , "OnPaymentSuccess Called with id : "+razorpayPaymentID);
        if(confirmRequestBundle !=null )
        {
            confirmRequestBundle.putString("paymentid" , razorpayPaymentID);
            try {
                this.changeFrag(SuccessFragment.newInstance(confirmRequestBundle), true, false);
            }
            catch(Exception e)
            {
                AppLog.e(TAG , "error : "+e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        /**
         * Add your logic here for a failed payment response
         */
        AppLog.e(TAG , "OnPaymentError Called with id : "+response);
        showToast("Your Order Couldn't Be Placed Due To Payment Error");
    }


    public void startPayment(Bundle bundle)
    {
        this.confirmRequestBundle = bundle;
        /**   * Instantiate Checkout   */
        Checkout checkout = new Checkout();
        /**   * Set your logo here   */
        checkout.setImage(R.drawable.app_icon);
        /**   * Reference to current activity   */
        final Activity activity = this;
        /**   * Pass your payment options to the Razorpay Checkout as a JSONObject   */
        try
        {
            JSONObject options = new JSONObject();
            /**     * Merchant Name     * eg: ACME Corp || HasGeek etc.     */
            options.put("name", ""+getResources().getString(R.string.app_name));
            /**     * Description can be anything     * eg: Order #123123     *     Invoice Payment     *     etc.     */
            options.put("description", "Order ID CT"+ Calendar.getInstance().getTimeInMillis());
            options.put("currency", "INR");
            /**     * Amount is always passed in PAISE     * Eg: "500" = Rs 5.00     */
            options.put("amount", bundle.getInt("cost")*100);
            checkout.open(activity, options);
        }
        catch(Exception e)
        {		Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }

        // THAT WAS FOR BYPASSING PAYTMENT GATEWAY
      /*  if(confirmRequestBundle !=null )
        {
            confirmRequestBundle.putString("paymentid" , "TESTSTRINGID");
            try {
                this.changeFrag(SuccessFragment.newInstance(confirmRequestBundle), true, false);
            }
            catch(Exception e)
            {
                AppLog.e(TAG , "error : "+e.getLocalizedMessage());
            }
        }*/

    }

    public void setmarkersender()
    {
        googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_sender))
                .flat(true)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_64))
                .title("Senderaddress")
                .position(new LatLng(senderx,sendery)));
    }

    public void setmarkerreceiver(){

        googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_receiver))
                .flat(true)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_64))
                .title("Recieveraddress")
                .position(new LatLng(receiverx,receivery)));
    }

    @Override
    public void onDirectionFinderStart() {
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
            // googleMap.clear();
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        hideLoading();
        polylinePaths = new ArrayList<>();
        for (Route route : routes) {
            distance = route.distance.text;
            distanceValue = route.distance.value;

            timeValue = route.duration.value;
           // time = route.duration.text;

            String[] myTime = route.duration.text.split(" ");
            if(myTime.length==2){
                SimpleDateFormat df = new SimpleDateFormat("mm");
                Date d = null;
                try {
                    d = df.parse(myTime[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, 20);
                String newTime = df.format(cal.getTime());
                AppLog.e(TAG,"newTime: "+newTime);

                time = newTime +" mins";
            }else {
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                Date d = null;
                try {
                    d = df.parse(myTime[0]+":"+myTime[2]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, 20);
                String newTime = df.format(cal.getTime());

                newTime.split(":");

                AppLog.e(TAG," size: "+newTime.split(":").length);
                AppLog.e(TAG,"newTime1: "+newTime);
                time = cal.get(Calendar.HOUR)+" hour "+cal.get(Calendar.MINUTE)+" mins";
            }

            AppLog.e(TAG,"timeValue: "+timeValue+" : "+route.duration.text+" : ");

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(getResources().getColor(R.color.colorPrimary)).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(googleMap.addPolyline(polylineOptions));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(senderx , sendery)).include(new LatLng(receiverx , receivery));

            //Animate to the bounds
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), getResources().getDimensionPixelSize(R.dimen._40sdp));
            googleMap.moveCamera(cameraUpdate);

        }
    }


    public void drawpath()
    {
        originloc = senderx+","+sendery;
        destloc = receiverx+","+receivery;
        setmarkersender();
        setmarkerreceiver();
        try {
            new DirectionFinder(Home.this,this, originloc, destloc).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationDetect(String address, String city, String pincode) {

    }

    public void clearMap(){
        googleMap.clear();

    }

    @Override
    public void onBackPressed() {
        AppLog.e(TAG,"Fragment Name : "+Home.fragmentName);
        if(Home.fragmentName.equalsIgnoreCase("SuccessFragment"))
        {
            gotoActivity(Home.class,null,true);
        }
        else {
            super.onBackPressed();
        }

    }
}
