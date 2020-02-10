package com.couriertrack.api_model;

import com.google.gson.Gson;


public class BaseReq {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
