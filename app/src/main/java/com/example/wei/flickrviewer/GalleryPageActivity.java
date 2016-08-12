package com.example.wei.flickrviewer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by wei on 2016/3/1 0001.
 */
public class GalleryPageActivity extends SingleFragmentActivity {

    private static final String KEY_URL = "com.example.wei.flickrviewer.key_url";

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, GalleryPageActivity.class);
        intent.putExtra(KEY_URL, url);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String url = getIntent().getStringExtra(KEY_URL);
        return GalleryPageFragment.newInstance(url);
    }
}
