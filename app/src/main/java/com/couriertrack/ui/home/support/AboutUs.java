package com.couriertrack.ui.home.support;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.couriertrack.R;
import com.couriertrack.ui.Base;
import com.couriertrack.ui.webview.Webview;

public class AboutUs extends Base {

    TextView tvwebsite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        setToolbar();
        setTitle("About Us");
        enableBack(true);

        init();
    }

    private void init() {

        tvwebsite = findViewById(R.id.tv_website);
        /*tvwebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        tvwebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("url",getResources().getString(R.string.website));
                b.putString("title","About Us");
                gotoActivity(Webview.class,b,false);
            }
        });
        /*String privacy = "<a href='"+getResources().getString(R.string.website)+"'> www.passonn.com </a>";//+getResources().getString(R.string.privacy_policy)+
        tvwebsite.setMovementMethod(LinkMovementMethod.getInstance());
        tvwebsite.setText(Html.fromHtml(privacy));*/
    }
}
