package com.couriertrack.ui.home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.couriertrack.R;
import com.couriertrack.api_model.CreateOrderModel;
import com.couriertrack.api_model.GetPricingModel;
import com.couriertrack.api_model.LoginModel;
import com.couriertrack.api_model.NewPickupOrderListModel;
import com.couriertrack.databinding.FragmentConfirmOrderBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.utils.AppLog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class ConfirmOrderFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ConfirmOrderFragment";
    FragmentConfirmOrderBinding binding;
    CreateOrderModel.CreateOrderReq createOrderReq;
    int cost;


    public static ConfirmOrderFragment newInstance(Bundle bundle) {
        ConfirmOrderFragment fragment = new ConfirmOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_confirm_order, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_confirm_order,container,false);

        init();

        return binding.getRoot();
    }

    private void init()
    {

        if(getArguments()!= null)
        {
            Type type = new TypeToken<CreateOrderModel.CreateOrderReq>(){}.getType();
            createOrderReq = new Gson().fromJson(getArguments().getString("createorderreq"),type);
        }

        binding.tvdistance.setText(""+((Home)getActivity()).distance);
        binding.tvtime.setText(""+((Home)getActivity()).time);

        //
        getPricingFromAPI();

        binding.btConfirmorder.setOnClickListener(this);
        Home.fragmentName=TAG;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bt_confirmorder:
                {
                if(isValid()){
                    setCreateOrderData();
                }
                //((Home)getActivity()).changeFrag(SuccessFragment.newInstance(null),true,false);
                break;
            }
        }
    }

    private void getPricingFromAPI()
    {
        GetPricingModel.GetPricingReq pricingReq = new GetPricingModel.GetPricingReq();
        pricingReq.setType("type1");
        pricingReq.setUserID(appPref.getUserId());
        pricingReq.setDistance(""+((Home) getActivity()).distanceValue/1000.0);

        callPricingAPI(pricingReq);
    }

    private void calculateAndSetCost(int distance , float baseprice , float priceperkm , float firstfixkm , float firstfixrate , float payableprice)
    { /// PRICE SHOWING DIRECTLY FROM  API
        int cost = 0;

            if(distance/1000.0 < firstfixkm)
            cost = (int)(baseprice + firstfixkm*firstfixrate );
            else
            cost = (int)(baseprice + firstfixkm*firstfixrate + ((distance/1000.0)-firstfixkm)*priceperkm);

            this.cost = (int) payableprice;
            binding.tvCost.setText(this.cost+"");
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(binding.tvdistance.getText()))
        {
            showToast(getResources().getString(R.string.lbl_estimated_distance));
            return false;
        }
        else if (TextUtils.isEmpty(binding.tvtime.getText()))
        {
            showToast(getResources().getString(R.string.lbl_estimated_time));
            return false;
        }
        else if (TextUtils.isEmpty(binding.tvCost.getText()))
        {
            showToast(getResources().getString(R.string.lbl_estimated_cost));
            return false;
        }
        return true;
    }


    private void setCreateOrderData()
    {
        if(createOrderReq!= null)
        {
            createOrderReq.setCost(binding.tvCost.getText().toString());
            createOrderReq.setEstimated_distance(binding.tvdistance.getText().toString());
            createOrderReq.setEstimated_time(binding.tvtime.getText().toString());
            Bundle b = new Bundle();
            b.putString("createorderreq",new Gson().toJson(createOrderReq));
            b.putInt("cost" , cost);
            //((Home)getActivity()).changeFrag(SuccessFragment.newInstance(b),true,false);
            ((Home) getActivity()).startPayment(b);
           // callCreateOrderAPI(createOrderReq);
        }
    }

    public void callCreateOrderAPI(CreateOrderModel.CreateOrderReq createOrderReq)
    {
        AppLog.e(TAG,"createOrderReq : "+createOrderReq);
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
                    public void onNext(Response<CreateOrderModel.CreateOrderRes> createOrderRes)
                    {
                        AppLog.e(TAG,"createOrderRes :"+createOrderRes);
                        if(isSuccess(createOrderRes,createOrderRes.body()))
                        {
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

    public void callPricingAPI(GetPricingModel.GetPricingReq getPricingReq)
    {
        AppLog.e(TAG,"getPricingReq : "+getPricingReq);
        showLoading();
        apiService.getPricing(getPricingReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<GetPricingModel.GetPricingResponse>>()
                {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<GetPricingModel.GetPricingResponse> getPricingResponse)
                    {
                        AppLog.e(TAG,"getPricingResponse :"+getPricingResponse);
                        if(isSuccess(getPricingResponse,getPricingResponse.body()))
                        {
                            calculateAndSetCost(((Home)getActivity()).distanceValue,getPricingResponse.body().getPricing().getBase_price() , getPricingResponse.body().getPricing().getPrice_per_km() , getPricingResponse.body().getPricing().getFirst_fix_km() , getPricingResponse.body().getPricing().getFirst_fix_rate() , getPricingResponse.body().getPricing().getPayable_price());
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        onFailure(e);
                    }

                    @Override
                    public void onComplete()
                    {
                        onDone();
                    }
                });
    }

    private void onCreateOrderRes(CreateOrderModel.CreateOrderRes createOrderRes)
    {
        AppLog.e(TAG,"createOrderRes: "+createOrderRes);
        if(createOrderRes.isStatus()){
            showToast("orderSuccess");
        }
    }

}
