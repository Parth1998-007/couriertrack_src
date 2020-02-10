package com.couriertrack.ui.login;

import android.os.Bundle;

import com.couriertrack.R;
import com.couriertrack.ui.Base;


public class Login extends Base {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        changeFrag(LoginFragment.newInstance(null),false,false);
    }

    public void openForgotpassword()
    {
        changeFrag(ForgotPasswordFragment.newInstance(null),true,false);
    }

    public void backToLogin()
    {
        changeFrag(LoginFragment.newInstance(null), false , true);
    }
}
