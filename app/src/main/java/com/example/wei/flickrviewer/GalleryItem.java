package com.example.wei.flickrviewer;

import android.net.Uri;

/**
 * Created by wei on 2016/3/1 0001.
 */
public class GalleryItem {

    private String mId;
    private String mCaption;
    private String mUrl;
    private String mOwner;

    public String getId() {
        return mId;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getGalleryPageUrl() {
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build().toString();
    }
}
