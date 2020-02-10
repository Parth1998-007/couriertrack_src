package com.couriertrack.ui.courier.home;

import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.CourierOrderList;
import com.couriertrack.databinding.FragmentMyOrderCourierBinding;
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
public class MyOrderCourierFragment extends BaseFragment implements GpsHelper.GpsHelperListener {
    public String TAG = "MyOrderCourierFragment";
    FragmentMyOrderCourierBinding binding;
    GpsHelper gpsHelper;
    private NewPickupAdapter orderListAdapter;
    private static final int REQUEST_LOCATION = 1;

    public MyOrderCourierFragment() {
        // Required empty public constructor
    }

    public static MyOrderCourierFragment newInstance(Bundle bundle) {
        MyOrderCourierFragment myOrderCourier = new MyOrderCourierFragment();
        myOrderCourier.setArguments(bundle);
        return myOrderCourier;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_order_courier, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    void init() {
        orderListAdapter = new NewPickupAdapter();
        setNewPickupData();
    }

    private void setOrderListData(String latitude, String longitude) {
        AppLog.e(TAG, "setOrderListData : " + longitude);

        CourierOrderList.OrderListReq orderListReq = new CourierOrderList.OrderListReq();
        orderListReq.setUsre_id(Integer.parseInt(appPref.getUserId()));
        orderListReq.setLat(latitude);
        orderListReq.setLng(longitude);
        callNewPickupOrderListAPI(orderListReq);

    }

    private void callNewPickupOrderListAPI(CourierOrderList.OrderListReq orderListReq) {
        AppLog.e(TAG, "orderListReq : " + orderListReq);

        apiService.courierOrderList(orderListReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CourierOrderList.OrderListRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        AppLog.e(TAG, "Disposable Show Loding : " + d.isDisposed());
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<CourierOrderList.OrderListRes> orderListResResponse) {
                        AppLog.e(TAG, "Responce : " + orderListResResponse);
                        AppLog.e(TAG, "Responce : " + orderListResResponse.body());
                        AppLog.e(TAG, "Responce : " + orderListResResponse.body().getPickupOrders());
                        if (orderListResResponse.body().isStatus()) {
                            if (isSuccess(orderListResResponse, orderListResResponse.body())) {

                                if (orderListResResponse.body().getPickupOrders().size() == 0) {
                                    binding.tvDataError.setVisibility(View.VISIBLE);
                                    binding.tvDataError.setText("You have not accepted any order");
                                } else {
                                    binding.tvDataError.setVisibility(View.GONE);
                                    orderListAdapter.clear();
                                    orderListAdapter.addMyOrderData(orderListResResponse.body().getPickupOrders(), "MyOrderCourierFragment");
                                }

                            }
                        } else {
                            binding.tvDataError.setVisibility(View.VISIBLE);
                            binding.tvDataError.setText("" + orderListResResponse.body().getMsg());

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
        binding.rvOrderList.setAdapter(orderListAdapter);
    }

    @Override
    public void onLocationChanged(Location location) {
        AppLog.e(TAG, "Latitude : " + location.getLatitude());
        appPref.set(AppPref.USER_Latitude, "" + location.getLatitude());
        appPref.set(AppPref.USER_Longitude, "" + location.getLongitude());
        setOrderListData("" + location.getLatitude(), "" + location.getLongitude());
        gpsHelper.stopLocationUpdates();
    }

    @Override
    public void onStart() {
        onstart_run = onstart_run + 1;
        AppLog.e(TAG, "onStart_2 Time");
       // if (onstart_run == 1) {
            AppLog.e(TAG, "onStart");
            if (!appPref.getString(AppPref.USER_Longitude).equals("") || !appPref.getString(AppPref.USER_Latitude).equals("")) {
                setOrderListData("" + appPref.getString(AppPref.USER_Latitude), "" + appPref.getString(AppPref.USER_Longitude));
            } else {
                gpsHelper = new GpsHelper(this, context);
                gpsHelper.startLocationUpdates();

            }
       // }
        super.onStart();
    }


}
