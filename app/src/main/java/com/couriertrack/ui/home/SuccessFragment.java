package com.couriertrack.ui.home;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.api_model.CreateOrderModel;
import com.couriertrack.databinding.FragmentSuccessBinding;
import com.couriertrack.ui.Base;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.courier.myorder.MyOrderCourierDetail;
import com.couriertrack.ui.myorder.MyOrder;
import com.couriertrack.utils.AppLog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SuccessFragment";
    FragmentSuccessBinding binding;
    CreateOrderModel.CreateOrderReq createOrderReq;
    private String order_status;
    public String onBackPress;
    public static SuccessFragment newInstance(Bundle bundle) {
        SuccessFragment fragment = new SuccessFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_success, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false);

        init();

        if (createOrderReq != null)
        {
            callCreateOrderAPI(createOrderReq);
        }

        return binding.getRoot();
    }

    private void init() {

        if (getArguments() != null) {
            Type type = new TypeToken<CreateOrderModel.CreateOrderReq>() {
            }.getType();
            createOrderReq = new Gson().fromJson(getArguments().getString("createorderreq"), type);
        }

        binding.btSuccessok.setOnClickListener(this);
        Home.fragmentName=TAG;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_successok: {
                //((Home)getActivity()).clearMap();
                //((Home)getActivity()).changeFrag(HomeFragment.newInstance(null),false,true);
                gotoActivity(MyOrder.class, null, true);
                // gotoActivity(MyOrder.class,null,false);
                break;
            }
        }
    }

    private void showConfirmationDialog(final String msg, String btn_okay) {

        final Dialog dialog_con = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog_con.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_con.setContentView(R.layout.dialog_confirmation);

        dialog_con.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button pauseOrderCancel = dialog_con.findViewById(R.id.btPauseOrderCancel);
        pauseOrderCancel.setVisibility(View.GONE);
        final TextView con_msg = dialog_con.findViewById(R.id.msg_con);
        con_msg.setText(msg);
        Button pauseOrderOkay = dialog_con.findViewById(R.id.btPauseOrderOkay);
        pauseOrderOkay.setText(btn_okay);
        pauseOrderOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_con.dismiss();
                gotoActivity(MyOrder.class, null, true);

            }
        });
        dialog_con.show();
    }

    private void callCreateOrderAPI(CreateOrderModel.CreateOrderReq createOrderReq) {
        AppLog.e(TAG, "createOrderReq : " + createOrderReq);
        showLoading();
        apiService.createOrder(createOrderReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CreateOrderModel.CreateOrderRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<CreateOrderModel.CreateOrderRes> createOrderRes) {
                        AppLog.e(TAG, "createOrderRes :" + createOrderRes);
                        if (isSuccess(createOrderRes, createOrderRes.body())) {
                            onCreateOrderRes(createOrderRes.body());
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

    private void onCreateOrderRes(CreateOrderModel.CreateOrderRes createOrderRes) {
        AppLog.e(TAG, "createOrderRes: " + createOrderRes);
        if (createOrderRes.isStatus()) {
            binding.tvOrderid.setText("" + createOrderRes.getOrder_id());
            order_status = createOrderRes.getOrder_status();
            binding.tvMsg.setText("" + createOrderRes.getMsg());
            if (order_status.equalsIgnoreCase("new"))
                binding.tvSuccess.setText("Order Successfully Placed");
             else if (order_status.equalsIgnoreCase("pending"))
             {
                binding.tvSuccess.setText("Order Saved As Pending");
                binding.tvMsg.setText("Your order is saved and will be visible after verification completes.");
             }
            else
                binding.tvSuccess.setText("Order Cancelled");

        } else {
            binding.tvSuccess.setText("Order Failed");
        }
    }


}
