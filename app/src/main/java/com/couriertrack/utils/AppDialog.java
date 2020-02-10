package com.couriertrack.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;


public class AppDialog {
    static Dialog dialog;

    public static Dialog showNoNetworkDialog(Context context) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       /* dialog.setContentView(R.layout.dialog_no_network);
        dialog.findViewById(R.id.tvOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/
        dialog.show();
        return dialog;
    }

    public static Dialog showRetryDialog(Context context, final RetryListener retryListener) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       /* dialog.setContentView(R.layout.dialog_retry);

        dialog.findViewById(R.id.tvRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryListener.onRetryClick(dialog);
            }
        });*/
        dialog.show();

        return dialog;
    }

    public interface RetryListener {
        void onRetryClick(Dialog dialog);
    }

}
