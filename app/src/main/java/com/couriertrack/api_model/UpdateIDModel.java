package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateIDModel {
    public static class ProfileIDReq extends BaseReq {

        @SerializedName("front_img")
        @Expose
        private String front_img;
        @SerializedName("profile")
        @Expose
        private String profile_pic;

        @SerializedName("back_img")
        @Expose
        private String back_img;
        @SerializedName("doc_type")
        @Expose
        private String doc_type;
        @SerializedName("doc_number")
        @Expose
        private String doc_number;

        @SerializedName("user_type")
        @Expose
        private int user_type;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        @SerializedName("user_id")
        @Expose
        public int user_id;

        public int getUser_type() {
            return user_type;
        }

        public void setUser_type(int user_type) {
            this.user_type = user_type;
        }


        public String getFront_img() {
            return front_img;
        }

        public void setFront_img(String front_img) {
            this.front_img = front_img;
        }

        public String getBack_img() {
            return back_img;
        }

        public void setBack_img(String back_img) {
            this.back_img = back_img;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(String doc_type) {
            this.doc_type = doc_type;
        }

        public String getDoc_number() {
            return doc_number;
        }

        public void setDoc_number(String doc_number) {
            this.doc_number = doc_number;
        }
    }

    public static class UpdateProfileIDRes extends BaseRes {
        @SerializedName("user_id")
        @Expose
        private String user_id;
        @SerializedName("first_name")
        @Expose
        private String first_name;
        @SerializedName("last_name")
        @Expose
        private String last_name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("gender")
        @Expose
        private int gender;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("front_img")
        @Expose
        private String front_img;
        @SerializedName("back_img")
        @Expose
        private String back_img;
        @SerializedName("doc_type")
        @Expose
        private String doc_type;
        @SerializedName("doc_number")
        @Expose
        private String doc_number;
        @SerializedName("user_status")
        @Expose
        private String user_status;
        @SerializedName("api_key")
        @Expose
        private String api_key;


        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getFront_img() {
            return front_img;
        }

        public void setFront_img(String front_img) {
            this.front_img = front_img;
        }

        public String getBack_img() {
            return back_img;
        }

        public void setBack_img(String back_img) {
            this.back_img = back_img;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(String doc_type) {
            this.doc_type = doc_type;
        }

        public String getDoc_number() {
            return doc_number;
        }

        public void setDoc_number(String doc_number) {
            this.doc_number = doc_number;
        }

        public String getUser_status() {
            return user_status;
        }

        public void setUser_status(String user_status) {
            this.user_status = user_status;
        }

        public String getApi_key() {
            return api_key;
        }

        public void setApi_key(String api_key) {
            this.api_key = api_key;
        }
    }
}
