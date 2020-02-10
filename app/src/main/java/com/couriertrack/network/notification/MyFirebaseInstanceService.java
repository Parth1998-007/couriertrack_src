package com.couriertrack.network.notification;

import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceService extends FirebaseInstanceIdService {
    private static final String TAG="MyFirebaseInstanceServi";
    private AppPref appPref;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        appPref = AppPref.getInstance(getApplicationContext());

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        AppLog.e(TAG, "Refreshed token: " + refreshedToken);

        /* If you want to send messages to this application instance or manage this apps subscriptions on the server side, send the Instance ID token to your app server.*/
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        appPref.set(AppPref.FCM_TOKEN,refreshedToken);
        AppLog.e("TOKEN ", refreshedToken.toString());
    }
}
