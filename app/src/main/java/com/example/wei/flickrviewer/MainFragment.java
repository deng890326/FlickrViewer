package com.example.wei.flickrviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wei on 2016/3/1 0001.
 */
public class MainFragment extends BlockFragment {

    private static final String TAG = "MainFragment";

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

//    @InjectView(R.id.progress)
//    ProgressBar mProgressBar;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private PictureDownloader<GalleryHolder> mPictureDownloader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(null, 3));

        final SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchGalleryTask().execute();
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        refresh();

        return view;
    }

    public void refresh() {
        Log.d(TAG, "refresh()");
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                new FetchGalleryTask().execute();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPictureDownloader = new PictureDownloader<>(new PictureDownloader.OnRespondedListener<GalleryHolder>() {
            @Override
            public void onResponded(GalleryHolder target, Bitmap result) {
                if (result != null && isResumed()) {
                    target.mImageView.setImageBitmap(result);
                }
            }
        });
        mPictureDownloader.start();
        mPictureDownloader.getLooper();

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit");
                mPictureDownloader.clearCache();
                AppGlobalPreference.setSearchText(getActivity(), query);
                refresh();
                searchView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange");
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchView.onActionViewCollapsed();
                }
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "setOnSearchClickListener");
                searchView.onActionViewExpanded();
                String text = AppGlobalPreference.getSearchText(getActivity());
                searchView.setQuery(text, false);
            }
        });

        MenuItem subscribeItem = menu.findItem(R.id.subscribe);
        boolean on = NotificationService.isNotifyServiceOn(getActivity());
        subscribeItem.setTitle(on ? R.string.close_subscribe : R.string.open_subcribe);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                AppGlobalPreference.setSearchText(getActivity(), "");
                refresh();
                break;
            case R.id.search:
                break;
            case R.id.subscribe:
                boolean on = NotificationService.isNotifyServiceOn(getActivity());
                NotificationService.setNotifyService(getActivity(), !on);
                getActivity().invalidateOptionsMenu();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        mPictureDownloader.cancelAllRequest();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mPictureDownloader.clearCache();
    }

    private void setupAdapter(List<GalleryItem> list) {
        GalleryItemAdapter adapter = new GalleryItemAdapter(list);
        mRecyclerView.setAdapter(adapter);
    }

    private class FetchGalleryTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            String query = AppGlobalPreference.getSearchText(getActivity());
            if (TextUtils.isEmpty(query)) {
                return FlickrFetcher.getRecentGalleryItems();
            }
            return FlickrFetcher.getSearchGalleryItems(query);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> list) {
            super.onPostExecute(list);
//            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
            if (list != null) {
                setupAdapter(list);
            } else {
                Toast.makeText(getActivity(), R.string.failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    class GalleryHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {

        @InjectView(R.id.image)
        ImageView mImageView;

        private GalleryItem mGalleryItem;

        public GalleryHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            mGalleryItem = galleryItem;
            Bitmap cache = mPictureDownloader.getCachedResult(mGalleryItem.getUrl());
            if (cache == null) {
                mImageView.setImageResource(R.drawable.picture_128);
            }
            mPictureDownloader.queueBitmapForTarget(this, mGalleryItem.getUrl());
        }


        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(getActivity(), mGalleryItem.getCaption(), Toast.LENGTH_LONG).show();
            return true;
        }

        @Override
        public void onClick(View v) {
            String url = mGalleryItem.getGalleryPageUrl();
            Intent intent = GalleryPageActivity.newIntent(getActivity(), url);
            startActivity(intent);
        }
    }

    private class GalleryItemAdapter extends RecyclerView.Adapter<GalleryHolder> {

        private List<GalleryItem> mGalleryItemList;

        public GalleryItemAdapter(List<GalleryItem> galleryItemList) {
            mGalleryItemList = galleryItemList;
        }

        @Override
        public GalleryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_gallery_item, parent, false);
            return new GalleryHolder(view);
        }

        @Override
        public void onBindViewHolder(GalleryHolder holder, int position) {
            holder.bindGalleryItem(mGalleryItemList.get(position));
        }

        @Override
        public int getItemCount() {
            return mGalleryItemList.size();
        }
    }
}
