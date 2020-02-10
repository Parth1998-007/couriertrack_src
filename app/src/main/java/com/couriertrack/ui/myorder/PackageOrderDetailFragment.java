package com.couriertrack.ui.myorder;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.OrderDetailModel;
import com.couriertrack.databinding.FragmentPackageOrderDetailBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.utils.AppLog;

/**
 * A simple {@link Fragment} subclass.
 */
public class PackageOrderDetailFragment extends BaseFragment {


    private static final String TAG = "PackageOrderDetailFragment";
    FragmentPackageOrderDetailBinding binding;
    OrderDetailModel.OrderDetailRes orderDetailRes;
    public static PackageOrderDetailFragment newInstance(Bundle bundle) {
        PackageOrderDetailFragment fragment = new PackageOrderDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_package_order_detail, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_package_order_detail,container,false);

        init();
        return binding.getRoot();
    }

    private void init() {
        if(orderDetailRes!=null){

            binding.tvweight.setText(""+orderDetailRes.getWeight());
            binding.tvtype.setText(""+orderDetailRes.getType());
            binding.tvdetail.setText(""+orderDetailRes.getDetail());
        }
    }

    public void onOrderDetailRes(OrderDetailModel.OrderDetailRes orderDetailRes) {
        //AppLog.e(TAG,"orderdetailRes :"+orderDetailRes);

        if(orderDetailRes.isStatus()){

            this.orderDetailRes = orderDetailRes;
               /* binding.tvweight.setText(""+orderDetailRes.getWeight());
                binding.tvtype.setText(""+orderDetailRes.getType());
                binding.tvdetail.setText(""+orderDetailRes.getDetail());*/
        }

    }
}
