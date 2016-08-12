package com.example.wei.flickrviewer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by wei on 2016/3/3 0003.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final String ACTION_NOTIFIACTION = "com.example.wei.flickrviewer.action_notification";
    private static final String EXTRA_ID = "com.example.wei.flickrviewer.extra_id";
    private static final String EXTRA_NOTIFICATION = "com.example.wei.flickrviewer.extra_notification";

    public static Intent newIntent(int id, Notification notification) {
        return new Intent(ACTION_NOTIFIACTION)
                .putExtra(EXTRA_ID, id)
                .putExtra(EXTRA_NOTIFICATION, notification);
    }

    public static IntentFilter newIntentFilter() {
        return new IntentFilter(ACTION_NOTIFIACTION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (getResultCode() == Activity.RESULT_CANCELED) {
            return;
        }

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(EXTRA_ID, 0);
        Notification notification = intent.getParcelableExtra(EXTRA_NOTIFICATION);
        nm.notify(id, notification);
    }
}
