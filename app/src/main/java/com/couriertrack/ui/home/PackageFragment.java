package com.couriertrack.ui.home;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.api_model.CreateOrderModel;
import com.couriertrack.databinding.FragmentPackageBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.SelectionAdapter;
import com.couriertrack.utils.AppLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */

public class PackageFragment extends BaseFragment implements View.OnClickListener, SelectionAdapter.Selectitemlistener {

    private static final String TAG = "PackageFragment";
    FragmentPackageBinding binding;
    CreateOrderModel.CreateOrderReq createOrderReq;
    SelectionAdapter selectionAdapter;
    int selecpostype=-1;
    int selecweighttype=-1;

    public static PackageFragment newInstance(Bundle bundle) {
        PackageFragment fragment = new PackageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_package, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_package,container,false);

        init();

        return binding.getRoot();
    }

    private void init()
    {
        if(getArguments()!= null){
            Type type = new TypeToken<CreateOrderModel.CreateOrderReq>(){}.getType();
            createOrderReq = new Gson().fromJson(getArguments().getString("createorderreq"),type);
        }

        binding.btPkgnext.setOnClickListener(this);
        AppLog.e(TAG," RECIVERLATLAG: "+((Home)getActivity()).receiverx+" : "+((Home)getActivity()).receivery);
        ((Home)getActivity()).setmarkersendervisibility(false);
        ((Home)getActivity()).setmarkerreceivervisibility(false);

        ((Home)getActivity()).drawpath();
        Home.fragmentName=TAG;

        binding.etType.setOnClickListener(this);
        binding.etWeight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_pkgnext:{
                if(isValid()){
                    setCreateOrderData();
                }
                break;
            }
            case R.id.et_type:{
                showDialogforselection(selecpostype,"type");
                break;
            }
            case R.id.et_weight:{
                showDialogforselection(selecweighttype,"weight");
                break;
            }
        }
    }

    private void showDialogforselection(int selecpos, String text) {
        selectionAdapter = new SelectionAdapter(text);
        selectionAdapter.SetSelectItemListener(this);

        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_selection);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvselect = dialog.findViewById(R.id.tv_select);
        if(text.equalsIgnoreCase("type")){
            tvselect.setText("Tap on type to select.");

        }else {
            tvselect.setText("Tap on range to select.\n" +
                    "Maximum weight allowed is 5Kg \n" +
                    "( Refer Terms & Conditions – 3. Services)");

        }

        Button btselect = dialog.findViewById(R.id.btnSelect);
        btselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        RecyclerView rvselect = dialog.findViewById(R.id.rvselect);
        rvselect.setAdapter(selectionAdapter);

        ArrayList<String> datalist = new ArrayList<>();
        if(text.equalsIgnoreCase("type")){
            datalist.clear();
            datalist.add("Papers");
            datalist.add("Goods");
            datalist.add("Belongings");
            datalist.add("Consumables");
            datalist.add("Others");
        }else {
            datalist.clear();
            datalist.add("0-1 Kg");
            datalist.add("1-2 Kg");
            datalist.add("2-3 Kg");
            datalist.add("3-4 Kg");
            datalist.add("4-5 Kg");
        }

        setselectionitem(selecpos,datalist);

        dialog.show();
    }

    private void setselectionitem(int selecposcode, ArrayList<String> selectionitem) {
        selectionAdapter.clear();
        selectionAdapter.additemprimary(selecposcode,selectionitem);
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(binding.etWeight.getText().toString())) {
            showToast("Select Weight Range");
            return false;
        } else if (TextUtils.isEmpty(binding.etType.getText().toString())) {
            showToast("Select Type of Package");
            return false;
        }/*else if (isEmpty(binding.etDetail, R.string.lbl_detail_pkg)) {
            return false;
        }*/
        return true;
    }

    private void setCreateOrderData() {
        if(createOrderReq != null){
            createOrderReq.setWeight(binding.etWeight.getText().toString());
            createOrderReq.setType(binding.etType.getText().toString());
            createOrderReq.setDetail(binding.etDetail.getText().toString());
            Bundle b = new Bundle();
            b.putString("createorderreq",new Gson().toJson(createOrderReq));
            ((Home)getActivity()).changeFrag(ConfirmOrderFragment.newInstance(b),true,false);
        }
    }

    @Override
    public void selectitem(String tag, String item, int adapterPosition) {

        if(tag.equalsIgnoreCase("weight")){
            selecweighttype = adapterPosition;
            binding.etWeight.setText(item);
        }else {
            selecpostype = adapterPosition;
            binding.etType.setText(item);
        }

    }
}
