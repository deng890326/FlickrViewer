package com.example.wei.flickrviewer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wei on 2016/3/1 0001.
 */
public class AppGlobalPreference {

    private static final String KEY_SEARCH = "search";
    private static final String KEY_NOTIFY_SWITCH = "notify_switch";
    private static final String KEY_LAST_RESULT_ID = "key_last_result_id";

    private static final String GLOBAL_PRE_NAME = "global_pre_name";

    public static String getSearchText(Context context) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(GLOBAL_PRE_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_SEARCH, "");
    }

    public static void setSearchText(Context context, String text) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(GLOBAL_PRE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_SEARCH, text).apply();
    }

    public static String getLastResultId(Context context) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(GLOBAL_PRE_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_LAST_RESULT_ID, "");
    }

    public static void setLastResultId(Context context, String text) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(GLOBAL_PRE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_LAST_RESULT_ID, text).apply();
    }

    public static boolean getNotifySwitch(Context context) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(GLOBAL_PRE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_NOTIFY_SWITCH, false);
    }

    public static void setNotifySwitch(Context context, boolean on) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(GLOBAL_PRE_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_NOTIFY_SWITCH, on).apply();
    }
}
