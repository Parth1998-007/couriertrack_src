package com.couriertrack.network;

import android.content.Context;
import android.util.Log;

import com.couriertrack.api_model.BaseRes;
import com.couriertrack.api_model.LoginModel;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RetroManager {

    private static final int API_LOGIN = 1;
    private OnAPIListener mListener;

    private Context context;

    public RetroManager(Context context) {
        this.context = context;
    }


    public void login(OnAPIListener listener, LoginModel.LoginReq request) {

        mListener = listener;

        ApiService api = RetroClient.getApiService(getHttpClient());

        Call<LoginModel.LoginRes> call = api.login2(request);

        AppLog.e("URL data: ", "Url: " + call.request().url().toString());
        AppLog.e("URL data: ", "Data: " + request.toString());

        call.enqueue(new Callback<LoginModel.LoginRes>() {

            @Override
            public void onResponse(Call<LoginModel.LoginRes> call, Response<LoginModel.LoginRes> response) {

                if (response.body() != null) {
                    mListener.onResponse(API_LOGIN, response.body(), response.code());
                } else {
                    mListener.onResponseNull(API_LOGIN, response.code());
                }
            }
            @Override
            public void onFailure(Call<LoginModel.LoginRes> call, Throwable t) {
                t.printStackTrace();
                mListener.onFailure(API_LOGIN, t.toString());
            }
        });

    }


    public interface OnAPIListener {
        void onResponse(int apiId, BaseRes response, int statusCode);

        void onResponseNull(int apiId, int statusCode);

        void onFailure(int apiId, String msg);

        void onForceLogout(String msg);
    }

    private OkHttpClient getHttpClient() {
        return new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                /*Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        Credentials.basic("aUsername", "aPassword"));*/

                Request.Builder builder = originalRequest.newBuilder().header("Authorization", AppPref.getInstance(context).getString(AppPref.API_KEY));

                Log.e("AUTHORIZATION", "key is " + AppPref.getInstance(context).getString(AppPref.API_KEY));

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();
    }

}
