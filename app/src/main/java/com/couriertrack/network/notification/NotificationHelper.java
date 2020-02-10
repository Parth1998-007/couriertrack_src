package com.couriertrack.network.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.couriertrack.R;
import com.couriertrack.ui.Splash;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.ui.home.Home;
import com.couriertrack.ui.myorder.MyOrder;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;

import org.json.JSONObject;

/**
 * Created by frenzin05 on 12/21/2017.
 */

public class NotificationHelper {
    private static final String TAG = "Noti_Helper";


    public static void manageNotification(Context context, JSONObject jsonNotification)
    {
        AppPref appPref= AppPref.getInstance(context);
        Intent intent;

        if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
           intent =  new Intent(context, MyOrder.class);
        else
            intent =  new Intent(context, HomeCourier.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showAlertNotification(context, jsonNotification,intent);
    }

    /**
     * show alert notification
     *
     * @param context
     * @param jsonNotification
     */
    private static void showAlertNotification(Context context, JSONObject jsonNotification, Intent intent) {

        String CHANNEL_ID = context.getResources().getString(R.string.app_name) + "_01";// The id of the channel.

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setChannelId(CHANNEL_ID)
                        .setContentText(jsonNotification.optString("message"))
                        .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(jsonNotification.optString("message")))
                        .setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.app_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotifyMgr != null;
            mNotifyMgr.createNotificationChannel(mChannel);
        }

        assert mNotifyMgr != null;
        mNotifyMgr.notify(001, mBuilder.build());
    }

}
