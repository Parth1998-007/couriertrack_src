package com.couriertrack.api_model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckVerifyModel {
    public static class checkVerifiedUserReq extends BaseReq
    {
        @SerializedName("user")
        @Expose
        public static int user;

        @SerializedName("user_id")
        @Expose
        public static int user_id;

        public static int getUser() {
            return user;
        }

        public static void setUser(int user) {
            checkVerifiedUserReq.user = user;
        }

        public static int getUser_id() {
            return user_id;
        }

        public static void setUser_id(int user_id) {
            checkVerifiedUserReq.user_id = user_id;
        }
    }

    public static class checkVerifiedUserRes
    {
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("msg")
        @Expose
        private String msg;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
