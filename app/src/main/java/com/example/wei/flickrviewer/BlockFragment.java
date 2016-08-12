package com.example.wei.flickrviewer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by wei on 2016/3/3 0003.
 */
public class BlockFragment extends Fragment {
    private static final String TAG = "BlockFragment";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "canceling broadcast");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = NotificationReceiver.newIntentFilter();
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter,
                Utils.PERMISSION_PRIVATE, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
