package com.couriertrack.ui.myorder;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.couriertrack.R;
import com.couriertrack.ui.Base;
import com.couriertrack.ui.home.Home;
import com.couriertrack.ui.login.SignUpFragment;

public class MyOrder extends Base {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        setToolbar();
        setTitle("My Order");
        enableBack(true);

        changeFrag(MyOrderFragment.newInstance(null),false,false);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if(fm.getBackStackEntryCount()>0){
            super.onBackPressed();
        }
        else {
            gotoActivity(Home.class,null,true);
            finish();
        }

    }
}
