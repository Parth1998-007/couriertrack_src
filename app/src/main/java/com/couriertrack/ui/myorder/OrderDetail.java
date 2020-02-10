package com.couriertrack.ui.myorder;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.api_model.BaseRes;
import com.couriertrack.api_model.CancelOrderModel;
import com.couriertrack.api_model.CreateOrderModel;
import com.couriertrack.api_model.OrderDetailModel;
import com.couriertrack.databinding.ActivityOrderDetailBinding;
import com.couriertrack.network.ApiService;
import com.couriertrack.network.RetroClient;
import com.couriertrack.ui.Base;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.ui.home.Home;
import com.couriertrack.ui.home.map.DirectionFinder;
import com.couriertrack.ui.home.map.DirectionFinderListener;
import com.couriertrack.ui.home.map.Route;
import com.couriertrack.ui.webview.Webview;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class OrderDetail extends Base implements OnMapReadyCallback, DirectionFinderListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "OrderDetail";
    ActivityOrderDetailBinding binding;
    private GoogleMap googleMap;
    private static final int REQUEST_LOCATION = 1;
    private GoogleApiClient mGoogleApiClient;
    double lt;
    double lg;
    String originloc, destloc;
    private List<Polyline> polylinePaths = new ArrayList<>();
    public String distance, time;
    int orderid;
    OrderDetailFragment orderDetailFragment;
    PackageOrderDetailFragment packageOrderDetailFragment;
    CarrierFragment carrierFragment;
    ReciverDetailFragment reciverDetailFragment;
    SenderDetailFragment senderDetailFragment;
    OrderDetailModel.OrderDetailRes orderDetailRes;
    Handler handler;
    Runnable runnable;
    public int callBackAPI = 30000;
    public TextView tvaction;
    NotiCntReciever notiCntReciever;
    boolean zoomed ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_order_detail);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        setToolbar();
        setTitle("Order Detail");
        enableBack(true);

        init();

        initGoogleClient();
        initMapView(savedInstanceState);

        runnable = new Runnable()
        {
            public void run() {
//                            showToast("Location Update");
                getOrderDetail(orderid,"hide");
                handler.postDelayed(this, callBackAPI);
            }
        };
    }

    private class NotiCntReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppLog.e(TAG," msgCount onReceive()");
            getOrderDetail(orderid,"hide");
        }
    }

    private void init()
    {
        senderDetailFragment = SenderDetailFragment.newInstance(null);
        reciverDetailFragment = ReciverDetailFragment.newInstance(null);
        packageOrderDetailFragment = PackageOrderDetailFragment.newInstance(null);
        orderDetailFragment = OrderDetailFragment.newInstance(null);
        carrierFragment = CarrierFragment.newInstance(null);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            orderid = b.getInt("orderid", 0);
        }

        binding.lltrack.setOnClickListener(this);
        binding.llpackage.setOnClickListener(this);
        binding.llreciever.setOnClickListener(this);
        binding.llsender.setOnClickListener(this);
        binding.llcarrier.setOnClickListener(this);
    }

    //Initialized Google Client
    private void initGoogleClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }
    }

    private void initMapView(Bundle savedInstanceState)
    {
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        } else {

        }

        googleMap.setMyLocationEnabled(true);

        /*googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                lt = cameraPosition.target.latitude;
                lg = cameraPosition.target.longitude;



                AppLog.e(TAG , "Lat Is " + lt);
                AppLog.e(TAG  , "Lng Is " + lg);

            }
        });*/

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                /**/  // getAddress();
            }
        });
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
                onMapReady(googleMap);
                googleMap.setMyLocationEnabled(true);

            } else {
                // Permission was denied or request was cancelled
            }
        }

        // canGetLocation();
    }

