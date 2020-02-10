package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPricingModel {

    public static class GetPricingReq extends BaseReq{
        @SerializedName("user_id")
        @Expose
        private String userID;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @SerializedName("type")
        @Expose
        private String type;

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        @SerializedName("weight")
        @Expose
        private String weight;

        @SerializedName("distance")
        @Expose
        private String distance;

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userid) {
            this.userID = userid;
        }

    }

    public static class GetPricingResponse extends BaseRes{
        @SerializedName("data")
        @Expose
        private Pricing pricing;

        public Pricing getPricing()
        {
            return pricing;
        }

        public void setPricing(Pricing pricing) {
            this.pricing = pricing;
        }
    }

    public static class Pricing
    {
        public float getBase_price() {
            return base_price;
        }

        public void setBase_price(float base_price)
        {
            this.base_price = base_price;
        }

        public float getPrice_per_km()
        {
            return price_per_km;
        }

        public void setPrice_per_km(float price_per_km)
        {
            this.price_per_km = price_per_km;
        }

        @SerializedName("base_price")
        @Expose
        private float base_price;

        public float getPayable_price() {
            return payable_price;
        }

        public void setPayable_price(float payable_price) {
            this.payable_price = payable_price;
        }

        @SerializedName("payable_amount")
        @Expose
        private float payable_price;

        public float getFirst_fix_km() {
            return first_fix_km;
        }

        public void setFirst_fix_km(float first_fix_km) {
            this.first_fix_km = first_fix_km;
        }

        public float getFirst_fix_rate() {
            return first_fix_rate;
        }

        public void setFirst_fix_rate(float first_fix_rate) {
            this.first_fix_rate = first_fix_rate;
        }

        @SerializedName("first_fix_km")
        @Expose
        private float first_fix_km;

        @SerializedName("first_fix_rate")
        @Expose
        private float first_fix_rate;

        @SerializedName("price_per_km")
        @Expose
        private float price_per_km;

    }
}
