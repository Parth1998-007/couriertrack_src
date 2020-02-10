package com.couriertrack.ui.home;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.api_model.CityListModel;
import com.couriertrack.api_model.CreateOrderModel;
import com.couriertrack.databinding.FragmentReceiverBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.home.map.FindAddress;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.couriertrack.utils.GpsHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class ReceiverFragment extends BaseFragment implements View.OnClickListener, FindAddress.FindAddressListener {

    private static final String TAG = "ReceiverFragment";
    FragmentReceiverBinding binding;
    Double lat,lag;
    CreateOrderModel.CreateOrderReq createOrderReq;

    public static ReceiverFragment newInstance(Bundle bundle) {
        ReceiverFragment fragment = new ReceiverFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_receiver, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receiver,container,false);

        init();
      //  initMapView(savedInstanceState);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home)getActivity()).setmarkerreceivervisibility(true);
    }

    private void init()
    {
        setadddress("",((Home)getActivity()).senderx,((Home)getActivity()).sendery);

        if(getArguments()!= null)
        {
            Type type = new TypeToken<CreateOrderModel.CreateOrderReq>(){}.getType();
            createOrderReq = new Gson().fromJson(getArguments().getString("createorderreq"),type);
        }

        AppLog.e(TAG," senderLATLAG: "+((Home)getActivity()).senderx+" : "+((Home)getActivity()).sendery);
        binding.btReceivnext.setOnClickListener(this);
        Home.fragmentName=TAG;
    }

    private boolean isValid()
    {
        if (isEmpty(binding.etrecievername, R.string.lbl_enter_receiver_name))
        {
            return false;
        }
        else if (isEmpty(binding.etrecievermobile, R.string.lbl_enter_receiver_mobile))
        {
            return false;
        }
        else if (validMobileNumber(binding.etrecievermobile, R.string.lbl_enter_valid_mobile))
        {
            return false;
        }
        else if (isEmpty(binding.etDropaddress, R.string.lbl_drop_addressenter))
        {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_receivnext:{
                if(isValid()){
                    setCreateOrderData();
                }
                break;
            }
        }
    }

    private void setCreateOrderData() {
        if(createOrderReq != null){
            createOrderReq.setReceiver_name(binding.etrecievername.getText().toString());
            createOrderReq.setReceiver_mobile(binding.etrecievermobile.getText().toString());
            createOrderReq.setReceiver_lat(String.valueOf(lat));
            createOrderReq.setReceiver_lng(String.valueOf(lag));
            createOrderReq.setDrop_address(binding.etDropaddress.getText().toString());

            ((Home)getActivity()).receiverx = lat;
            ((Home)getActivity()).receivery = lag;

            if(!distanceValid())
            {
                showToast("Drop Location Is Too Close , Please Select Receiver Lcoation Correctly");
                return;
            }
            // ((Home)getActivity()).setmarkerreceiver();
            Bundle b = new Bundle();
            b.putString("createorderreq",new Gson().toJson(createOrderReq));
            ((Home)getActivity()).changeFrag(PackageFragment.newInstance(b),true,false);
        }
    }

    private boolean distanceValid()
    {
        if(GpsHelper.distance(((Home) getActivity()).senderx , ((Home) getActivity()).sendery ,lat ,lag ) < 0.1)
        {
            return false;
        }
        return true;
    }

    public void setadddress(String address,double lt, double lg){
        AppLog.e(TAG," "+address);

        lat = lt;
        lag = lg;

        new FindAddress(lt,lg,getContext(),this).execute();

    }

    @Override
    public void onLocationDetect(String address, String city, String pincode) {

        if(binding!= null) {
            callcitylistAPi(address, city, pincode);
        }

    }

    private void callcitylistAPi(final String address, final String city, final String pincode) {
        showLoading();
        apiService.cityList(Integer.parseInt(appPref.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CityListModel.CityListRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<CityListModel.CityListRes> cityListRes) {
                        AppLog.e(TAG,"cityListRes :"+cityListRes.body());
                        if(isSuccess(cityListRes,cityListRes.body())){
                            onCityListRes(cityListRes.body(),address,city,pincode);
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

    private void onCityListRes(CityListModel.CityListRes cityListRes, String address, String city, String pincode) {
        ArrayList<String> citylist = new ArrayList<>();
        for(int i=0;i<cityListRes.getData().size();i++){
            citylist.add(cityListRes.getData().get(i).getCity());
        }

        if(binding!= null) {
            AppLog.e(TAG,"address: "+address);
            AppLog.e(TAG,"city: "+city);
            AppLog.e(TAG,"pincode: "+pincode);

            AppLog.e(TAG,"currentragment: "+getChildFragmentManager().findFragmentById(R.id.fragment));

            boolean isvalidcity = false;
            for(int i =0;i<citylist.size();i++){
                if(city.equalsIgnoreCase(citylist.get(i))){
                    isvalidcity = true;
                    break;
                }
            }

            if(isvalidcity){
                if(binding!= null){
                    try{
                        if(!address.equalsIgnoreCase("null")){
                            binding.etDropaddress.setText(""+address);
                        }else {
                            binding.etDropaddress.setText("");
                        }}catch (Exception e){}

                }
            }else {
                // showToast("Sorry we not available in your city");
                if(binding!= null){
                    if(!TextUtils.isEmpty(city)) {
                        showDialog(cityListRes);
                        // showToast("Sorry we not available in your city ");
                        binding.etDropaddress.setText("");
                    }
                }
            }

        }

    }


    private void showDialog(CityListModel.CityListRes cityListRes) {
        final Dialog dialog_con = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog_con.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_con.setContentView(R.layout.dialog_city);

        dialog_con.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvtitle = dialog_con.findViewById(R.id.lbl_title);
        tvtitle.setText(""+cityListRes.getCity_not_available_title());
        TextView tvmsg = dialog_con.findViewById(R.id.msg_con);
        tvmsg.setText(""+cityListRes.getCity_not_available());

        Button btok = dialog_con.findViewById(R.id.btOk);
        final CheckBox btnotagain = dialog_con.findViewById(R.id.cbnotagain);

        btok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                dialog_con.dismiss();
                ((Home)getActivity()).homeFragment.dismissdialog();

            }
        });
        dialog_con.show();
    }

}
