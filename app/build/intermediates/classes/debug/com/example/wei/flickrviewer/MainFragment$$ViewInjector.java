// Generated code from Butter Knife. Do not modify!
package com.example.wei.flickrviewer;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainFragment$$ViewInjector {
  public static void inject(Finder finder, final com.example.wei.flickrviewer.MainFragment target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492948, "field 'mRecyclerView'");
    target.mRecyclerView = (android.support.v7.widget.RecyclerView) view;
    view = finder.findRequiredView(source, 2131492947, "field 'mSwipeRefreshLayout'");
    target.mSwipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) view;
  }

  public static void reset(com.example.wei.flickrviewer.MainFragment target) {
    target.mRecyclerView = null;
    target.mSwipeRefreshLayout = null;
  }
}
