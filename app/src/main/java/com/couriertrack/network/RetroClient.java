package com.couriertrack.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.couriertrack.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance(OkHttpClient okHttpClient) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    /**
     * Get API notification
     *
     * @return API notification
     */
    public static ApiService getApiService(OkHttpClient okHttpClient) {
        return getRetrofitInstance(okHttpClient).create(ApiService.class);
    }

}
