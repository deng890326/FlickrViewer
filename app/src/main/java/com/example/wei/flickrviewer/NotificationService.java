package com.example.wei.flickrviewer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by wei on 2016/3/3 0003.
 */
public class NotificationService extends IntentService {
    private static final String TAG = "NotificationService";

    private static final String ACTION_CHECK_UPDATE = "com.example.wei.flickrviewer.aciont_check_update";
    private static final long NOTIFICATION_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;


    public NotificationService() {
        super(TAG);
    }

    public static void setNotifyService(Context context, boolean on) {
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(ACTION_CHECK_UPDATE);
        PendingIntent operation = PendingIntent.getService(context, 0, intent, 0);
        if (on) {
            am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    NOTIFICATION_INTERVAL, operation);
        } else {
            am.cancel(operation);
        }
        AppGlobalPreference.setNotifySwitch(context, on);
    }

    public static boolean isNotifyServiceOn(Context context) {
        return AppGlobalPreference.getNotifySwitch(context);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        String action = intent.getAction();
        if (ACTION_CHECK_UPDATE.equals(action)) {
            if (!Utils.isNetworkAvailable(this)) {
                Log.d(TAG, "onHandleIntent, no network");
                return;
            }

            String text = AppGlobalPreference.getSearchText(this);
            String lastId = AppGlobalPreference.getLastResultId(this);

            List<GalleryItem> result;
            if (TextUtils.isEmpty(text)) {
                result = FlickrFetcher.getRecentGalleryItems();
            } else {
                result = FlickrFetcher.getSearchGalleryItems(text);
            }

            if (result == null) {
                Log.d(TAG, "onHandleIntent, result == null");
                return;
            }

            GalleryItem item = result.get(0);
            if (!item.getId().equals(lastId)) {
                Log.d(TAG, "onHandleIntent, got new id");
                Intent newIntent = MainActivity.newIntent(this, true);
                Log.d(TAG, "onHandleIntent(), newIntent=" + newIntent);
                PendingIntent enterApp = PendingIntent.getActivity(this, 0, newIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setTicker(getString(R.string.new_pictures_title))
                        .setContentTitle(getString(R.string.new_pictures_title))
                        .setContentText(getString(R.string.new_pictures_text))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentIntent(enterApp)
                        .build();
                Intent NotifyIntent = NotificationReceiver.newIntent(0, notification);
                sendOrderedBroadcast(NotifyIntent, Utils.PERMISSION_PRIVATE, null, null,
                        Activity.RESULT_OK, null, null);
                AppGlobalPreference.setLastResultId(this, item.getId());
            } else {
                Log.d(TAG, "onHandleIntent, got old id");
            }
        }
    }
}
