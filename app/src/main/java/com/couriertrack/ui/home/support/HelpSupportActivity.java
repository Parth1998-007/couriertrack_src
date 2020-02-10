package com.couriertrack.ui.home.support;

import android.os.Bundle;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.ui.Base;

public class HelpSupportActivity extends Base {

    TextView tvaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        setToolbar();
        setTitle("Help And Support");
        enableBack(true);
        changeFrag(HelpSupportFragment.newInstance(null),false,false);
    }


}
