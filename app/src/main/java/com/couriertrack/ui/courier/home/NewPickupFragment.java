package com.couriertrack.ui.courier.home;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.api_model.AcceptedCourierModel;
import com.couriertrack.api_model.NewPickupOrderListModel;
import com.couriertrack.api_model.UpdateLocationModel;
import com.couriertrack.databinding.FragmentNewPickupBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.couriertrack.utils.GpsHelper;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPickupFragment extends BaseFragment implements NewPickupAdapter.ClickAcceptPickup, GpsHelper.GpsHelperListener {
    private FragmentNewPickupBinding binding;
    NewPickupAdapter newPickupAdapter;
    String TAG = "NewPickupFragment";
    NewPickupOrderListModel.NewPickupListReq newPickupListReq;
    private AcceptedCourierModel.OrderAcceptReq acceptPickupReq;
    private int position;
    GpsHelper gpsHelper;

    NotiCntReciever notiCntReciever;

    public NewPickupFragment()
    {
        // Required empty public constructor
    }

    public static NewPickupFragment newInstance(Bundle bundle)
    {
        NewPickupFragment newPickupFragment = new NewPickupFragment();
        newPickupFragment.setArguments(bundle);
        return newPickupFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_pickup, container, false);
        init();
        AppLog.e(TAG, "User Id :" + Integer.parseInt(appPref.getUserId()));
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuaction = menu.findItem(R.id.menu_action);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.menu_refresh, null);

        menuaction.setActionView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!appPref.getString(AppPref.USER_Longitude).equals("") || !appPref.getString(AppPref.USER_Latitude).equals(""))
                    setNewPickupOrderListData("" + appPref.getString(AppPref.USER_Latitude), "" + appPref.getString(AppPref.USER_Longitude));
                /*else {
                    gpsHelper = new GpsHelper(this, context);
                    gpsHelper.startLocationUpdates();
                }*/

            }
        });


    }

    void init()
    {
        newPickupAdapter = new NewPickupAdapter();
        newPickupAdapter.setViewAcceptItemClick(this);
        setNewPickupData();
    }

    private class NotiCntReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            AppLog.e(TAG," msgCount onReceive()");

            setNewPickupOrderListData(appPref.getString(AppPref.USER_Latitude) , appPref.getString(AppPref.USER_Longitude));
        }
    }

    private void setNewPickupOrderListData(String latitude, String longitude)
    {
        AppLog.e(TAG,"LAT "+latitude);
        AppLog.e(TAG,"Log "+longitude);
        newPickupListReq = new NewPickupOrderListModel.NewPickupListReq();
        newPickupListReq.setUsre_id(Integer.parseInt(appPref.getUserId()));
        newPickupListReq.setLat(latitude);
        newPickupListReq.setLng(longitude);
        callNewPickupOrderListAPI(newPickupListReq);

    }

    private void callNewPickupOrderListAPI(NewPickupOrderListModel.NewPickupListReq newPickupListReq) {
        showLoading();
        apiService.newpickorderlist(newPickupListReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<NewPickupOrderListModel.PickUpOrderListRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<NewPickupOrderListModel.PickUpOrderListRes> pickUpOrderListResResponse) {
                        AppLog.e(TAG, "Responce : " + pickUpOrderListResResponse);
                        AppLog.e(TAG, "Responce : " + pickUpOrderListResResponse.body());

                        if(pickUpOrderListResResponse.body() !=null)
                        if (pickUpOrderListResResponse.body().isStatus()) {

                            if (isSuccess(pickUpOrderListResResponse, pickUpOrderListResResponse.body())) {

                                if (pickUpOrderListResResponse.body().getPickupOrders().size() == 0)
                                {
                                    binding.tvDataError.setVisibility(View.VISIBLE);
                                } else {
                                    binding.tvDataError.setVisibility(View.GONE);
                                    binding.tvDataError.setText(getResources().getString(R.string.no_pickup_request));
                                    newPickupAdapter.clear();
                                    newPickupAdapter.addData(pickUpOrderListResResponse.body().getPickupOrders(), "NewPickupFragment");
                                }

                            }
                        } else {
                            binding.tvDataError.setVisibility(View.VISIBLE);
                            binding.tvDataError.setText(getResources().getString(R.string.no_pickup_request));
                        }
                        else
                        {
                            showToast("Please Try Again !");
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

    void setNewPickupData() {
        binding.rvnewpickup.setAdapter(newPickupAdapter);
    }

    private void setAcceptPickupData(int user_id, int order_id, NewPickupOrderListModel.NewPickupOrderListDetail order) {
        AppLog.e(TAG, "User Id : " + user_id + " Order Id : " + order_id);
        acceptPickupReq = new AcceptedCourierModel.OrderAcceptReq();
        acceptPickupReq.setUser_id(user_id);
        acceptPickupReq.setOrder_id(order_id);
        callAcceptPickup(acceptPickupReq , order);
    }

    private void callAcceptPickup(final AcceptedCourierModel.OrderAcceptReq acceptPickupReq, final NewPickupOrderListModel.NewPickupOrderListDetail order)
    {
        showLoading();
        apiService.acceptOrder(acceptPickupReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<AcceptedCourierModel.OrderAcceptRes>>()
                {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<AcceptedCourierModel.OrderAcceptRes> acceptPickup)
                    {
                        AppLog.e(TAG,  "Responce" +acceptPickup );
                        AppLog.e(TAG, acceptPickup.body() + "");
                        if (acceptPickup.body().isStatus()) {
                            showToast(acceptPickup.body().getMsg());

                            newPickupAdapter.remove(position);
                            GpsHelper.navigateUser(getActivity() , order.getSender_lat() , order.getSender_lng());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        onFailure(e);
                        AppLog.e(TAG,  "Error " +e.getLocalizedMessage() );
                    }

                    @Override
                    public void onComplete() {
                        onDone();
                        AppLog.e(TAG,  "Completed " );
                    }
                });
    }


    @Override
    public void onClickAccepyPickup(NewPickupOrderListModel.NewPickupOrderListDetail order, int position)
    {
        showConfirmationDialog("Are you sure to accept Order?", "Cancel", "Accept", order);
        this.position = position;
    }

    private void showConfirmationDialog(final String msg, String btn_cancel, String btn_okay, final NewPickupOrderListModel.NewPickupOrderListDetail order)
    {

        final Dialog dialog_con = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog_con.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_con.setContentView(R.layout.dialog_confirmation);

        dialog_con.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView con_msg = dialog_con.findViewById(R.id.msg_con);
        con_msg.setText(msg);

        final TextView orderID = dialog_con.findViewById(R.id.lbl_title_value);
        orderID.setText(""+order.getOrder_id());

        Button pauseOrderOkay = dialog_con.findViewById(R.id.btPauseOrderOkay);
        pauseOrderOkay.setText(btn_okay);
        pauseOrderOkay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog_con.dismiss();
                setAcceptPickupData(Integer.parseInt(appPref.getUserId()), order.getOrder_id() , order);

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

    @Override
    public void onLocationChanged(Location location) {
        hideLoading();
        appPref.set(AppPref.USER_Latitude, "" + location.getLatitude());
        appPref.set(AppPref.USER_Longitude, "" + location.getLongitude());
        setNewPickupOrderListData("" + location.getLatitude(), "" + location.getLongitude());
        gpsHelper.stopLocationUpdates();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!appPref.getString(AppPref.USER_Longitude).equals("") || !appPref.getString(AppPref.USER_Latitude).equals(""))
            setNewPickupOrderListData("" + appPref.getString(AppPref.USER_Latitude), "" + appPref.getString(AppPref.USER_Longitude));
        else {
            gpsHelper = new GpsHelper(this, context);
            gpsHelper.startLocationUpdates();
        }

        notiCntReciever=new NotiCntReciever();
        getActivity().registerReceiver(notiCntReciever,new IntentFilter(getActivity().getPackageName()+"."+AppPref.SCREEN_OPEN_COURIER_HOME));
    }

    @Override
    public void onStop()
    {
        super.onStop();

        try {
            if(notiCntReciever!=null){
                getActivity().unregisterReceiver(notiCntReciever);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
