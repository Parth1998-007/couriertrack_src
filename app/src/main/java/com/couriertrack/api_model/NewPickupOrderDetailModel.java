package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewPickupOrderDetailModel {
    public static class OrderDetailReq extends BaseReq{

    }

    public static class NewPickupOrderDetailRes extends BaseRes{

        @SerializedName("order_id")
        @Expose
        private int order_id;
        @SerializedName("unique_id")
        @Expose
        private String unique_id;
        @SerializedName("user")
        @Expose
        private User user;
        @SerializedName("sender_name")
        @Expose
        private String sender_name;
        @SerializedName("sender_mobile")
        @Expose
        private String sender_mobile;
        @SerializedName("sender_lat")
        @Expose
        private String sender_lat;
        @SerializedName("sender_lng")
        @Expose
        private String sender_lng;
        @SerializedName("drop_address")
        @Expose
        private String drop_address;
        @SerializedName("receiver_name")
        @Expose
        private String receiver_name;
        @SerializedName("receiver_lat")
        @Expose
        private String receiver_lat;
        @SerializedName("receiver_lng")
        @Expose
        private String receiver_lng;
        @SerializedName("receiver_mobile")
        @Expose
        private String receiver_mobile;
        @SerializedName("pickup_address")
        @Expose
        private String pickup_address;
        @SerializedName("weight")
        @Expose
        private String weight;
        @SerializedName("detail")
        @Expose
        private String detail;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("cost")
        @Expose
        private String cost;
        @SerializedName("order_status")
        @Expose
        private String order_status;
        @SerializedName("order_datetime")
        @Expose
        private String order_datetime;
        @SerializedName("estimated_time")
        @Expose
        private String estimated_time;
        @SerializedName("estimated_distance")
        @Expose
        private String estimated_distance;

        public int getOrder_id() {
            return order_id;
        }

        public void setOrder_id(int order_id) {
            this.order_id = order_id;
        }

        public String getUnique_id() {
            return unique_id;
        }

        public void setUnique_id(String unique_id) {
            this.unique_id = unique_id;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getSender_name() {
            return sender_name;
        }

        public void setSender_name(String sender_name) {
            this.sender_name = sender_name;
        }

        public String getSender_mobile() {
            return sender_mobile;
        }

        public void setSender_mobile(String sender_mobile) {
            this.sender_mobile = sender_mobile;
        }

        public String getSender_lat() {
            return sender_lat;
        }

        public void setSender_lat(String sender_lat) {
            this.sender_lat = sender_lat;
        }

        public String getSender_lng() {
            return sender_lng;
        }

        public void setSender_lng(String sender_lng) {
            this.sender_lng = sender_lng;
        }

        public String getDrop_address() {
            return drop_address;
        }

        public void setDrop_address(String drop_address) {
            this.drop_address = drop_address;
        }

        public String getReceiver_name() {
            return receiver_name;
        }

        public void setReceiver_name(String receiver_name) {
            this.receiver_name = receiver_name;
        }

        public String getReceiver_lat() {
            return receiver_lat;
        }

        public void setReceiver_lat(String receiver_lat) {
            this.receiver_lat = receiver_lat;
        }

        public String getReceiver_lng() {
            return receiver_lng;
        }

        public void setReceiver_lng(String receiver_lng) {
            this.receiver_lng = receiver_lng;
        }

        public String getReceiver_mobile() {
            return receiver_mobile;
        }

        public void setReceiver_mobile(String receiver_mobile) {
            this.receiver_mobile = receiver_mobile;
        }

        public String getPickup_address() {
            return pickup_address;
        }

        public void setPickup_address(String pickup_address) {
            this.pickup_address = pickup_address;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public String getOrder_status() {
            return order_status;
        }

        public void setOrder_status(String order_status) {
            this.order_status = order_status;
        }

        public String getOrder_datetime() {
            return order_datetime;
        }

        public void setOrder_datetime(String order_datetime) {
            this.order_datetime = order_datetime;
        }

        public String getEstimated_time() {
            return estimated_time;
        }

        public void setEstimated_time(String estimated_time) {
            this.estimated_time = estimated_time;
        }

        public String getEstimated_distance() {
            return estimated_distance;
        }

        public void setEstimated_distance(String estimated_distance) {
            this.estimated_distance = estimated_distance;
        }
    }

    private static class User {
        @SerializedName("user_id")
        @Expose
        private int user_id;
        @SerializedName("user_name")
        @Expose
        private String user_name;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }
}
