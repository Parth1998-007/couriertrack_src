package com.couriertrack.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.couriertrack.R;
import com.couriertrack.api_model.BaseRes;
import com.couriertrack.network.ApiService;
import com.couriertrack.network.RetroClient;
import com.couriertrack.ui.login.Login;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;


public class Base extends AppCompatActivity {

    private static final String TAG = "Base";
    public Toolbar toolbar;

    protected AppPref appPref;
    public static String activity_name;
    Dialog dialog;
    public ApiService apiService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPref=AppPref.getInstance(this);
        apiService = RetroClient.getApiService(getHttpClient());

    }

    public OkHttpClient getHttpClient() {
        return new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                /*Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        Credentials.basic("aUsername", "aPassword"));*/

                Request.Builder builder = originalRequest.newBuilder().header("Authorization", appPref.getString(AppPref.API_KEY));

                Log.e("AUTHORIZATION", "key is " + appPref.getString(AppPref.API_KEY));

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    public void subscribe(Disposable d) {
        compositeDisposable.add(d);
        showLoading();
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

    public void subscribe_service(Disposable d) {
        compositeDisposable.add(d);
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


    public void setToolbar()
    {
        toolbar=findViewById(R.id.toolBar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
        }
    }

    public void setToolbar(String title)
    {
        toolbar=findViewById(R.id.toolBar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(title);
        }
    }
    public void enableBack(boolean isBack)
    {
        if(toolbar!=null)
        {
            if(isBack)
            {
                toolbar.setNavigationIcon(R.drawable.back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
            else
            {
                toolbar.setNavigationIcon(null);
            }

        }
    }
    public void setTitle(String title)
    {
        TextView textView=findViewById(R.id.tvTitle);
        if(textView!=null)
            textView.setText(title);
    }

    public boolean isOnline()
    {
        try
        {
            ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                return cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }
    public  boolean hasPermission( String[] permissions) {

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && permissions!=null)
        {
            for(String permission:permissions)
            {
                if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }
    public void gotoActivity(Class className, Bundle bundle, boolean isClearStack)
    {
        Intent intent=new Intent(this,className);

        if(bundle!=null)
            intent.putExtras(bundle);

        if(isClearStack)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }
    public void gotoActivity(@NonNull Intent intent, boolean isClearStack)
    {
        if(isClearStack)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }

    public void showToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public void clearFragment(){
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    public void changeFrag(Fragment fragment, boolean isBackStack, boolean isPopBack)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if(isPopBack)
        {
            fm.popBackStack();
        }
        if(isBackStack)
        {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(R.id.fragment,fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void getFrag(Fragment fragment, boolean isBackStack, boolean isPopBack)
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment getfragment = fm.findFragmentById(R.id.fragment);


       /* if(isPopBack)
        {
            fm.popBackStack();

        }
        if(isBackStack)
        {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(R.id.fragment,fragment);
        fragmentTransaction.commit();*/
    }
    public void changeFrag(Fragment fragment, boolean isBackStack, boolean isPopBack, int resourceId)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if(isPopBack)
        {
            fm.popBackStack();

        }
        if(isBackStack)
        {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(resourceId,fragment);
        fragmentTransaction.commit();
    }
    public void logout()
    {
        appPref.clearData();
        gotoActivity(Login.class,null,true);
        finish();
    }

    public  void showLoading()
    {
        if(dialog!=null)
            hideLoading();

        if(dialog==null)
        {
            dialog=new Dialog(this);
            if(dialog.getWindow()!=null)
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loading_bar);
        }
        if(!dialog.isShowing())
            dialog.show();
    }
    public  void hideLoading()
    {
        if(dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }
}
