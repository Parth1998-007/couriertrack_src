package com.couriertrack.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.couriertrack.R;
import com.couriertrack.api_model.BaseRes;
import com.couriertrack.network.ApiService;
import com.couriertrack.network.RetroClient;
import com.couriertrack.ui.login.Login;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;

public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    public Context context;

    public AppPref appPref;
    public int onstart_run;
    Dialog dialog;
    protected ApiService apiService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPref = AppPref.getInstance(context);
        apiService = RetroClient.getApiService(getHttpClient());
    }

    public void clearFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public OkHttpClient getHttpClient() {
        return new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                /*Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        Credentials.basic("aUsername", "aPassword"));*/

                Request.Builder builder = originalRequest.newBuilder().header("Authorization", appPref.getString(AppPref.API_KEY));

                Log.e("AUTHORIZATION", "key " + appPref.getString(AppPref.API_KEY));

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public boolean isOnline() {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public void hideKeyboard() {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void gotoActivity(Class className, Bundle bundle, boolean isClearStack) {
        Intent intent = new Intent(context, className);

        if (bundle != null)
            intent.putExtras(bundle);

        if (isClearStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }

    public boolean hasPermission(String[] permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog((Activity) context, resultCode, 1)
                        .show();
            } else {
                Log.e(TAG, "device not supported");
            }
            return false;
        }
        return true;
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void changeFrag(Fragment fragment, boolean isBackStack, boolean isPopBack) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if (isPopBack) {
            fm.popBackStack();
        }
        if (isBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        //fragmentTransaction.replace(R.id.fragment,fragment);
        fragmentTransaction.commit();
    }

    public void logout() {
        appPref.clearData();
        gotoActivity(Login.class, null, true);
        ((Activity) context).finish();
    }

    public void showLoading() {
        if (dialog != null)
            hideLoading();

        if (dialog == null) {
            dialog = new Dialog(context, R.style.AppTheme);
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loading_bar);
        }
        if (!dialog.isShowing())
        {
            AppLog.e(TAG,"Loding Request");
            dialog.show();
        }else
        {
            AppLog.e(TAG,"Loding Not Show");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();

    }

    public void subscribe(Disposable d) {
        AppLog.e(TAG,"subscribe : ");

        compositeDisposable.add(d);
        showLoading();
    }

    protected boolean isSuccess(Response res, BaseRes baseRes) {
        if (res.code() == 200) {
            return true;
        } else if (res.code() == 401) {
            showToast("Login Again");
            logout();
        } else {
            showToast(baseRes != null ? baseRes.getMsg() : getString(R.string.msg_try_again));
        }
        return false;
    }

    public void hideLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isEmpty(EditText editText, int resString) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.requestFocus();
            showToast(getString(resString));
            return true;
        }
        return false;
    }

    public boolean validMobileNumber(EditText editText, int resString) {
        if (TextUtils.isEmpty(editText.getText().toString()))
        {
            editText.requestFocus();
            showToast(getString(resString));
            return true;
        }
        else if(editText.getText().toString().length() < 10)
        {
            editText.requestFocus();
            showToast(getString(resString));
            return true;
        }
        return false;
    }

    public void onFailure(Throwable e) {
        AppLog.e(TAG, "onError() : " + e.getMessage());
        showToast(getString(R.string.msg_try_again));
        hideLoading();
    }

    public void onDone() {
        AppLog.e(TAG, "onComplete()");
        hideLoading();
    }


}
