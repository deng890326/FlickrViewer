// Generated code from Butter Knife. Do not modify!
package com.example.wei.flickrviewer;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class GalleryPageFragment$$ViewInjector {
  public static void inject(Finder finder, final com.example.wei.flickrviewer.GalleryPageFragment target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492946, "field 'mProgressBar'");
    target.mProgressBar = (android.widget.ProgressBar) view;
    view = finder.findRequiredView(source, 2131492945, "field 'mWebView'");
    target.mWebView = (android.webkit.WebView) view;
  }

  public static void reset(com.example.wei.flickrviewer.GalleryPageFragment target) {
    target.mProgressBar = null;
    target.mWebView = null;
  }
}
