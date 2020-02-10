package com.couriertrack.ui.courier.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.ui.Base;

public class WalletActivity extends Base {

    TextView tvaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        setToolbar();
        setTitle("My Earnings");
        enableBack(true);
        changeFrag(WalletFragment.newInstance(null),false,false);
    }


}
