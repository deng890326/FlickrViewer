package com.example.wei.flickrviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wei on 2016/3/3 0003.
 */
public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean on = NotificationService.isNotifyServiceOn(context);
        NotificationService.setNotifyService(context, on);
    }
}
