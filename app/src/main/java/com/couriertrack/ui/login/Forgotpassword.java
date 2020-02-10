package com.couriertrack.ui.login;

import android.os.Bundle;

import com.couriertrack.R;
import com.couriertrack.ui.Base;
import com.couriertrack.utils.AppPref;

public class Forgotpassword extends Base
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setToolbar();

        setTitle("Forgot Password");
        enableBack(true);
        changeFrag(ForgotPasswordFragment.newInstance(null), false, false);
    }

}