//    public void canGetLocation() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            return;
//        } else {
//
//        }
//
//        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//        if (location != null) {
//
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//        } else {
//
//            startLocationUpdates();
//
//        }
//    }
//    private void startLocationUpdates() {
//        googleMap.clear();
//        LocationRequest mLocationRequest = LocationRequest.create();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//
//            return;
//        }
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
//    }


    public void drawpath(String senderx, String sendery, String receiverx, String receivery) {
        originloc = senderx + "," + sendery;
        destloc = receiverx + "," + receivery;

        try
        {
            new DirectionFinder(OrderDetail.this, this, originloc, destloc).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setmarkersender(double senderx, double sendery) {

        googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_sender))
                .flat(true)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_64))
                .title("Senderaddress")
                .position(new LatLng(senderx, sendery)));

       /* if (googleMap != null) {
            // googleMap.clear();
            //googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(senderx, sendery), 15));
        }*/
    }

    public void setmarkerreceiver(double receiverx, double receivery) {

        googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_receiver))
                .flat(true)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_64))
                .title("Recieveraddress")
                .position(new LatLng(receiverx, receivery)));

     /*   if (googleMap != null) {
            // googleMap.clear();
            //googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(receiverx, receivery), 15));
        }*/
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
            time = route.duration.text;

            AppLog.e(TAG, "distance: " + distance + " ,time: " + time);

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(getResources().getColor(R.color.colorPrimary)).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(googleMap.addPolyline(polylineOptions));

        }

        //orderDetailFragment.onOrderDetailRes(orderDetailRes);
        // packageOrderDetailFragment.onOrderDetailRes(orderDetailRes);
        // reciverDetailFragment.onOrderDetailRes(orderDetailRes);
        // senderDetailFragment.onOrderDetailRes(orderDetailRes);

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        AppLog.d(TAG, "Lat :: " + location.getLatitude());
        AppLog.d(TAG, "Lng :: " + location.getLongitude());


        /*if (googleMap != null) {
            // googleMap.clear();
            //googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        getOrderDetail(orderid,"show");
        handler = new Handler();
        handler.postDelayed(runnable, callBackAPI);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }

        notiCntReciever=new NotiCntReciever();
        registerReceiver(notiCntReciever,new IntentFilter(getPackageName()+"."+ AppPref.SCREEN_OPEN_ORDERDETAIL));

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        if(handler != null)
            handler.removeCallbacks(runnable);

        try {
            if(notiCntReciever!=null){
                unregisterReceiver(notiCntReciever);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        AppLog.d(TAG, "Connected");
        // canGetLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

        AppLog.d(TAG, "Suspend");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        clearFragment();
        switch (v.getId()) {
            case R.id.llcarrier: {
                binding.ivCarrier.setImageDrawable(getResources().getDrawable(R.drawable.icon_checkbox_filled));
                binding.ivTrack.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivPkg.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivReciever.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivSender.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                changeFrag(carrierFragment, false, false);
                break;
            }
            case R.id.lltrack: {
                binding.ivCarrier.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivTrack.setImageDrawable(getResources().getDrawable(R.drawable.icon_checkbox_filled));
                binding.ivPkg.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivReciever.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivSender.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                changeFrag(orderDetailFragment, false, false);
                break;
            }
            case R.id.llpackage: {
                binding.ivCarrier.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivTrack.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivPkg.setImageDrawable(getResources().getDrawable(R.drawable.icon_checkbox_filled));
                binding.ivReciever.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivSender.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                changeFrag(packageOrderDetailFragment, false, false);
                break;
            }
            case R.id.llreciever: {
                binding.ivCarrier.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivTrack.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivPkg.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivReciever.setImageDrawable(getResources().getDrawable(R.drawable.icon_checkbox_filled));
                binding.ivSender.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                changeFrag(reciverDetailFragment, false, false);
                break;
            }
            case R.id.llsender: {
                binding.ivCarrier.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivTrack.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivPkg.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivReciever.setImageDrawable(getResources().getDrawable(R.drawable.icon_uncheckbox));
                binding.ivSender.setImageDrawable(getResources().getDrawable(R.drawable.icon_checkbox_filled));
                changeFrag(senderDetailFragment, false, false);
                break;
            }
        }
    }

    private void getOrderDetail(int orderid, final String loding) {
        if(loding.equalsIgnoreCase("show"))
        showLoading();
        apiService.orderDetail(Integer.parseInt(appPref.getUserId()), orderid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<OrderDetailModel.OrderDetailRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if(loding.equalsIgnoreCase("show"))
                            subscribe(d);
                    }

                    @Override
                    public void onNext(Response<OrderDetailModel.OrderDetailRes> orderDetailRes) {
                        if (isSuccess(orderDetailRes, orderDetailRes.body())) {
                            onOrderDetailRes(orderDetailRes.body() , loding);
                        }
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

    public void onFailure(Throwable e) {
        AppLog.e(TAG, "onError() : " + e.getMessage());
        showToast(getString(R.string.msg_try_again));
        hideLoading();
    }

    public void onDone() {
        AppLog.e(TAG, "onComplete()");
        hideLoading();
    }


    public void onOrderDetailRes(OrderDetailModel.OrderDetailRes orderDetailRes , String loding)
    {
        AppLog.e(TAG, "orderdetailRes :" + orderDetailRes);

        if (orderDetailRes.isStatus()) {
            this.orderDetailRes = orderDetailRes;

            if(orderDetailRes.getOrder_status().equalsIgnoreCase("cancelled")){
                tvaction.setText("Cancelled");
            }else {
                tvaction.setText("Cancel Order");
            }
            binding.btHomenext.setText("Order Status : " + orderDetailRes.getOrder_status().toUpperCase());
            orderDetailFragment.onOrderDetailRes(orderDetailRes);
            packageOrderDetailFragment.onOrderDetailRes(orderDetailRes);
            reciverDetailFragment.onOrderDetailRes(orderDetailRes);
            senderDetailFragment.onOrderDetailRes(orderDetailRes);
            carrierFragment.onOrderDetailRes(orderDetailRes);
            AppLog.e(TAG, "distance: " + distance + " ,time: " + time);
            if (googleMap != null) {
                 googleMap.clear();
                //googleMap.addMarker(new MarkerOptions().position(latLng));
                if(orderDetailRes.getOrder_status().equalsIgnoreCase("pickedup"))
                {
                    setmarkerreceiver(Double.parseDouble(orderDetailRes.getReceiver_lat()), Double.parseDouble(orderDetailRes.getReceiver_lng()));
                    googleMap.addMarker(new MarkerOptions().
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_driver))
                            .flat(true)
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_64))
                            .title("Driver Location")
                            .position(new LatLng(Double.parseDouble(orderDetailRes.getCourier_boy_lat()), Double.parseDouble(orderDetailRes.getCourier_boy_lng()))));
                   // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(orderDetailRes.getCourier_boy_lat()), Double.parseDouble(orderDetailRes.getCourier_boy_lng())), 15));
                    if(!zoomed)
                    {   zoomed = true;

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(Double.parseDouble(orderDetailRes.getCourier_boy_lat()), Double.parseDouble(orderDetailRes.getCourier_boy_lng()))).include(new LatLng(Double.parseDouble(orderDetailRes.getReceiver_lat()), Double.parseDouble(orderDetailRes.getReceiver_lng())));

                        //Animate to the bounds
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), getResources().getDimensionPixelSize(R.dimen._40sdp));
                        googleMap.moveCamera(cameraUpdate);

                    }

                }
                else
                {
                    setmarkersender(Double.parseDouble(orderDetailRes.getSender_lat()), Double.parseDouble(orderDetailRes.getSender_lng()));
                    setmarkerreceiver(Double.parseDouble(orderDetailRes.getReceiver_lat()), Double.parseDouble(orderDetailRes.getReceiver_lng()));
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(orderDetailRes.getReceiver_lat()), Double.parseDouble(orderDetailRes.getReceiver_lng())), 15));
                    if(!zoomed)
                    {    zoomed = true;
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(Double.parseDouble(orderDetailRes.getSender_lat()), Double.parseDouble(orderDetailRes.getSender_lng()))).include(new LatLng(Double.parseDouble(orderDetailRes.getReceiver_lat()), Double.parseDouble(orderDetailRes.getReceiver_lng())));

                        //Animate to the bounds
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), getResources().getDimensionPixelSize(R.dimen._40sdp));
                        googleMap.moveCamera(cameraUpdate);
                    }
                }
            }

            if (orderDetailRes.getOrder_status().equalsIgnoreCase("new") || orderDetailRes.getOrder_status().equalsIgnoreCase("accepted") || orderDetailRes.getOrder_status().equalsIgnoreCase("delivered")|| orderDetailRes.getOrder_status().equalsIgnoreCase("pending")) // for new Order path draw betn sender and receiver
            {
                drawpath(orderDetailRes.getSender_lat(), orderDetailRes.getSender_lng(), orderDetailRes.getReceiver_lat(), orderDetailRes.getReceiver_lng());

            } else if (orderDetailRes.getOrder_status().equalsIgnoreCase("pickedup")) // for new Order path draw betn driver and receiver
            {
                drawpath(orderDetailRes.getCourier_boy_lat(),orderDetailRes.getCourier_boy_lng(), orderDetailRes.getReceiver_lat(),orderDetailRes.getReceiver_lng());
            }


            if(loding.equalsIgnoreCase("show"))
            changeFrag(carrierFragment, false, false);

        }
        else
        {
            if(loding.equalsIgnoreCase("show"))
            changeFrag(carrierFragment, false, false);
        }

    }

    @Override
    public void onBackPressed() {

        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuaction = menu.findItem(R.id.menu_action);
        View view = LayoutInflater.from(this).inflate(R.layout.menu_cancel, null);
        tvaction = view.findViewById(R.id.tvaction);
        menuaction.setActionView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvaction.getText().toString().equalsIgnoreCase("Cancel Order")) {
                    showCancelllationDialog();

                } else if (tvaction.getText().toString().equalsIgnoreCase("Cancelled")) {

                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void showCancelllationDialog() {
        final Dialog dialog_con = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog_con.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_con.setContentView(R.layout.dialog_cancel);

        dialog_con.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvterms = dialog_con.findViewById(R.id.tv_terms);
        final EditText etreason = dialog_con.findViewById(R.id.etreason);//etreason
        tvterms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("url",getResources().getString(R.string.terms_condition));
                bundle.putString("title","Terms and Condition");
                gotoActivity(Webview.class,bundle,false);
            }
        });


        Button btyes = dialog_con.findViewById(R.id.btYes);
        btyes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                callCancelOrderAPI(etreason.getText().toString());
                dialog_con.dismiss();
            }
        });
        Button btno = dialog_con.findViewById(R.id.btno);
        btno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_con.dismiss();
            }
        });

        dialog_con.show();
    }

    private void callCancelOrderAPI(String reason) {
        CancelOrderModel.CancelOrderReq cancelOrderReq = new CancelOrderModel.CancelOrderReq();
        cancelOrderReq.setOrder_id(orderid);
        cancelOrderReq.setUser_id(Integer.parseInt(appPref.getUserId()));
        cancelOrderReq.setReason(""+reason);
        AppLog.e(TAG, "cncelOrderReq : " + cancelOrderReq);
        showLoading();
        apiService.cancelOrder(cancelOrderReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CancelOrderModel.CancelOrderRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<CancelOrderModel.CancelOrderRes> cancelOrderRes) {
                        AppLog.e(TAG, "cancelOrderRes :" + cancelOrderRes);
                        if (isSuccess(cancelOrderRes, cancelOrderRes.body())) {
                            oncancelOrderRes(cancelOrderRes.body());
                        }
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

    private void oncancelOrderRes(CancelOrderModel.CancelOrderRes cancelOrderRes) {
        if(cancelOrderRes.isStatus()){
            showToast(cancelOrderRes.getMsg());
            tvaction.setText("Cancelled");
            getOrderDetail(orderid,"hide");
        }
    }
}
