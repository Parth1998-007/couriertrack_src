package com.couriertrack.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.couriertrack.api_model.OrderListModel;
import com.couriertrack.databinding.FragmentHomeBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.home.map.FindAddress;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.gson.Gson;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements View.OnClickListener, FindAddress.FindAddressListener {
    private static final String TAG = "HomeFragment";
    FragmentHomeBinding binding;
    Double lat, lag;
    String mobilePattern = "[0-9]{10}";
    Dialog dialog_con;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(Bundle bundle) {
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        init();

        //initMapView(savedInstanceState);
        AppLog.e(TAG, "USER ID : " + appPref.getUserId());
        return binding.getRoot();
    }

    private void showworkingTimeDilaog(String title, String msg) {
        final Dialog dialog_con = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog_con.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_con.setContentView(R.layout.dialog_worktime);

        dialog_con.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvtitle = dialog_con.findViewById(R.id.lbl_title);
        tvtitle.setText(""+title);
        TextView tvmsg = dialog_con.findViewById(R.id.msg_con);
        tvmsg.setText(""+msg);

        Button btok = dialog_con.findViewById(R.id.btOk);
        final CheckBox btnotagain = dialog_con.findViewById(R.id.cbnotagain);

        btok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
               if(btnotagain.isChecked()){
                   appPref.set(AppPref.WORKINGTIMEMSG,true);
               }else {
                   appPref.set(AppPref.WORKINGTIMEMSG,false);
               }
                dialog_con.dismiss();
            }
        });
        appPref.set(AppPref.UPDATESHOWWORKINGMSG,true);
        dialog_con.show();
    }

    private void init() {
        ((Home) getActivity()).setmarkersendervisibility(true);

        binding.etSendername.setText(appPref.getString(AppPref.NAME));
        binding.etSendermobile.setText(appPref.getString(AppPref.MOBILE));
        binding.btHomenext.setOnClickListener(this);
        Home.fragmentName=TAG;
      /*  address = ((Home)getActivity()).getaddressfrommap();
        AppLog.e(TAG," "+address);*/


    }

    public void setadddress(String address, double lt, double lg)
    {
        AppLog.e(TAG, " " + address);

        lat = lt;
        lag = lg;
        new FindAddress(lt, lg, getContext(), this).execute();

    }

    //Initialized Map View

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_homenext: {
                if (isValid()) {
//                    if(binding.etSendermobile.getText().toString().matches(mobilePattern))
                    setCreateOrderData();
//                    else
//                        showToast("Invalid Mobile Number");
                }
                break;
            }
        }
    }

    private boolean isValid() {
        if (isEmpty(binding.etSendername, R.string.lbl_enter_your_name)) {
            return false;
        } else if (isEmpty(binding.etSendermobile, R.string.lbl_enter_your_mobile)) {
            return false;
        }
        else if (validMobileNumber(binding.etSendermobile, R.string.lbl_enter_valid_mobile)) {
            return false;
        }
        else if (isEmpty(binding.etPickupaddress, R.string.lbl_pick_addressenter)) {
            return false;
        }
        return true;
    }

    private void setCreateOrderData() {
        CreateOrderModel.CreateOrderReq createOrderReq = new CreateOrderModel.CreateOrderReq();
        createOrderReq.setUser_id("" + appPref.getUserId());
        createOrderReq.setSender_name(binding.etSendername.getText().toString());
        createOrderReq.setSender_mobile(binding.etSendermobile.getText().toString());
        createOrderReq.setPickup_address(binding.etPickupaddress.getText().toString());
        createOrderReq.setSender_lat(String.valueOf(lat));
        createOrderReq.setSender_lng(String.valueOf(lag));

        ((Home) getActivity()).senderx = lat;
        ((Home) getActivity()).sendery = lag;
        Bundle b = new Bundle();
        b.putString("createorderreq", new Gson().toJson(createOrderReq));
        ((Home) getActivity()).receiverFragment.setArguments(b);
        ((Home) getActivity()).changeFrag(((Home) getActivity()).receiverFragment, true, false);//ReceiverFragment.newInstance(b)
    }

    @Override
    public void onLocationDetect(String address, String city, String pincode) {
        callcitylistAPi(address,city,pincode);

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
                        AppLog.e(TAG,"cityListRes :"+cityListRes);
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

        if(!appPref.getBoolean(AppPref.UPDATESHOWWORKINGMSG)){
            if(!appPref.getBoolean(AppPref.WORKINGTIMEMSG)){
                showworkingTimeDilaog(cityListRes.getPop_up_title(),cityListRes.getPop_up_message());
            }
        }


        ArrayList<String> citylist = new ArrayList<>();
        for(int i=0;i<cityListRes.getData().size();i++){
            citylist.add(cityListRes.getData().get(i).getCity());
        }
       // citylist = cityListRes.getData();

        if(binding!= null) {
            AppLog.e(TAG, "address: " + address);
            AppLog.e(TAG,"city: "+city);
            AppLog.e(TAG,"pincode: "+pincode);

            boolean isvalidcity = false;
            for(int i =0;i<citylist.size();i++){
                if(city.equalsIgnoreCase(citylist.get(i))){
                    isvalidcity = true;
                    break;
                }
            }

            if(isvalidcity){
                if (binding != null)
                    binding.etPickupaddress.setText("" + address);
            }else {
                if (binding != null){
                    if(!TextUtils.isEmpty(city)){
                        showDialog(cityListRes);
                        // showToast("Sorry we not available in your city");
                        binding.etPickupaddress.setText("");
                    }
                }
            }

        }
    }

    private void showDialog(CityListModel.CityListRes cityListRes) {
         dialog_con = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
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
            }
        });
        dialog_con.show();
    }

    public void dismissdialog(){
        if(dialog_con !=null){
            dialog_con.dismiss();;
        }
    }
}
