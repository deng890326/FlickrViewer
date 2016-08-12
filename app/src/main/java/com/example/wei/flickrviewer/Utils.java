package com.example.wei.flickrviewer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by wei on 2016/3/3 0003.
 */
public class Utils {

    public static final String PERMISSION_PRIVATE = "com.example.wei.flickrviewer.private_broadcast";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo.isAvailable() && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
