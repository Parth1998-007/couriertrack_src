package com.couriertrack.ui.myorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.LoginModel;
import com.couriertrack.api_model.OrderListModel;
import com.couriertrack.databinding.FragmentMyOrderBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.ui.login.SignUpFragment;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrderFragment extends BaseFragment implements MyOrderAdapter.OrderItemClicked {

    private static final String TAG = "MyOrderFragment";
    FragmentMyOrderBinding binding;
    MyOrderAdapter myOrderAdapter;
    NotiCntReciever notiCntReciever;

    public static MyOrderFragment newInstance(Bundle bundle)
    {
        MyOrderFragment fragment = new MyOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_my_order, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_order,container,false);
        init();

        return binding.getRoot();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        getOrderList();
        notiCntReciever=new NotiCntReciever();
        getActivity().registerReceiver(notiCntReciever,new IntentFilter(getActivity().getPackageName()+"."+ AppPref.SCREEN_OPEN_MYORDER));
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

    private class NotiCntReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AppLog.e(TAG," msgCount onReceive()");
            getOrderList();
        }
    }

    private void getOrderList()
    {
        showLoading();
        apiService.orderlist(Integer.parseInt(appPref.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<OrderListModel.OrderListRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<OrderListModel.OrderListRes> orderListRes) {
                        AppLog.e(TAG,"orderListRes :"+orderListRes);
                        if(isSuccess(orderListRes,orderListRes.body())){
                            onOrderListRes(orderListRes.body());
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

    private void onOrderListRes(OrderListModel.OrderListRes orderListRes)
    {
        if(orderListRes.isStatus()){
            if(orderListRes.getOrders().size()==0){
                binding.tvnoorder.setVisibility(View.VISIBLE);
            }else {
                binding.tvnoorder.setVisibility(View.GONE);
                myOrderAdapter.clear();
                myOrderAdapter.addOrders(orderListRes.getOrders());
            }

        }
    }

    private void setOrderList() {
        binding.rvmyorder.setAdapter(myOrderAdapter);
    }

    private void init() {
        myOrderAdapter = new MyOrderAdapter();
        myOrderAdapter.setOrderItemClicked(this);
        setOrderList();
    }

    @Override
    public void OrderClicked(int orderid)
    {
        Bundle b =new Bundle();
        b.putInt("orderid",orderid);
        gotoActivity(OrderDetail.class,b,false);
    }
}
