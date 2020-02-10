package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PasswordChangeModel {

    public static class ChangePassword extends BaseReq
    {

        public String getNewPassword()
        {
            return newPassword;
        }

        public void setNewPassword(String newPassword)
        {
            this.newPassword = newPassword;
        }

        @SerializedName("new_password")
        @Expose
        private String newPassword;

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        @SerializedName("user_type")
        @Expose
        private int userType;

        public String getMobile()
        {
            return mobile;
        }

        public void setMobile(String mobile)
        {
            this.mobile = mobile;
        }

        @SerializedName("mobile")
        @Expose
        private String mobile;



    }

}
