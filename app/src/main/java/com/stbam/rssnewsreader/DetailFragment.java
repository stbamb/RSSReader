package com.stbam.rssnewsreader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;
import com.stbam.rssnewsreader.parser.RSSFeed;

public class DetailFragment extends Fragment {

    private int fPos;
    RSSFeed fFeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fFeed = (RSSFeed)getArguments().getSerializable("feed");
        fPos = getArguments().getInt("pos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        // Initializr views
        TextView title = (TextView)view.findViewById(R.id.title);
        WebView desc = (WebView)view.findViewById(R.id.desc);
        // Enable the vertical fading edge (by default it is disabled)
        ScrollView sv = (ScrollView)view.findViewById(R.id.sv);
        sv.setVerticalFadingEdgeEnabled(true);
        // Set webview properties
        WebSettings ws = desc.getSettings();
        ws.setLightTouchEnabled(false);
        ws.setPluginState(PluginState.ON);
        ws.setJavaScriptEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        ws.setBuiltInZoomControls(false);
        ws.setSupportZoom(false);
        ws.setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        desc.setWebViewClient(new WebViewClient());

        // Set the views
        title.setText(fFeed.getItem(fPos).getTitle());

        String data = "<html><body><center>" + fFeed.getItem(fPos).getDescription() + "</center></body></html>";
        desc.loadData(data, "text/html; charset=UTF-8", null);

        // para ver como se forman los tags HTML que carga el WebView
        //Log.d("HTML", data);
        return view;
    }
}
