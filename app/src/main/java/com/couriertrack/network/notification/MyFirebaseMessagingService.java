package com.couriertrack.network.notification;
import android.content.Intent;

import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onCreate() {
        super.onCreate();
        AppLog.e(TAG, "OnCreate");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        AppLog.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0)
        {
                String jsonString = remoteMessage.getData().get("notification_type");

                Intent intent = new Intent(getPackageName() +"."+ AppPref.SCREEN_OPEN_COURIER_HOME);

                sendBroadcast(intent);

                Intent intent2 = new Intent(getPackageName() +"."+ AppPref.SCREEN_OPEN_MYORDER);

                sendBroadcast(intent2);

                Intent intent3 = new Intent(getPackageName() +"."+ AppPref.SCREEN_OPEN_ORDERDETAIL);

                sendBroadcast(intent3);


                AppLog.d(TAG, "Message data payload: " + remoteMessage.getData());
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                try {
                    JSONObject jsonNotification = new JSONObject(jsonObject.getString("message"));
                    NotificationHelper.manageNotification(getApplicationContext(), jsonNotification);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            AppLog.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

}