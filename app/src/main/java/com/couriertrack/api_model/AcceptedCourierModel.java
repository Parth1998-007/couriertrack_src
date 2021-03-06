package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AcceptedCourierModel
{
    public static class OrderAcceptReq extends BaseReq
    {
        @SerializedName("user_id")
        @Expose
        public int user_id;

        @SerializedName("order_id")
        @Expose
        public int order_id;



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

    }

    public static class OrderAcceptRes extends BaseRes
    {

//        @SerializedName("status")
//        @Expose
//        public String status;
//
//        @SerializedName("msg")
//        @Expose
//        public String msg;
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getMsg() {
//            return msg;
//        }
//
//        public void setMsg(String msg) {
//            this.msg = msg;
//        }
    }
}
