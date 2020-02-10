package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResendOTPModel {
    public static class SendOTPReq extends BaseReq {
        @SerializedName("user_id")
        @Expose
        public int user_id;

        @SerializedName("order_id")
        @Expose
        public int order_id;

        @SerializedName("for")
        @Expose
        public String for_user;


        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getOrder_id() {
            return order_id;
        }

        public void setOrder_id(int order_id) {
            this.order_id = order_id;
        }

        public String getFor_user() {
            return for_user;
        }

        public void setFor_user(String for_user) {
            this.for_user = for_user;
        }
    }

    public static class SendOTPRes extends BaseRes {
        @SerializedName("otp")
        @Expose
        public String otp;

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }
    }
}
