package com.couriertrack.ui.home.support;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.couriertrack.R;
import com.couriertrack.api_model.CourierOrderList;
import com.couriertrack.api_model.PricingModel;
import com.couriertrack.databinding.ActivityPricingBinding;
import com.couriertrack.ui.Base;
import com.couriertrack.utils.AppLog;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class Pricing extends Base {

    private static final String TAG = "Pricing";
    ActivityPricingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pricing);

        setToolbar();
        setTitle("Pricing");
        enableBack(true);

        callPricingAPI();
    }

    private void callPricingAPI() {
        apiService.pricing(Integer.parseInt(appPref.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<PricingModel.PricingRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        AppLog.e(TAG, "Disposable Show Loding : " + d.isDisposed());
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<PricingModel.PricingRes> pricingResResponse) {

                            if (isSuccess(pricingResResponse, pricingResResponse.body())) {
                                setData(pricingResResponse.body());
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

    private void setData(PricingModel.PricingRes pricingRes) {
        if(pricingRes.isStatus()){
            binding.tvTitle.setText(""+pricingRes.getData().getTitle());
            binding.tvBasicfare.setText("Basic fare : Rs."+pricingRes.getData().getBasicfare());
            binding.tvDistancefee.setText("Distance fee* : Rs."+pricingRes.getData().getDistancefee()+" per KM");
            binding.tvMinimumfee.setText("*- minimum applicable distance fee is Rs."+pricingRes.getData().getMinimumfee()+".");
            binding.tvNote.setText("Note: "+pricingRes.getData().getNote());

        }
    }
}
