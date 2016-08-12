package com.example.wei.flickrviewer;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 2016/3/1 0001.
 */
public class FlickrFetcher {

    private static String TAG = "FlickrFetcher";

    private static final String API_KEY = "e37500e60e947cf4df1ecc9b26d96998";
    private static final String FETCH_RECENT_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";

    private static final Uri ENDPOINT =
            Uri.parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    private static final int BUFFER_SIZE = 1024;

    public static byte[] getBytesFromUrl(String fromUrl) throws IOException {
        URL url = new URL(fromUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(connection.getResponseMessage() + "with: " + fromUrl);
        }

        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int readSize;
        while ((readSize = inputStream.read(buffer, 0, BUFFER_SIZE)) > 0) {
            outputStream.write(buffer, 0, readSize);
        }

        connection.disconnect();
        return outputStream.toByteArray();
    }

    public static String getStringFromUrl(String fromUrl) throws IOException {
        return new String(getBytesFromUrl(fromUrl));
    }

    public static List<GalleryItem> getRecentGalleryItems() {
        String url = buildUrl(FETCH_RECENT_METHOD, null);
        Log.d(TAG, "getRecentGalleryItems, url=" + url);
        return getGalleryItemList(url);
    }

    public static List<GalleryItem> getSearchGalleryItems(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        Log.d(TAG, "getSearchGalleryItems, url=" + url);
        return getGalleryItemList(url);
    }

    @Nullable
    private static List<GalleryItem> getGalleryItemList(String url) {
        List<GalleryItem> result = null;
        try {
            String json = getStringFromUrl(url);
            Log.d(TAG, "getRecentGalleryItems, json=" + json);
            result = getGalleryItemFromJson(json);
        } catch (IOException e) {
            Log.e(TAG, "fail to get url");
        } catch (JSONException e) {
            Log.e(TAG, "fail to parse json");
        }
        return result;
    }

    private static String buildUrl(String method, String text) {
        Uri.Builder builder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);
        if (!TextUtils.isEmpty(text)) {
            builder.appendQueryParameter("text", text);
        }

        return builder.build().toString();
    }

    private static List<GalleryItem> getGalleryItemFromJson(String fromJson) throws JSONException {
        JSONObject all = new JSONObject(fromJson);
        JSONArray photos = all.getJSONObject("photos").getJSONArray("photo");

        ArrayList<GalleryItem> result = new ArrayList<>();
        for (int i = 0; i < photos.length(); i++) {
            JSONObject photo = photos.getJSONObject(i);

            if (!photo.has("url_s")) {
                continue;
            }

            GalleryItem item = new GalleryItem();
            item.setId(photo.getString("id"));
            item.setCaption(photo.getString("title"));
            item.setOwner(photo.getString("owner"));
            item.setUrl(photo.getString("url_s"));
            result.add(item);
        }
        Log.d(TAG, "getGalleryItemFromJson, result=" + result);

        return result;
    }
}
