package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.SliderModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.SliderProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.NewsHomeResponse;
import com.paramount.bed.data.remote.response.NewsResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.front.slider.LoopingCirclePageIndicator;
import com.paramount.bed.util.AnimateUtils;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ViewPagerCustomDuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TopNewsHomeDialog {
    private BaseActivity activity;
    private ConstraintLayout dialog;
    private ViewPagerCustomDuration viewPager;
    private DialogListener listener;


    private ArrayList<NewsResponse> unreadNews = new ArrayList<>();
    private ArrayList<Fragment> sliderFragments = new ArrayList<>();
    private LoopingCirclePageIndicator indicator;
    private ImageView btnFront;
    private ImageView btnBack;
    HomeService homeService;

    @SuppressLint("CheckResult")
    public TopNewsHomeDialog(BaseActivity activity, DialogListener listener){
        activity.showProgress();
        this.activity = activity;
        this.dialog = activity.findViewById(R.id.dialogTopNewsSingle);
        this.viewPager = activity.findViewById(R.id.view_pager);
        this.btnFront = activity.findViewById(R.id.btnFront);
        this.indicator = activity.findViewById(R.id.indicator);
        this.btnBack = activity.findViewById(R.id.btnBack);
        this.listener = listener;

        homeService = ApiClient.getClient(activity.getApplicationContext()).create(HomeService.class);
        listener.start();
        if (NetworkUtil.isNetworkConnected(activity)) {
            topNewsDialog(0);
        }else {
            finish();
        }

    }

    @SuppressLint("CheckResult")
    public void topNewsDialog(int retryCount){
        homeService.getTopNewsHome(UserLogin.getUserLogin().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse<NewsHomeResponse>>() {
                    @Override
                    public void onNext(BaseResponse<NewsHomeResponse> response) {
                        if (response != null){
                            if (response.isSucces() && response.getData() != null) {
                                NewsHomeResponse homeResponse = response.getData();
                                if (homeResponse.birthday == null && homeResponse.maintenance == null && homeResponse.malfunction == null && homeResponse.regular == null) {
                                    finish();
                                } else {
                                    LogUserAction.sendNewLog(activity.userService, "TOP_NEWS_HOME_SHOW", "", "", "UI000504");
                                    dialog.setVisibility(View.VISIBLE);
                                    activity.hideProgress();
                                    loadSlider(response.getData());
                                    CardView contentNews = activity.findViewById(R.id.contentNews);
                                    contentNews.setAnimation(AnimateUtils.explode((() -> activity.runOnUiThread(() -> {
                                        ImageView imgClose = activity.findViewById(R.id.ivClose2);
                                        imgClose.setOnClickListener(view -> {
                                            if (unreadNews.size() > 0) {

                                                ArrayList<String> unreadNewsTypes = new ArrayList<>();
                                                for (NewsResponse news : unreadNews) {
                                                    String localizedTag = LanguageProvider.getLanguage(news.getKeyTag());
                                                    unreadNewsTypes.add(localizedTag);
                                                }

                                                String message = LanguageProvider.getLanguage("UI000504C003").replace("%UNREAD_TYPE%", TextUtils.join(LanguageProvider.getLanguage("UI000504C010"), unreadNewsTypes));
                                                DialogUtil.createCustomYesNo(activity, "", message, LanguageProvider.getLanguage("UI000504C004"), (dialogInterface, i) -> {

                                                }, LanguageProvider.getLanguage("UI000504C005"), (dialogInterface, i) -> {
                                                    dialog.setVisibility(View.GONE);
                                                    listener.finish();

                                                });
                                            } else {
                                                dialog.setVisibility(View.GONE);
                                                listener.finish();
                                            }

                                        });
                                    }))));
                                }
                            }else{
                                finish();
                            }
                        }else{
                            if(retryCount<BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        topNewsDialog(retryCount+1);
                                    }
                                },BuildConfig.REQUEST_TIME_OUT);
                            }else {
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    topNewsDialog(retryCount+1);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            finish();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void finish(){
        dialog.setVisibility(View.GONE);
        listener.finish();
    }

    @SuppressLint("CheckResult")
    private void logNewsView(int newsId, int retryCount){
        HomeService homeService = ApiClient.getClient(activity.getApplicationContext()).create(HomeService.class);
        homeService.logNews(UserLogin.getUserLogin().getId(),newsId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse<String>>() {
                    @Override
                    public void onNext(BaseResponse<String> stringBaseResponse) {
                        if(stringBaseResponse!=null){
                            unreadNews.removeIf(newsResponse -> newsResponse.id == newsId);
                        }else {
                            if(retryCount<BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Logger.d("LOG NEWS VIEW: " + TopNewsHomeDialog.class.getSimpleName());
                                        logNewsView(newsId,retryCount+1);
                                    }
                                },BuildConfig.REQUEST_TIME_OUT);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Logger.d("LOG NEWS VIEW: " + TopNewsHomeDialog.class.getSimpleName());
                                    logNewsView(newsId,retryCount+1);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadSlider(NewsHomeResponse newsHomeResponse) {
        try {
            if (SliderModel.getAll().size() == 0) {
                SliderProvider.init();
            }
            HashMap<String,NewsResponse> allSliderValue = new HashMap<String, NewsResponse>();
            if(newsHomeResponse.getBirthday()!=null) {
                NewsResponse responseBirthday = newsHomeResponse.getBirthday();
                responseBirthday.setKey("NEWS_BIRTH_DATE");
                responseBirthday.setPriority(1);

                allSliderValue.put("NEWS_BIRTH_DATE",responseBirthday);
                sliderFragments.add(SliderItemNewsFragment.newInstance(responseBirthday,"NEWS_BIRTH_DATE"));
                unreadNews.add(responseBirthday);
            }
            if(newsHomeResponse.getMaintenance()!=null) {
                NewsResponse responseMaintenance = newsHomeResponse.getMaintenance();
                responseMaintenance.setKey("NEWS_MAINTENANCE");
                responseMaintenance.setPriority(2);

                allSliderValue.put("NEWS_MAINTENANCE",responseMaintenance);
                sliderFragments.add(SliderItemNewsFragment.newInstance(responseMaintenance,"NEWS_MAINTENANCE"));
                unreadNews.add(responseMaintenance);
            }
            if(newsHomeResponse.getMalfunction()!=null) {
                NewsResponse responseMalfunction = newsHomeResponse.getMalfunction();
                responseMalfunction.setKey("NEWS_MALFUNCTION");
                responseMalfunction.setPriority(3);

                allSliderValue.put("NEWS_MALFUNCTION",responseMalfunction);
                sliderFragments.add(SliderItemNewsFragment.newInstance(responseMalfunction,"NEWS_MALFUNCTION"));
                unreadNews.add(responseMalfunction);
            }
            if(newsHomeResponse.getRegular()!=null) {
                NewsResponse responseRegular = newsHomeResponse.getRegular();
                responseRegular.setKey("NEWS_REGULAR");
                responseRegular.setPriority(4);

                allSliderValue.put("NEWS_REGULAR",responseRegular);
                sliderFragments.add(SliderItemNewsFragment.newInstance(responseRegular,"NEWS_REGULAR"));
                unreadNews.add(responseRegular);
            }

            if (allSliderValue.size() == 0) {
                //if 0, do nothing, show dummy image for all eternity
                return;

            }
            //hide cover
            //set data and pager
            final ArrayList<NewsResponse> slider = new ArrayList<>();

            for (String key : allSliderValue.keySet()) {
                slider.add(allSliderValue.get(key));
            }

            Collections.sort(slider);

            if(slider.size() > 0){
                logNewsView(slider.get(0).id,0);
            }

            SliderNewsFragmentAdapter sliderNewsFragmentAdapter = new SliderNewsFragmentAdapter(activity.getSupportFragmentManager(),slider);
            viewPager.setAdapter(sliderNewsFragmentAdapter);
            viewPager.setScrollDurationFactor(5);
            viewPager.setOffscreenPageLimit(1);
            viewPager.setSwipeLocked(true);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if(position==0) btnBack.setVisibility(View.GONE);
                    else btnBack.setVisibility(View.VISIBLE);
                    if(position==allSliderValue.size()-1) btnFront.setVisibility(View.GONE);
                    else  btnFront.setVisibility(View.VISIBLE);
                    btnBack.setOnClickListener(v -> {
                        viewPager.setCurrentItem(position-1);
                    });
                    btnFront.setOnClickListener(v -> {
                        viewPager.setCurrentItem(position+1);
                    });
                    logNewsView(slider.get(position).id,0);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //endregion
            //set indicator
            indicator.setViewPager(viewPager, sliderNewsFragmentAdapter);
            sliderNewsFragmentAdapter.registerDataSetObserver(indicator.getDataSetObserver());
            btnBack.setVisibility(View.GONE);
            if(allSliderValue.size()<2) btnFront.setVisibility(View.GONE);
            btnFront.setOnClickListener(v -> {
                viewPager.setCurrentItem(1);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface DialogListener {
        void start();
        void finish();
    }
}
