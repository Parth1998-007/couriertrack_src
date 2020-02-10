package com.couriertrack.ui.courier.myorder;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.api_model.AcceptedCourierModel;
import com.couriertrack.api_model.CompleteDeliveredModel;
import com.couriertrack.api_model.NewPickupOrderDetailModel;
import com.couriertrack.api_model.PickupCourierModel;
import com.couriertrack.api_model.ResendOTPModel;
import com.couriertrack.databinding.ActivityMyorderCourierDetailBinding;
import com.couriertrack.ui.Base;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.ui.home.map.DirectionFinder;
import com.couriertrack.ui.home.map.DirectionFinderListener;
import com.couriertrack.ui.home.map.Route;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.GpsHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class MyOrderCourierDetail extends Base implements View.OnClickListener, OnMapReadyCallback, DirectionFinderListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ActivityMyorderCourierDetailBinding binding;
    SenderCourierDetailFragment senderCourierDetailFragment;
    ReciverCourierDetailFragment reciverCourierDetailFragment;
    PackageCourierDetailFragment packageCourierDetailFragment;
    String TAG = "MyOrderCourierDetail";
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 1;
    private GoogleMap googleMap;
    String originloc, destloc;
    private List<Polyline> polylinePaths = new ArrayList<>();
    public String distance, time;
    private String order_id;
    NewPickupOrderDetailModel.NewPickupOrderDetailRes newOrderPickupOrderDetailRes;
    private AcceptedCourierModel.OrderAcceptReq acceptPickupReq;
    private CompleteDeliveredModel.completeDeliveredReq completePickupReq;
    private PickupCourierModel.PickupOrderReq pickupOrderReq;
    private String pickup_status;
    private ResendOTPModel.SendOTPReq resendOTPModel;
    public String customer_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MyOrderCourierDetail.this, R.layout.activity_myorder_courier_detail);
        init();
        setToolbar();
        setTitle("Courier Detail");
        enableBack(true);
        initGoogleClient();
        initMapView(savedInstanceState);
    }

    void init() {
        Intent order_id_intent = getIntent();
        order_id = order_id_intent.getStringExtra("pickup_order_id");
        //pickup_status = order_id_intent.getStringExtra("pickup_status");
        AppLog.e(TAG, order_id);
        AppLog.e(TAG, "Pickup Status : " + pickup_status);
        senderCourierDetailFragment = SenderCourierDetailFragment.newInstance(null);
        reciverCourierDetailFragment = ReciverCourierDetailFragment.newInstance(null);
        packageCourierDetailFragment = PackageCourierDetailFragment.newInstance(null);
        binding.btHomenext.setOnClickListener(this);
        binding.llSender.setOnClickListener(this);
        binding.ivSender.setImageResource(R.drawable.icon_checkbox_filled);
        binding.llReceiver.setOnClickListener(this);
        binding.llPackage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sender:
                {
                binding.ivSender.setImageResource(R.drawable.icon_checkbox_filled);
                binding.ivReceiver.setImageResource(R.drawable.icon_uncheckbox);
                binding.ivPackage.setImageResource(R.drawable.icon_uncheckbox);
                changeFrag(senderCourierDetailFragment, false, false);
                break;
                }
            case R.id.ll_receiver:
                {
                binding.ivSender.setImageResource(R.drawable.icon_uncheckbox);
                binding.ivReceiver.setImageResource(R.drawable.icon_checkbox_filled);
                binding.ivPackage.setImageResource(R.drawable.icon_uncheckbox);
                changeFrag(reciverCourierDetailFragment, false, false);
                break;
                }
            case R.id.ll_package:
                {
                binding.ivSender.setImageResource(R.drawable.icon_uncheckbox);
                binding.ivReceiver.setImageResource(R.drawable.icon_uncheckbox);
                binding.ivPackage.setImageResource(R.drawable.icon_checkbox_filled);
                changeFrag(packageCourierDetailFragment, false, false);
                break;
                }
            case R.id.bt_homenext:
                {
                    if(!TextUtils.isEmpty(pickup_status)){
                        if (pickup_status.equalsIgnoreCase("new"))
                            showConfirmationDialog("Are you sure to Accept Order?", "", "Cancel", "Accept", Integer.parseInt(order_id), "not_otp");
                        else if (pickup_status.equalsIgnoreCase("pickedup"))
                            showConfirmationDialog(getResources().getString(R.string.lbl_verify_receiver_detail), getResources().getString(R.string.hint_receiver_otp), "Cancel", "Complete", Integer.parseInt(order_id), "otp");
                        else if (pickup_status.equalsIgnoreCase("delivered"))
                            showToast("Courier Already Delivered");
                        else if(pickup_status.equalsIgnoreCase("CANCELLED")){

                        } else
                            showConfirmationDialog(getResources().getString(R.string.lbl_verify_sender_detail), getResources().getString(R.string.hint_sender_otp), "Cancel", "Pickup", Integer.parseInt(order_id), "otp");
                    }

                break;
                }

            case R.id.tv_sendotp:
                {
                resendOTP(Integer.valueOf(appPref.getUserId()), Integer.valueOf(order_id), customer_type);
                break;
                }
        }
    }

    private void showConfirmationDialog(final String msg, String hint, String btn_cancel, String btn_okay, final int order_id, String otp) {

        final Dialog dialog_con = new Dialog(MyOrderCourierDetail.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog_con.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_con.setContentView(R.layout.dialog_confirmation);

        dialog_con.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final LinearLayout otp_view = dialog_con.findViewById(R.id.ll_otpview);
        if (pickup_status.equalsIgnoreCase("pickedup")) {
            customer_type = "receiver";
        } else {
            customer_type = "sender";
        }
        if (otp.equalsIgnoreCase("otp"))
        {
            otp_view.setVisibility(View.VISIBLE);
        }
        final TextView con_msg = dialog_con.findViewById(R.id.msg_con);
        con_msg.setText(msg);

        con_msg.setVisibility(View.GONE);

        TextView orderID = dialog_con.findViewById(R.id.lbl_title_value);
        orderID.setText(""+order_id);

        final TextView tv_sendotp = dialog_con.findViewById(R.id.tv_sendotp);
        tv_sendotp.setOnClickListener(this);

        final EditText etotp = dialog_con.findViewById(R.id.etotp);
        etotp.setHint(hint);
        Button pauseOrderOkay = dialog_con.findViewById(R.id.btPauseOrderOkay);
        pauseOrderOkay.setText(btn_okay);
        pauseOrderOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pickup_status.equalsIgnoreCase("new"))
                {
                    dialog_con.dismiss();
                    setAcceptPickupData(Integer.parseInt(appPref.getUserId()), order_id );

                }
                else if (pickup_status.equalsIgnoreCase("accepted"))
                {
                    if (!etotp.getText().toString().isEmpty())
                        setPickupOrderData(Integer.parseInt(appPref.getUserId()), order_id, etotp.getText().toString(), dialog_con);
                    else
                        showToast("Enter Sender OTP");

                }
                else if(pickup_status.equalsIgnoreCase("pickedup"))
                {
                    if (!etotp.getText().toString().isEmpty())
                    {
                        setCompleteDeliveredData(Integer.parseInt(appPref.getUserId()), order_id, etotp.getText().toString(), dialog_con);
                    }
                    else
                    showToast("Enter Receiver OTP");
                }


            }
        });
        Button pauseOrderCancel = dialog_con.findViewById(R.id.btPauseOrderCancel);
        pauseOrderCancel.setText(btn_cancel);
        pauseOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_con.dismiss();
            }
        });

        dialog_con.show();
    }

    private void resendOTP(int user_id, int order_id, String customer_type) {
        AppLog.e(TAG, "User Id : " + user_id + " Order Id : " + order_id+" Customer Type : "+customer_type);
        resendOTPModel = new ResendOTPModel.SendOTPReq();
        resendOTPModel.setUser_id(user_id);
        resendOTPModel.setOrder_id(order_id);
        resendOTPModel.setFor_user(customer_type);
        callResendOTP(resendOTPModel);
    }

    private void callResendOTP(ResendOTPModel.SendOTPReq sendOTPReq) {
        showLoading();
        apiService.resendOTP(sendOTPReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResendOTPModel.SendOTPRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<ResendOTPModel.SendOTPRes> sendOTPResResponse) {
                        AppLog.e(TAG, "Response : "+sendOTPResResponse );
                        AppLog.e(TAG, sendOTPResResponse.body() + "");
                        if (sendOTPResResponse.body().isStatus()) {
                            showToast(sendOTPResResponse.body().getMsg());
                        } else {
                            showToast(sendOTPResResponse.body().getMsg());
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


    private void setAcceptPickupData(int user_id, int order_id ) {
        AppLog.e(TAG, "User Id : " + user_id + " Order Id : " + order_id);
        acceptPickupReq = new AcceptedCourierModel.OrderAcceptReq();
        acceptPickupReq.setUser_id(user_id);
        acceptPickupReq.setOrder_id(order_id);
        callAcceptPickup(acceptPickupReq);
    }

    private void callAcceptPickup(AcceptedCourierModel.OrderAcceptReq acceptPickupReq ) {
        showLoading();
        apiService.acceptOrder(acceptPickupReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<AcceptedCourierModel.OrderAcceptRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<AcceptedCourierModel.OrderAcceptRes> acceptPickup) {
                        AppLog.e(TAG, acceptPickup + "");
                        AppLog.e(TAG, acceptPickup.body() + "");

                        hideLoading();
                        if (acceptPickup.body().isStatus()) {
                            //showToast(acceptPickup.body().getMsg());
                            binding.btHomenext.setText("Accepted");
                            GpsHelper.navigateUser(MyOrderCourierDetail.this , newOrderPickupOrderDetailRes.getSender_lat() , newOrderPickupOrderDetailRes.getSender_lng());
                            getNewPickupOrderDetail(Integer.parseInt(order_id));
                        } else {
                            showToast(acceptPickup.body().getMsg());
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


    private void setPickupOrderData(int user_id, int order_id, String otp,Dialog dialog)
    {
        AppLog.e(TAG, "User Id : " + user_id + " Order Id : " + order_id);
        pickupOrderReq = new PickupCourierModel.PickupOrderReq();
        pickupOrderReq.setOtp(otp);
        pickupOrderReq.setUser_id(user_id);
        pickupOrderReq.setOrder_id(order_id);
        callOrderPickup(pickupOrderReq,dialog);
    }

    private void callOrderPickup(PickupCourierModel.PickupOrderReq pickupOrderReq, final Dialog dialog) {
        showLoading();
        apiService.pickupOrder(pickupOrderReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<PickupCourierModel.PickupOrderRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<PickupCourierModel.PickupOrderRes> acceptPickup) {
                        AppLog.e(TAG, acceptPickup + "");
                        AppLog.e(TAG, acceptPickup.body() + "");
                        dialog.dismiss();
                        if (acceptPickup.body().isStatus())
                        {
                            showToast(acceptPickup.body().getMsg());

                            GpsHelper.navigateUser(MyOrderCourierDetail.this , newOrderPickupOrderDetailRes.getReceiver_lat() , newOrderPickupOrderDetailRes.getReceiver_lng());
                            getNewPickupOrderDetail(Integer.parseInt(order_id));
                        }
                        else
                        {
                            showToast(acceptPickup.body().getMsg());
                            //pickup_status = "accepted";
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

    private void setCompleteDeliveredData(int user_id, int order_id, String otp, Dialog dialog) {
        AppLog.e(TAG, "User Id : " + user_id + " Order Id : " + order_id);
        completePickupReq = new CompleteDeliveredModel.completeDeliveredReq();
        completePickupReq.setUser_id(user_id);
        completePickupReq.setOrder_id(order_id);
        completePickupReq.setOtp(otp);
        callCompleteDelivered(completePickupReq, dialog);
    }

    private void callCompleteDelivered(CompleteDeliveredModel.completeDeliveredReq completePickupReq, final Dialog dialog) {
        showLoading();
        apiService.completeOrder(completePickupReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CompleteDeliveredModel.completeDeliveredRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<CompleteDeliveredModel.completeDeliveredRes> acceptPickup) {
                        AppLog.e(TAG, acceptPickup + "");
                        AppLog.e(TAG, acceptPickup.body() + "");
                        dialog.dismiss();
                        if (acceptPickup.body().isStatus()) {
                            showToast(acceptPickup.body().getMsg());
                           /* binding.btHomenext.setText("Courier Delivered");
                            pickup_status = "delivered";*/

//                            newPickupAdapter.remove(position);
                            getNewPickupOrderDetail(Integer.parseInt(order_id));
                        } else {
                            showToast(acceptPickup.body().getMsg());
                            //pickup_status = "pickedup";
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

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        AppLog.d(TAG, "Lat :: " + location.getLatitude());
        AppLog.d(TAG, "Lng :: " + location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                /**/  // getAddress();
            }
        });
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

    private void initMapView(Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getNewPickupOrderDetail(Integer.parseInt(order_id));
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


    public void drawpath(String senderx, String sendery, String receiverx, String receivery) {
        originloc = senderx + "," + sendery;
        destloc = receiverx + "," + receivery;
        setmarkersender(Double.parseDouble(senderx), Double.parseDouble(sendery));
        setmarkerreceiver(Double.parseDouble(receiverx), Double.parseDouble(receivery));
        try {
            new DirectionFinder(MyOrderCourierDetail.this, this, originloc, destloc).execute();
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

        if (googleMap != null) {
            // googleMap.clear();
            //googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(senderx, sendery), 15));
        }
    }

    public void setmarkerreceiver(double receiverx, double receivery) {

        googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_receiver))
                .flat(true)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_64))
                .title("Recieveraddress")
                .position(new LatLng(receiverx, receivery)));

        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(receiverx, receivery), 15));
        }
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
    public void onDirectionFinderSuccess(List<Route> routes)
    {
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

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(route.startLocation).include(route.endLocation);

            //Animate to the bounds
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), getResources().getDimensionPixelSize(R.dimen._40sdp));
            googleMap.moveCamera(cameraUpdate);

        }

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


    private void getNewPickupOrderDetail(int orderid) {
        showLoading();
        apiService.pickupOrderDetail(Integer.parseInt(appPref.getUserId()), orderid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<NewPickupOrderDetailModel.NewPickupOrderDetailRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<NewPickupOrderDetailModel.NewPickupOrderDetailRes> orderDetailRes) {
                        if (isSuccess(orderDetailRes, orderDetailRes.body())) {
                            onNewPickupOrderDetailRes(orderDetailRes.body());
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

    public void onNewPickupOrderDetailRes(NewPickupOrderDetailModel.NewPickupOrderDetailRes newOrderPickupOrderDetailRes) {
        AppLog.e(TAG, "orderdetailRes :" + newOrderPickupOrderDetailRes);
        AppLog.e(TAG, "orderdetailRes :" + newOrderPickupOrderDetailRes.getOrder_status());

        if (newOrderPickupOrderDetailRes.isStatus()) {
            this.newOrderPickupOrderDetailRes = newOrderPickupOrderDetailRes;

            pickup_status = newOrderPickupOrderDetailRes.getOrder_status();

            if (pickup_status.equalsIgnoreCase("new") || newOrderPickupOrderDetailRes.getOrder_status().equalsIgnoreCase("accepted") || newOrderPickupOrderDetailRes.getOrder_status().equalsIgnoreCase("pickedup") || newOrderPickupOrderDetailRes.getOrder_status().equalsIgnoreCase("delivered")) // for new Order path draw betn sender and receiver
            {
                drawpath(newOrderPickupOrderDetailRes.getSender_lat(), newOrderPickupOrderDetailRes.getSender_lng(), newOrderPickupOrderDetailRes.getReceiver_lat(), newOrderPickupOrderDetailRes.getReceiver_lng());

            } else if (newOrderPickupOrderDetailRes.getOrder_status().equalsIgnoreCase("In Transit")) // for new Order path draw betn driver and receiver
            {
                // drawpath(newOrderPickupOrderDetailRes.getDriver_lat(),newOrderPickupOrderDetailRes.getDriver_lng(), newOrderPickupOrderDetailRes.getReceiver_lat(),newOrderPickupOrderDetailRes.getReceiver_lng());
            }

            binding.tvEarning.setText("Total Earning : "+newOrderPickupOrderDetailRes.getCost()+" INR ");
            changeFrag(senderCourierDetailFragment, false, false);

            /*if (Base.activity_name.equals("NewPickupFragment"))
                binding.btHomenext.setText("Accept Pickup");
            else {*/
                if (pickup_status.equalsIgnoreCase("pickedup"))
                    binding.btHomenext.setText("Complete Order");
                else if (pickup_status.equalsIgnoreCase("delivered"))
                    binding.btHomenext.setText("Courier Delivered");
                else if(pickup_status.equalsIgnoreCase("new"))
                    binding.btHomenext.setText("Accept Order");
                else if(pickup_status.equalsIgnoreCase("accepted"))
                    binding.btHomenext.setText("Pickup Order");
                else if(pickup_status.equalsIgnoreCase("CANCELLED"))
                    binding.btHomenext.setText("CANCELLED");

            /*}*/
            senderCourierDetailFragment.onnewOrderPickupOrderDetailRes(newOrderPickupOrderDetailRes);
            reciverCourierDetailFragment.onnewOrderPickupOrderDetailRes(newOrderPickupOrderDetailRes);
            packageCourierDetailFragment.onnewOrderPickupOrderDetailRes(newOrderPickupOrderDetailRes);
            AppLog.e(TAG, "distance: " + distance + " ,time: " + time);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

  /*  @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        if (Base.activity_name.equals("NewPickupFragment"))
            bundle.putInt("tab_faragment", 0);
        else
            bundle.putInt("tab_faragment", 1);
        gotoActivity(HomeCourier.class, bundle, false);
    }*/
}
