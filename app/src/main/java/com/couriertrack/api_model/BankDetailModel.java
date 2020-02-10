package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetailModel {
    public static class BankDetailReq extends  BaseReq{
        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        @SerializedName("bank_name")
        @Expose
        private String bank_name;

        @SerializedName("account_num")
        @Expose
        private String account_num;

        public String getAccount_name() {
            return account_name;
        }

        public void setAccount_name(String account_name) {
            this.account_name = account_name;
        }

        @SerializedName("account_name")
        @Expose
        private String account_name;

        @SerializedName("ifsc_code")
        @Expose
        private String ifsc_code;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        @SerializedName("user_id")
        @Expose
        private String user_id;

        public String getAccount_num() {
            return account_num;
        }

        public void setAccount_num(String account_num) {
            this.account_num = account_num;
        }

        public String getIfsc_code() {
            return ifsc_code;
        }

        public void setIfsc_code(String ifsc_code) {
            this.ifsc_code = ifsc_code;
        }
    }

    public static class BankDetailRes extends  BaseRes
    {
        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String account_name) {
            this.bank_name = account_name;
        }

        public String getAccount_num() {
            return account_num;
        }

        public void setAccount_num(String account_num) {
            this.account_num = account_num;
        }

        public String getIfsc_code() {
            return ifsc_code;
        }

        public void setIfsc_code(String ifsc_code) {
            this.ifsc_code = ifsc_code;
        }

        @SerializedName("bank_name")
        @Expose
        private String bank_name;
        @SerializedName("account_num")
        @Expose
        private String account_num;

        public String getAccount_name() {
            return account_name;
        }

        public void setAccount_name(String account_name) {
            this.account_name = account_name;
        }

        @SerializedName("account_name")
        @Expose
        private String account_name;

        @SerializedName("ifsc_code")
        @Expose
        private String ifsc_code;
    }
}
