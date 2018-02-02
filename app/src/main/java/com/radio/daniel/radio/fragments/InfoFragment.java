package com.radio.daniel.radio.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.radio.daniel.radio.R;


public class InfoFragment extends Fragment {

    public final static String TAG = "InfoFragment";

    public InfoFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_info, container, false);
        final WebView webView = (WebView) root.findViewById(R.id.info_web_view);

        webView.loadUrl("file:///android_asset/license.html");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

}
