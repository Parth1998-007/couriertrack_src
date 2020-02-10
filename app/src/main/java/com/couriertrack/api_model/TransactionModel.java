package com.couriertrack.api_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TransactionModel {
    public static class TransactionReq extends BaseReq{
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        @SerializedName("user_id")
        @Expose
        private String userId;


    }

    public static class TransactionRes extends BaseRes{
        @SerializedName("transactions")
        @Expose
        private ArrayList<Transactions> transactionslist;
        @SerializedName("Current_Week_Earning")
        @Expose
        private String wallet;

        public ArrayList<Transactions> getTransactionslist() {
            return transactionslist;
        }

        public void setTransactionslist(ArrayList<Transactions> transactionslist) {
            this.transactionslist = transactionslist;
        }

        public String getWallet() {
            return wallet;
        }

        public void setWallet(String wallet) {
            this.wallet = wallet;
        }
    }

    public static class Transactions{
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("order_id")
        @Expose
        private String transaction_id;
        @SerializedName("created_date")
        @Expose
        private String created_date;
        @SerializedName("details")
        @Expose
        private String text;
        @SerializedName("type")
        @Expose
        private String type;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }

        public String getCreated_date() {
            return created_date;
        }

        public void setCreated_date(String created_date) {
            this.created_date = created_date;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
