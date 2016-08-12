package com.example.wei.flickrviewer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends SingleFragmentActivity {
    private static final String TAG = "MainActivity";

    private static final String EXTRA_REFRESH = "com.example.wei.flickrviewer.extra_refresh";

    public static Intent newIntent(Context context, boolean refresh) {
        return new Intent(context, MainActivity.class)
                .putExtra(EXTRA_REFRESH, refresh);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent(), intent=" + intent);
        super.onNewIntent(intent);
        boolean refresh = intent.getBooleanExtra(EXTRA_REFRESH, false);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof MainFragment) {
                MainFragment mainFragment = (MainFragment) fragment;
                if (refresh) {
                    mainFragment.refresh();
                }
            }
        }
    }

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }
}
