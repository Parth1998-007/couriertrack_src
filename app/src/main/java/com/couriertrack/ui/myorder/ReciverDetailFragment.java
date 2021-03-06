package com.couriertrack.ui.myorder;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.OrderDetailModel;
import com.couriertrack.databinding.FragmentReciverDetailBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.utils.AppLog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReciverDetailFragment extends BaseFragment {


    private static final String TAG = "ReciverDetailFragment";
    FragmentReciverDetailBinding binding;
    OrderDetailModel.OrderDetailRes orderDetailRes;
    public static ReciverDetailFragment newInstance(Bundle bundle) {
        ReciverDetailFragment fragment = new ReciverDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_reciver_detail, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reciver_detail,container,false);

        init();

        return binding.getRoot();
    }

    private void init() {
        if(orderDetailRes != null){
            binding.tvrecivername.setText(""+orderDetailRes.getReceiver_name());
            binding.tvrecieverphone.setText(""+orderDetailRes.getReceiver_mobile());
            binding.tvreceiveraddress.setText(""+orderDetailRes.getDrop_address());
        }

        binding.ivPhone.setVisibility(View.GONE);
        binding.ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+binding.tvrecieverphone.getText().toString()));//change the number
                startActivity(callIntent);
            }
        });
    }

    public void onOrderDetailRes(OrderDetailModel.OrderDetailRes orderDetailRes) {
       // AppLog.e(TAG,"orderdetailRes :"+orderDetailRes);

        if(orderDetailRes.isStatus()){
            this.orderDetailRes = orderDetailRes;
        }

    }
}
