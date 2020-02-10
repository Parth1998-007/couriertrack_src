package com.couriertrack.ui.login;

import android.os.Bundle;

import com.couriertrack.R;
import com.couriertrack.ui.Base;


public class BankDetails extends Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setToolbar();
        setTitle("Bank Account Details");
        enableBack(true);

        changeFrag(PaymentDetailFragment.newInstance(null),false,false);
    }
}
