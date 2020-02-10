package com.couriertrack.ui.courier.myorder;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.NewPickupOrderDetailModel;
import com.couriertrack.databinding.FragmentNewPickupBinding;
import com.couriertrack.databinding.FragmentSenderDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class SenderCourierDetailFragment extends Fragment {

    private NewPickupOrderDetailModel.NewPickupOrderDetailRes newOrderPickupOrderDetailRes;
    FragmentSenderDetailBinding binding;

    public static SenderCourierDetailFragment newInstance(Bundle bundle)
    {
        SenderCourierDetailFragment senderCourierDetailFragment=new SenderCourierDetailFragment();
        senderCourierDetailFragment.setArguments(bundle);
        return senderCourierDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_sender_detail,container,false);
        init();
        return binding.getRoot();
    }

    void init() {

        if (newOrderPickupOrderDetailRes != null) {
            binding.tvsendername.setText(""+newOrderPickupOrderDetailRes.getSender_name());
            binding.tvsenderaddress.setText(""+newOrderPickupOrderDetailRes.getPickup_address());
            binding.tvsenderphone.setText(""+newOrderPickupOrderDetailRes.getSender_mobile());
        }

        binding.ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+binding.tvsenderphone.getText().toString()));//change the number
                startActivity(callIntent);
            }
        });

    }

    public void onnewOrderPickupOrderDetailRes(NewPickupOrderDetailModel.NewPickupOrderDetailRes newOrderPickupOrderDetailRes) {
        this.newOrderPickupOrderDetailRes = newOrderPickupOrderDetailRes;
    }

}
