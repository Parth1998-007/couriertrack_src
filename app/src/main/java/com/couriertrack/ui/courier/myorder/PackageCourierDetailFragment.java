package com.couriertrack.ui.courier.myorder;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.NewPickupOrderDetailModel;
import com.couriertrack.databinding.FragmentPackageOrderDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class PackageCourierDetailFragment extends Fragment {

    private FragmentPackageOrderDetailBinding binding;
    private NewPickupOrderDetailModel.NewPickupOrderDetailRes newOrderPickupOrderDetailRes;

    public static PackageCourierDetailFragment newInstance(Bundle bundle)
    {
        PackageCourierDetailFragment senderCourierDetailFragment = new PackageCourierDetailFragment();
        senderCourierDetailFragment.setArguments(bundle);
        return senderCourierDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_package_order_detail, container, false);
        init();
        return binding.getRoot();
    }
    void init()
    {
        if (newOrderPickupOrderDetailRes != null)
        {
            binding.tvweight.setText(""+newOrderPickupOrderDetailRes.getWeight() + " KG");
            binding.llPackagedetail.setVisibility(View.GONE);
            binding.llPackagetype.setVisibility(View.GONE);
          //  binding.tvtype.setText(""+newOrderPickupOrderDetailRes.getType());
          //  binding.tvdetail.setText(""+newOrderPickupOrderDetailRes.getDetail());
        }
    }
    public void onnewOrderPickupOrderDetailRes(NewPickupOrderDetailModel.NewPickupOrderDetailRes newOrderPickupOrderDetailRes) {
        this.newOrderPickupOrderDetailRes=newOrderPickupOrderDetailRes;
    }
}
