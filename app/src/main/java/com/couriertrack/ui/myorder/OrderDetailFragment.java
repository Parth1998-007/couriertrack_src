package com.couriertrack.ui.myorder;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.couriertrack.R;
import com.couriertrack.api_model.OrderDetailModel;
import com.couriertrack.api_model.OrderListModel;
import com.couriertrack.databinding.FragmentOrderDetailBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.utils.AppLog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class OrderDetailFragment extends BaseFragment {

    private static final String TAG = "OrderDetailFragment";
    FragmentOrderDetailBinding binding;
    int[] tabIcon=new int[4];
    int[] tabicondash = new int[3];
    String[] tabLabels=new String[4];
    int orderid;
    OrderDetailModel.OrderDetailRes orderDetailRes;

    public static OrderDetailFragment newInstance(Bundle bundle)
    {
        OrderDetailFragment fragment = new OrderDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_order_detail, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail,container,false);
        init();
        //getOrderDetail(orderid);
        return binding.getRoot();
    }

    private void getOrderDetail(int orderid) {
        showLoading();
        apiService.orderDetail(Integer.parseInt(appPref.getUserId()),orderid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<OrderDetailModel.OrderDetailRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<OrderDetailModel.OrderDetailRes> orderDetailRes) {
                        if(isSuccess(orderDetailRes,orderDetailRes.body())){
                            onOrderDetailRes(orderDetailRes.body());
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

    public void onOrderDetailRes(OrderDetailModel.OrderDetailRes orderDetailRes) {
        //AppLog.e(TAG,"orderdetailRes :"+orderDetailRes);

        if(orderDetailRes.isStatus()){
          //  binding.tvdistance.setText(""+((OrderDetail)getActivity()).distance);
           // binding.tvtime.setText(""+((OrderDetail)getActivity()).time);
            this.orderDetailRes = orderDetailRes;
        }
    }

    private void init()
    {
        Bundle b = getActivity().getIntent().getExtras();
        if(b!= null){
            orderid = b.getInt("orderid",0);
        }

        if(orderDetailRes!=null)
        {
            binding.tvdistance.setText(""+orderDetailRes.getEstimated_distance());
            binding.tvtime.setText(""+orderDetailRes.getEstimated_time());
            binding.tvCost.setText(""+orderDetailRes.getCost());
            if(!orderDetailRes.getOrder_status().equalsIgnoreCase("new")&&!orderDetailRes.getOrder_status().equalsIgnoreCase("pending")&&!orderDetailRes.getOrder_status().equalsIgnoreCase("cancelled"))
            {
                binding.tvUserName.setText(""+orderDetailRes.getCourier_boy_name());
                binding.tvPhone.setText(""+orderDetailRes.getCourier_boy_mobile());
                binding.tvPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callCourier(orderDetailRes.getCourier_boy_mobile());
                    }
                });
                Glide.with(getActivity().getApplicationContext()).load(orderDetailRes.getCourier_boy_profile())
                        .apply(RequestOptions.placeholderOf(R.drawable.icon_driver).error(R.drawable.icon_driver))
                        .into(binding.ivDriverPtofile);
            }
            else
            {
                binding.tvUserName.setText("Waiting For Accept Order");
                binding.tvPhone.setText("");
            }

        }
    }

    private void callCourier(String number)
    {
        Uri uri = Uri.parse("tel:" + number);

        Intent sendIntent = new Intent(Intent.ACTION_DIAL, uri);

        this.startActivity(sendIntent);

    }


}
