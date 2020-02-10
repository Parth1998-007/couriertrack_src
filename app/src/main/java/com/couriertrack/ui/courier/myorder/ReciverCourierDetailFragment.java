package com.couriertrack.ui.courier.myorder;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.couriertrack.R;
import com.couriertrack.api_model.NewPickupOrderDetailModel;
import com.couriertrack.databinding.FragmentReciverDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 */

public class ReciverCourierDetailFragment extends Fragment {

    private NewPickupOrderDetailModel.NewPickupOrderDetailRes newOrderPickupOrderDetailRes;
    private FragmentReciverDetailBinding binding;

    public static ReciverCourierDetailFragment newInstance(Bundle bundle) {
        ReciverCourierDetailFragment senderCourierDetailFragment = new ReciverCourierDetailFragment();
        senderCourierDetailFragment.setArguments(bundle);
        return senderCourierDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reciver_detail, container, false);
        init();
        return binding.getRoot();
    }

    void init() {

        if (newOrderPickupOrderDetailRes != null) {
            binding.tvrecivername.setText(""+newOrderPickupOrderDetailRes.getReceiver_name());
            binding.tvreceiveraddress.setText(""+newOrderPickupOrderDetailRes.getDrop_address());
            binding.tvrecieverphone.setText(""+newOrderPickupOrderDetailRes.getReceiver_mobile());
        }

        binding.ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+binding.tvrecieverphone.getText().toString()));//change the number
                startActivity(callIntent);
            }
        });
    }

    public void onnewOrderPickupOrderDetailRes(NewPickupOrderDetailModel.NewPickupOrderDetailRes newOrderPickupOrderDetailRes) {
        this.newOrderPickupOrderDetailRes = newOrderPickupOrderDetailRes;
    }

}
