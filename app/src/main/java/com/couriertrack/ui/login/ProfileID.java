package com.couriertrack.ui.login;

import android.os.Bundle;

import com.couriertrack.R;
import com.couriertrack.ui.Base;


public class ProfileID extends Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setToolbar();
        setTitle("Update IDs");
        enableBack(true);

        changeFrag(profileIDFragment.newInstance(null),false,false);
    }
}
