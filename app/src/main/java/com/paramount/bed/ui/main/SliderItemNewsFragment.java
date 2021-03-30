package com.paramount.bed.ui.main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.paramount.bed.BedApplication;
import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.NewsResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SliderItemNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SliderItemNewsFragment extends Fragment {

    private String defaultCaption = "";
    private NewsResponse news;

    public SliderItemNewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SliderItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SliderItemNewsFragment newInstance(NewsResponse news, String newsKey) {
        SliderItemNewsFragment fragment = new SliderItemNewsFragment();
        SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("BAD_NEWS", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSettings.edit();
        Gson gson = new Gson();
        NewsResponse homeNews = new NewsResponse();
        homeNews.setUrl(news.url);
        homeNews.setContent(news.content);
        homeNews.setCreated_date(news.created_date);
        homeNews.setKey(newsKey);
        homeNews.setTitle(news.title);
        homeNews.setId(news.id);

        String json = gson.toJson(homeNews);
        mEditor.putString(newsKey,json);
        mEditor.commit();

        Bundle bundle = new Bundle();
        bundle.putString("key_bundle",newsKey);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("BAD_NEWS", Context.MODE_PRIVATE);
        String news_key = getArguments().getString("key_bundle");
        Gson gson = new Gson();
        String json = mSettings.getString(news_key, "");
        this.news=gson.fromJson(json, NewsResponse.class);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider_news, container, false);
        WebView webNews = view.findViewById(R.id.slideWebView);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtDate = view.findViewById(R.id.txtDate);
        TextView txtReadMore = view.findViewById(R.id.txtReadmore);

        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        webNews.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webNews.loadData("","text/html","utf-8");

        txtTitle.setText(this.news.getTitle());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.news.getCreated_date());

            String langDate = LanguageProvider.getLanguage("UI000504C001");
            txtDate.setText(langDate.replace("%YEAR%", new SimpleDateFormat("yyyy").format(this.news.getCreated_date()))
                    .replace("%MONTH%", new SimpleDateFormat("MM").format(this.news.getCreated_date()))
                    .replace("%DAY%", new SimpleDateFormat("dd").format(this.news.getCreated_date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String langreadmore = LanguageProvider.getLanguage("UI000504C002");
        SpannableString readmore = new SpannableString(langreadmore);
        readmore.setSpan(new UnderlineSpan(), 0, readmore.length(), 0);
        txtReadMore.setText(readmore);
        txtReadMore.setPaintFlags(txtReadMore.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        if(news.getUrl().length()>0) {
            txtReadMore.setVisibility(View.VISIBLE);
            txtReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getUrl()));
                    getActivity().startActivity(browserIntent);
                }
            });
        }else {
            txtReadMore.setVisibility(View.INVISIBLE);
        }


        webNews.loadDataWithBaseURL("file:///android_asset/", this.news.getContent(), "text/html", "utf-8", "");
        return view;
    }
}
