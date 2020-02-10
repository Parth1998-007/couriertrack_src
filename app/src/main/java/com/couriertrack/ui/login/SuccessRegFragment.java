package com.couriertrack.ui.login;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.SignUpModel;
import com.couriertrack.databinding.FragmentSuccessRegBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.ui.home.Home;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessRegFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SuccessRegFragment";
    FragmentSuccessRegBinding binding;
    SignUpModel.SignUpReq signUpReq;

    public static SuccessRegFragment newInstance(Bundle bundle) {
        SuccessRegFragment fragment = new SuccessRegFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_success_reg, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_success_reg, container, false);

        init();

        return binding.getRoot();
    }

    private void init() {

        if (getArguments() != null) {
            Type type = new TypeToken<SignUpModel.SignUpReq>() {
            }.getType();
            signUpReq = new Gson().fromJson(getArguments().getString("signupreq"), type);
        }
        binding.btngohome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btngohome: {
                if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
                    gotoActivity(Home.class, null, true);
                else
                    gotoActivity(HomeCourier.class, null, true);

                break;
            }
        }
    }


}
