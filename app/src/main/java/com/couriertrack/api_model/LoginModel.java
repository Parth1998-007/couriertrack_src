package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LoginModel {
    public static class LoginReq extends BaseReq {
        @SerializedName("device_type")
        @Expose
        private String device_type;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("login_type")
        @Expose
        private int login_type;
        @SerializedName("user_type")
        @Expose
        private int user_type;
        @SerializedName("token")
        @Expose
        private String token;

        public int getUser_type() {
            return user_type;
        }

        public void setUser_type(int user_type) {
            this.user_type = user_type;
        }

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getLogin_type() {
            return login_type;
        }

        public void setLogin_type(int login_type) {
            this.login_type = login_type;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class LoginRes extends BaseRes {
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

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        @SerializedName("profile_pic")
        @Expose
        private String profile_image;


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

    public static class Profile{
        @SerializedName("pk_customer_id")
        @Expose
        private int pk_customer_id;
        @SerializedName("first_name")
        @Expose
        private String first_name;
        @SerializedName("last_name")
        @Expose
        private String last_name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("gender")
        @Expose
        private int gender;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("token")
        @Expose
        private String token;
        @SerializedName("device_type")
        @Expose
        private String device_type;
        @SerializedName("api_key")
        @Expose
        private String api_key;
        @SerializedName("created_at")
        @Expose
        private String created_at;
        @SerializedName("updated_at")
        @Expose
        private String updated_at;
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
        @SerializedName("otp")
        @Expose
        private String otp;

        public int getPk_customer_id() {
            return pk_customer_id;
        }

        public void setPk_customer_id(int pk_customer_id) {
            this.pk_customer_id = pk_customer_id;
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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public String getApi_key() {
            return api_key;
        }

        public void setApi_key(String api_key) {
            this.api_key = api_key;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
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

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }
    }

}
