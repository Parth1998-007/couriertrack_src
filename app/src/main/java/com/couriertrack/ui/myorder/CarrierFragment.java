package com.couriertrack.ui.myorder;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.OrderDetailModel;
import com.couriertrack.databinding.FragmentCarrierBinding;
import com.couriertrack.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */

public class CarrierFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "CarrierFragment";
    FragmentCarrierBinding binding;
    OrderDetailModel.OrderDetailRes orderDetailRes;

    public static CarrierFragment newInstance(Bundle bundle)
    {
        CarrierFragment fragment = new CarrierFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_carrier, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_carrier,container,false);

        init();

        return binding.getRoot();
    }

    private void init() {
        if(orderDetailRes!= null){

            if(!TextUtils.isEmpty(orderDetailRes.getCourier_boy_name()) && !TextUtils.isEmpty(orderDetailRes.getCourier_boy_mobile())){
                binding.ivPhone.setVisibility(View.VISIBLE);
            }else {
                binding.ivPhone.setVisibility(View.GONE);
            }

            if(!TextUtils.isEmpty(orderDetailRes.getCourier_boy_name())){
                binding.tvname.setText(""+orderDetailRes.getCourier_boy_name());
            }else {
                binding.tvname.setText("");
            }

            if(!TextUtils.isEmpty(orderDetailRes.getCourier_boy_mobile())){
                binding.tvmobile.setText(""+orderDetailRes.getCourier_boy_mobile());
            }else {
                binding.tvmobile.setText("");
            }


        }

        binding.ivPhone.setOnClickListener(this);
    }

    public void onOrderDetailRes(OrderDetailModel.OrderDetailRes orderDetailRes) {
        //AppLog.e(TAG,"orderdetailRes :"+orderDetailRes);

        if(orderDetailRes.isStatus()){
            this.orderDetailRes = orderDetailRes;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_phone:{
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+binding.tvmobile.getText().toString()));//change the number
                startActivity(callIntent);
                break;
            }
        }
    }
}
