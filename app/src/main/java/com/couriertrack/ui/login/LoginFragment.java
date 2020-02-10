package com.couriertrack.ui.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.CreateOrderModel;
import com.couriertrack.api_model.LoginModel;
import com.couriertrack.databinding.FragmentLoginBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.ui.home.Home;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = LoginFragment.class.getSimpleName();
    FragmentLoginBinding binding;
    CompositeDisposable disposable = new CompositeDisposable();
    public String user_type_ ;

    public static LoginFragment newInstance(Bundle bundle) {
        LoginFragment fragment = new LoginFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        init();

        return binding.getRoot();
    }

    private void init() {
        user_type_ = "customer";

        binding.btnLogin.setOnClickListener(this);
        binding.tvSignup.setOnClickListener(this);
        binding.tvCustomer.setOnClickListener(this);
        binding.tvCourier.setOnClickListener(this);
        binding.tvForgotpassword.setOnClickListener(this);
        appPref.set(AppPref.USER_TYPE, user_type_);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: {

                if (isValid())
                {
                    LoginModel.LoginReq loginReq = new LoginModel.LoginReq();
                    loginReq.setPhone(binding.etPhone.getText().toString());
                    loginReq.setPassword(binding.etPassword.getText().toString());
                    loginReq.setDevice_type("android");
                    loginReq.setLogin_type(1);
                    if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
                        loginReq.setUser_type(1);
                    else
                        loginReq.setUser_type(2);
                    AppLog.e(TAG, "FCM TOKEN :" + appPref.getString(AppPref.FCM_TOKEN));
                    loginReq.setToken(appPref.getString(AppPref.FCM_TOKEN));
                    login(loginReq);

                }
                // onLoginSuccess();
                break;
            }
            case R.id.tv_signup:
                {
                gotoActivity(SignUp.class, null, false);
//                getActivity().finish();
                break;
                }

            case R.id.tv_forgotpassword:
                {
                startActivity(new Intent(getActivity() , Forgotpassword.class ));
                }
                break;
            case R.id.tv_customer:
                {
                user_type_ = "customer";
                appPref.set(AppPref.USER_TYPE, user_type_);
                binding.tvCustomer.setTextColor(getResources().getColor(R.color.white));
                binding.tvCourier.setTextColor(getResources().getColor(R.color.colorTextLight));
                binding.tvSignup.setText(getResources().getString(R.string.signup_customer));
                break;
                }
            case R.id.tv_courier:
                {
                user_type_ = "courier";
                appPref.set(AppPref.USER_TYPE, user_type_);
                binding.tvCustomer.setTextColor(getResources().getColor(R.color.colorTextLight));
                binding.tvCourier.setTextColor(getResources().getColor(R.color.white));
                binding.tvSignup.setText(getResources().getString(R.string.signup_delivery));
                break;
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    // using retrofit
    public void login(final LoginModel.LoginReq loginReq) {
        AppLog.e(TAG, "login : " + loginReq);
        showLoading();
        apiService.login(loginReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<LoginModel.LoginRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<LoginModel.LoginRes> loginRes) {
                        AppLog.e(TAG, "LoginRes :" + loginRes);
                        if (isSuccess(loginRes, loginRes.body())) {
                            onLoginSuccess(loginRes.body());
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

        // OR
        /*RetroManager retroManager=new RetroManager(context);
        retroManager.login(this,loginReq);*/

    }

    private boolean isValid() {
        if (isEmpty(binding.etPhone, R.string.hint_phone)) {
            return false;
        } else if (isEmpty(binding.etPassword, R.string.hint_pass)) {
            return false;
        } else if (binding.etPhone.getText().toString().length() != 10) {
            showToast("Enter valid mobile");
            return false;
        }
        return true;
    }

    public void onLoginSuccess(LoginModel.LoginRes loginRes) {

        if (loginRes.isStatus()) {
            appPref.set(AppPref.USER_ID, loginRes.getUser_id());
            appPref.set(AppPref.API_KEY, loginRes.getApi_key());
            AppLog.e(TAG, "key" + appPref.getString(AppPref.API_KEY));
            appPref.set(AppPref.NAME, loginRes.getFirst_name());
            appPref.set(AppPref.EMAIL, loginRes.getEmail());
            appPref.set(AppPref.GENDER, loginRes.getGender());
            appPref.set(AppPref.MOBILE, loginRes.getMobile());
            appPref.set(AppPref.DOCTYPE, loginRes.getDoc_type());
            appPref.set(AppPref.DOCNUMBER, loginRes.getDoc_number());
            appPref.set(AppPref.FRONTIMAGE, loginRes.getFront_img());
            appPref.set(AppPref.BACKIMAGE, loginRes.getBack_img());
            appPref.set(AppPref.USERSTATUS, loginRes.getUser_status());
            appPref.set(AppPref.IS_LOGIN, true);
            if (loginRes.getUser_status().equalsIgnoreCase("not_verified"))
            {
                if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
                    gotoActivity(SuccessRegActivity.class, null, true);
                else
                    gotoActivity(HomeCourier.class, null, true);
            } else {

                if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
                    gotoActivity(Home.class, null, true);
                else
                    gotoActivity(HomeCourier.class, null, true);
            }


        }

    }

}
