package com.couriertrack.ui.webview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.couriertrack.R;
import com.couriertrack.ui.Base;
import com.couriertrack.utils.AppLog;

public class Webview extends Base {

    private static final String TAG = "Webview";
    WebView webview;
    ProgressDialog mProgressDialog;
    String url="",title="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        setToolbar();
        enableBack(true);
        init();
    }

    public void init(){
        webview = findViewById(R.id.webview);
        Bundle b = getIntent().getExtras();
        if(b!=null){
            url = b.getString("url","");
            title = b.getString("title","");
            AppLog.e(TAG,"url: "+url);
            loadURL(url);

        }

        setTitle(title);

       // AppLog.e(TAG," "+getResources().getString(R.string.terms_condition));
    }

    private void loadURL(String url) {

        showLoadingDialog();
        webview.loadUrl(url);
        webview.setWebViewClient(new WebViewClient(){
            public void onPageFinished(android.webkit.WebView view, String url) {
                // do your stuff here

                hideLoadingDialog();

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:")) {

                    if (this != null) {
                        MailTo mt = MailTo.parse(url);
                        Intent i = newEmailIntent(Webview.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                        startActivity(i);
                        view.reload();
                        return true;
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });

    }

    private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    public synchronized void showLoadingDialog() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage(getString(R.string.please_wait));
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                // mProgressDialog.dismiss();
            } else {
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
            }
        } catch (Exception e) {
            Log.e("Base showLoadingDialog", "error is " + e.toString());
        }
    }

    public synchronized void hideLoadingDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.hide();
                mProgressDialog.cancel();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            Log.e("Base hideLoadingDialog", "error is " + e.toString());
        }
    }
}
