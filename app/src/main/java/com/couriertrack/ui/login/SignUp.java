package com.couriertrack.ui.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.couriertrack.R;
import com.couriertrack.ui.Base;
import com.couriertrack.utils.AppPref;

public class SignUp extends Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setToolbar();

        if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
            setTitle("REGISTER USER");
        else
            setTitle("REGISTER COURIER");
        changeFrag(SignUpFragment.newInstance(null), false, false);
    }


}
