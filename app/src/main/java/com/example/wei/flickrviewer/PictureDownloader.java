package com.example.wei.flickrviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by wei on 2016/3/1 0001.
 */
public class PictureDownloader<T> extends HandlerThread {
    private static final String TAG = "PictureDownloader";

    private static final int MSG_DOWNLOAD = 0;

    private Handler mResponder;
    private Handler mRequestHandler;
    private OnRespondedListener<T> mOnRespondedListener;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private LruCache<String, Bitmap> mCache;

    interface OnRespondedListener<T> {
        void onResponded(T target, Bitmap result);
    }

    public PictureDownloader(OnRespondedListener<T> onRespondedListener) {
        super(TAG);
        mResponder = new Handler();
        setOnRespondedListener(onRespondedListener);
        mCache = new LruCache<>(100);
    }

    public void setOnRespondedListener(OnRespondedListener<T> onRespondedListener) {
        mOnRespondedListener = onRespondedListener;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new RequestHandler<>(this);
    }

    private static class RequestHandler<T> extends Handler {

        private WeakReference<PictureDownloader<T>> mPictureDownloader;

        public RequestHandler(PictureDownloader<T> pictureDownloader) {
            mPictureDownloader = new WeakReference<>(pictureDownloader);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DOWNLOAD) {
                mPictureDownloader.get().handleRequest((T) msg.obj);
            }
        }
    }

    public Bitmap getCachedResult(String uri) {
        return mCache.get(uri);
    }

    public void clearCache() {
        mCache.evictAll();
    }

    public void queueBitmapForTarget(T target, String uri) {
        mRequestMap.remove(target);
        mRequestHandler.removeMessages(MSG_DOWNLOAD, target);

        if (!TextUtils.isEmpty(uri)) {
            Bitmap cache = mCache.get(uri);
            if (cache != null) {
                if (mOnRespondedListener != null) {
                    mOnRespondedListener.onResponded(target, cache);
                }
            } else {
                mRequestMap.put(target, uri);
                mRequestHandler.obtainMessage(MSG_DOWNLOAD, target).sendToTarget();
            }
        }
    }

    public void cancelAllRequest() {
        mRequestHandler.removeMessages(MSG_DOWNLOAD);
    }


    private void handleRequest(T target) {
        final String uri = mRequestMap.get(target);
        Log.d(TAG, "handleRequest, uri=" + uri);

        byte[] rawData = null;
        try {
            rawData = FlickrFetcher.getBytesFromUrl(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = (rawData != null) ?
                BitmapFactory.decodeByteArray(rawData, 0, rawData.length) :
                null;

        if (bitmap != null) mCache.put(uri, bitmap);

        respond(target, uri, bitmap);
    }

    private void respond(final T target, final String uri, final Bitmap result) {
        mResponder.post(new Runnable() {
            @Override
            public void run() {
                //检查该目标是否有新请求
                if (mRequestMap.get(target) != uri) {
                    return;
                }

                if (mOnRespondedListener != null) {
                    mOnRespondedListener.onResponded(target, result);
                }
            }
        });
    }
}
