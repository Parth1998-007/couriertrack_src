package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendOtpModel {

    public static class SendOtpreq extends BaseReq{
        @SerializedName("phone")
        @Expose
        private String phone;

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getOtpFor() {
            return otpFor;
        }

        public void setOtpFor(String otpFor) {
            this.otpFor = otpFor;
        }

        @SerializedName("user_type")
        @Expose
        private String userType;

        @SerializedName("for")
        @Expose
        private String otpFor;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

    }

    public static class SendOtpRes extends BaseRes{
        @SerializedName("otp")
        @Expose
        private String OTP;

        public String getOTP() {
            return OTP;
        }

        public void setOTP(String OTP) {
            this.OTP = OTP;
        }
    }
}
