package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelOrderModel {
    public static class CancelOrderReq extends BaseReq{
        @SerializedName("order_id")
        @Expose
        private int order_id;
        @SerializedName("user_id")
        @Expose
        public  int user_id;
        @SerializedName("reason")
        @Expose
        private String reason;

        public int getOrder_id() {
            return order_id;
        }

        public void setOrder_id(int order_id) {
            this.order_id = order_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class CancelOrderRes extends BaseRes{

    }
}
