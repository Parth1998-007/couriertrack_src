package com.couriertrack.ui.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.LoginModel;
import com.couriertrack.api_model.SendOtpModel;
import com.couriertrack.api_model.SignUpModel;
import com.couriertrack.databinding.FragmentVerifyBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class VerifyFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "VerifyFragment";
    FragmentVerifyBinding binding;
    SignUpModel.SignUpReq signUpReq;

    public static VerifyFragment newInstance(Bundle bundle) {
        VerifyFragment fragment = new VerifyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_verify, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify, container, false);

        init();

        return binding.getRoot();
    }

    private void init() {
        if (getArguments() != null) {
            Type type = new TypeToken<SignUpModel.SignUpReq>() {
            }.getType();
            signUpReq = new Gson().fromJson(getArguments().getString("signupreq"), type);
        }

        binding.btnverify.setOnClickListener(this);
        binding.tvSendotp.setOnClickListener(this);
        binding.llOtpview.setAlpha(0.4f);
        binding.etotp.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnverify: {
                if (isValid()) {
                    setsignupdata();
                }
                break;
            }
            case R.id.tv_sendotp:
            {
                if (isValidOTPdata())
                {
                    SendOtpModel.SendOtpreq sendOtpreq = new SendOtpModel.SendOtpreq();
                    sendOtpreq.setPhone(binding.etmobilenum.getText().toString());
                    sendOtpreq.setOtpFor(AppPref.OTPFOR_SIGNUP);
                    sendOtpreq.setUserType(""+signUpReq.getUser_type());
                    sendOTP(sendOtpreq);
                }
                break;
            }
        }
    }

    private void sendOTP(SendOtpModel.SendOtpreq sendOtpreq) {

        AppLog.e(TAG, "sendOtpreq : " + sendOtpreq);
        showLoading();
        apiService.sendOtpreq(sendOtpreq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<SendOtpModel.SendOtpRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<SendOtpModel.SendOtpRes> sendOtpreqRes) {
                        AppLog.e(TAG, "sendOTPRes :" + sendOtpreqRes.body());
                        if (isSuccess(sendOtpreqRes, sendOtpreqRes.body())) {
                            OnsendOtpRes(sendOtpreqRes.body());
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

    private void OnsendOtpRes(SendOtpModel.SendOtpRes sendOtpRes) {
        if (sendOtpRes.isStatus()) {
            //showToast(sendOtpRes.getMsg());
            appPref.set(AppPref.OTP, sendOtpRes.getOTP());
            binding.llOtpview.setAlpha(1.0f);
            binding.etotp.setEnabled(true);
        }
        else
        {
            showToast(sendOtpRes.getMsg());
        }
    }

    private boolean isValidOTPdata() {
        if (isEmpty(binding.etmobilenum, R.string.hint_mobile))
        {
            return false;
        }
        else if (binding.etmobilenum.getText().toString().length() != 10)
        {
            showToast("Enter valid mobile");
            return false;
        }
        return true;
    }

    private void setsignupdata() {
        if (signUpReq != null) {
            signUpReq.setMobile(binding.etmobilenum.getText().toString());
            Bundle b = new Bundle();
            b.putString("signupreq", new Gson().toJson(signUpReq));

            ((SignUp) getActivity()).changeFrag(IDFragment.newInstance(b), true, false);

        }
    }

    private boolean isValid() {
        if (isEmpty(binding.etmobilenum, R.string.hint_mobile)) {
            return false;
        } else if (binding.etmobilenum.getText().toString().length() != 10) {
            showToast("Enter valid mobile");
            return false;
        }
        if (isEmpty(binding.etotp, R.string.hint_recieved_otp)) {
            return false;
        } else if (!binding.etotp.getText().toString().equals(appPref.getString(AppPref.OTP))) {
            showToast("Please Enter Correct OTP");
            return false;
        }
        return true;
    }

}
