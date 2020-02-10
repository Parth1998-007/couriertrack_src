package com.couriertrack.ui.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.BaseRes;
import com.couriertrack.api_model.PasswordChangeModel;
import com.couriertrack.api_model.SendOtpModel;
import com.couriertrack.databinding.FragmentForgotpassBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class ForgotPasswordFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ForgotPassFragment";
    FragmentForgotpassBinding binding;

    public static ForgotPasswordFragment newInstance(Bundle bundle)
    {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_verify, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgotpass, container, false);

        init();

        return binding.getRoot();
    }

    private void init()
    {
        binding.btnverify.setOnClickListener(this);
        binding.tvSendotp.setOnClickListener(this);

        binding.llOtpview.setAlpha(0.4f);
        binding.llEnterotp.setAlpha(0.4f);

        binding.etotp.setEnabled(false);
        binding.etPassword.setEnabled(false);
        binding.etRePassword.setEnabled(false);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnverify:
            {
                if (isValid())
                {
                    setPasswordChangeData();
                }
                break;
            }
            case R.id.tv_sendotp:
            {
                if (isValidOTPdata())
                {
                    SendOtpModel.SendOtpreq sendOtpreq = new SendOtpModel.SendOtpreq();
                    sendOtpreq.setPhone(binding.etmobilenum.getText().toString());
                    if(appPref.getString(AppPref.USER_TYPE).equalsIgnoreCase("customer"))
                    sendOtpreq.setUserType(""+1);
                    else
                    {
                        sendOtpreq.setUserType(""+2);
                    }
                    sendOtpreq.setOtpFor(AppPref.OTPFOR_FORGOTPASS);
                    sendOTP(sendOtpreq);
                }
                break;
            }
        }
    }

    private void sendOTP(SendOtpModel.SendOtpreq sendOtpreq)
    {

        AppLog.e(TAG, "sendOtpreq : " + sendOtpreq);
        showLoading();
        apiService.sendOtpreq(sendOtpreq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<SendOtpModel.SendOtpRes>>()
                {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<SendOtpModel.SendOtpRes> sendOtpreqRes)
                    {
                        AppLog.e(TAG, "sendOTPRes :" + sendOtpreqRes.body());
                        if (isSuccess(sendOtpreqRes, sendOtpreqRes.body()))
                        {
                            OnsendOtpRes(sendOtpreqRes.body());
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        onFailure(e);
                    }

                    @Override
                    public void onComplete()
                    {
                        onDone();
                    }
                });


    }

    private void OnsendOtpRes(SendOtpModel.SendOtpRes sendOtpRes)
    {
        if (sendOtpRes.isStatus())
        {
            showToast(sendOtpRes.getMsg());
            appPref.set(AppPref.OTP, sendOtpRes.getOTP());

            binding.llEnterotp.setAlpha(1.0f);
            binding.llOtpview.setAlpha(1.0f);

            binding.etotp.setEnabled(true);
            binding.etPassword.setEnabled(true);
            binding.etRePassword.setEnabled(true);

        }
    }

    private boolean isValidOTPdata()
    {
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

    private void setPasswordChangeData()
    {

        if((appPref.getString(AppPref.OTP)).equalsIgnoreCase(binding.etotp.getText().toString()) )
        {

            if(!binding.etPassword.getText().toString().equalsIgnoreCase(""))
            if(binding.etPassword.getText().toString().equalsIgnoreCase(binding.etRePassword.getText().toString()))
            {

                PasswordChangeModel.ChangePassword passwordChangeModelReq = new PasswordChangeModel.ChangePassword();
                passwordChangeModelReq.setMobile(binding.etmobilenum.getText().toString());
                passwordChangeModelReq.setNewPassword(binding.etPassword.getText().toString());
                if (appPref.getString(AppPref.USER_TYPE).equalsIgnoreCase("customer"))
                    passwordChangeModelReq.setUserType(1);
                else
                    passwordChangeModelReq.setUserType(2);

                callPasswordChangeAPI(passwordChangeModelReq);
            }
            else
            {
                showToast("Confirm Password Not Matches , Please Correct It.");
            }
            else
            {
                showToast("Please Enter Password To Continue");
            }
        }
        else
        {
            showToast("Please Enter Correct OTP And  Try Again");
        }

    }

    private void callPasswordChangeAPI(PasswordChangeModel.ChangePassword passwordChangeModel)
    {
        AppLog.e(TAG, "callPasswordChangeAPI : " + passwordChangeModel);
        showLoading();
        apiService.changePassword(passwordChangeModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<BaseRes>>()
                {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<BaseRes> baseRes)
                    {
                        AppLog.e(TAG, "PasswordChange :" + baseRes.body());
                        if (isSuccess(baseRes, baseRes.body()))
                        {
                            //Password Changed SuccuessFully
                            showToast("Password Changed Successfully , Login With New Password");
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        onFailure(e);
                        showToast(e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete()
                    {
                        onDone();
                    }
                });

    }

    private boolean isValid()
    {
        if (isEmpty(binding.etmobilenum, R.string.hint_mobile))
        {
            return false;
        }
        else if (binding.etmobilenum.getText().toString().length() != 10)
        {
            showToast("Enter valid mobile");
            return false;
        }

        if (isEmpty(binding.etotp, R.string.hint_recieved_otp))
        {
            return false;
        }
        else if (!binding.etotp.getText().toString().equals(appPref.getString(AppPref.OTP)))
        {
            showToast("Please Enter Correct OTP");
            return false;
        }
        return true;
    }

}
