package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PricingModel {

    public static class PricingRes extends BaseRes{
        @SerializedName("data")
        @Expose
        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

    }

    public static class Data{

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("base_price")
        @Expose
        private String basicfare;
        @SerializedName("price_per_km")
        @Expose
        private String distancefee;
        @SerializedName("min_price")
        @Expose
        private String minimumfee;
        @SerializedName("note")
        @Expose
        private String note;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBasicfare() {
            return basicfare;
        }

        public void setBasicfare(String basicfare) {
            this.basicfare = basicfare;
        }

        public String getDistancefee() {
            return distancefee;
        }

        public void setDistancefee(String distancefee) {
            this.distancefee = distancefee;
        }

        public String getMinimumfee() {
            return minimumfee;
        }

        public void setMinimumfee(String minimumfee) {
            this.minimumfee = minimumfee;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}
