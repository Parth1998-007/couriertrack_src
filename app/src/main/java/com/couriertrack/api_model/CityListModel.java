package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CityListModel {

    public static class CityListRes extends BaseRes{
        @SerializedName("data")
        @Expose
        private ArrayList<Data> data;
        @SerializedName("pop_up_title")
        @Expose
        private String pop_up_title;
        @SerializedName("pop_up_message")
        @Expose
        private String pop_up_message;
        @SerializedName("city_not_available_text")
        @Expose
        private String city_not_available;
        @SerializedName("city_not_available_title")
        @Expose
        private String city_not_available_title;

        public ArrayList<Data> getData() {
            return data;
        }

        public void setData(ArrayList<Data> data) {
            this.data = data;
        }

        public String getPop_up_title() {
            return pop_up_title;
        }

        public void setPop_up_title(String pop_up_title) {
            this.pop_up_title = pop_up_title;
        }

        public String getPop_up_message() {
            return pop_up_message;
        }

        public void setPop_up_message(String pop_up_message) {
            this.pop_up_message = pop_up_message;
        }

        public String getCity_not_available() {
            return city_not_available;
        }

        public void setCity_not_available(String city_not_available) {
            this.city_not_available = city_not_available;
        }

        public String getCity_not_available_title() {
            return city_not_available_title;
        }

        public void setCity_not_available_title(String city_not_available_title) {
            this.city_not_available_title = city_not_available_title;
        }
    }

    public static class Data{
        @SerializedName("city")
        @Expose
        private String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}
