package com.paramount.bed.ui.front;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.paramount.bed.BedApplication;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.AppStateModel;
import com.paramount.bed.data.model.LanguageModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.SleepResetModel;
import com.paramount.bed.data.model.SliderModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.TutorialImageModel;
import com.paramount.bed.data.model.TutorialShowModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.UserRegistrationModel;
import com.paramount.bed.data.model.VersionModel;
import com.paramount.bed.data.provider.ContentProvider;
import com.paramount.bed.data.provider.FormPolicyProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.MaxRowProvider;
import com.paramount.bed.data.provider.SliderProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.TutorialDeviceResponse;
import com.paramount.bed.data.remote.response.TutorialResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.front.slider.LoopingCirclePageIndicator;
import com.paramount.bed.ui.front.slider.SliderFragmentAdapter;
import com.paramount.bed.ui.front.slider.SliderItemFragment;
import com.paramount.bed.ui.login.LoginActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.main.TncAppUpdateActivity;
import com.paramount.bed.ui.registration.CompanyRegistrationActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.ui.registration.TncActivity;
import com.paramount.bed.util.AppUpdaterUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.NemuriConstantsUtil;
import com.paramount.bed.util.ServerUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ViewPagerCustomDuration;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.paramount.bed.util.AppUpdaterUtil.ANDROID_APPLICATION_TYPE_BED;
import static com.paramount.bed.util.NetworkUtil.isNetworkConnected;

public class SliderActivity extends BaseActivity {

    private ArrayList<Fragment> sliderFragments = new ArrayList<>();
    private SliderFragmentAdapter sliderFragmentAdapter;
    public static SliderTimer sliderTimer;
    public static Timer timer;
    private Boolean loginClicked = false;
    public int isAllDownload = 0;
    @BindView(R.id.dummy_image)
    ImageView dummyImage;

    @BindView(R.id.view_pager)
    ViewPagerCustomDuration viewPager;

    @BindView(R.id.indicator)
    LoopingCirclePageIndicator indicator;
    private boolean isSliderSuccess;
    private boolean isLanguageSuccess;
    private boolean isHomeSuccess;
    private boolean isContentNetworkFailure;
    private boolean isContentFailed;
    private FormPolicyProvider formPolicyProvider;
    private MaxRowProvider maxRowProvider;
    public boolean isScreenUncompatibilityShowed = false;

    @OnClick(R.id.btnLogin)
    void login() {
        if (!loginClicked) {
            loginClicked = true;
            try {
                //#region Register SN
                TutorialShowModel.clear();
                SharedPreferences sn = BedApplication.getsApplication().getSharedPreferences("SN_NEMURI_SCAN", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sn.edit();
                editor.putString("SERIAL_NUMBER", "");
                editor.apply();
                //#endregion
            } catch (Exception e) {

            }
            if (StatusLogin.getUserLogin() == null) {
                NemuriScanModel.clear();
                SleepResetModel.clear();
            }
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            loginIntent.putExtra("fromLogin", true);
            loginIntent.putExtra("isFromSlider", true);
            SliderActivity.this.startActivity(loginIntent);
        }

    }

    @OnClick(R.id.btnSignup)
    void signup() {
        if (isNetworkConnected(this)) {
            if (checkIfUserExist()) {
                DialogUtil.createCustomYesNo(SliderActivity.this, "",
                        LanguageProvider.getLanguage("UI000200C011"),
                        LanguageProvider.getLanguage("UI000200C012"),
                        (dialogInterface, i) -> {
                            LogUserAction.sendKickLog(userService, "kick_user_cancel", "UI000200");
                            dialogInterface.dismiss();
                        },
                        LanguageProvider.getLanguage("UI000200C013"), (dialogInterface, i) -> {
                            LogUserAction.sendKickLog(userService, "kick_user_confirm", "UI000200");
                            dialogInterface.dismiss();
                            clearExistingUser();
                            new Handler().postDelayed(() -> doSignUpPersonal(true), 100);
                        });
            } else {
                doSignUpPersonal(false);
            }
        } else {
            DialogUtil.createSimpleOkDialog(this,
                    "",
                    LanguageProvider.getLanguage("UI000200C009"),
                    LanguageProvider.getLanguage("UI000200C010"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }
    }

    @OnClick(R.id.btnSignupCompany)
    void signupCompany() {
        if (isNetworkConnected(this)) {
            if (checkIfUserExist()) {
                DialogUtil.createCustomYesNo(SliderActivity.this, "",
                        LanguageProvider.getLanguage("UI000200C011"),
                        LanguageProvider.getLanguage("UI000200C012"),
                        (dialogInterface, i) -> {
                            LogUserAction.sendKickLog(userService, "kick_user_cancel", "UI000200");
                            dialogInterface.dismiss();
                        },
                        LanguageProvider.getLanguage("UI000200C013"), (dialogInterface, i) -> {
                            LogUserAction.sendKickLog(userService, "kick_user_confirm", "UI000200");
                            dialogInterface.dismiss();
                            clearExistingUser();
                            new Handler().postDelayed(() -> doSignUpCompany(true), 100);
                        });
            } else {
                doSignUpCompany(false);
            }
        } else {
            DialogUtil.createSimpleOkDialog(this,
                    "",
                    LanguageProvider.getLanguage("UI000200C009"),
                    LanguageProvider.getLanguage("UI000200C010"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }
    }

    public static Button btnLogin;
    public static TextView btnSignupCompany, btnSignup;
    public static LinearLayout divider;
    public static SliderActivity activity;

    View.OnTouchListener touchTransparent = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // touch down code
                    if (view.getId() == R.id.btnSignup) {
                        btnSignup.setTextColor(btnSignup.getTextColors().withAlpha(128));
                    } else if (view.getId() == R.id.btnSignupCompany) {
                        btnSignupCompany.setTextColor(btnSignupCompany.getTextColors().withAlpha(128));
                    }

                    break;

                case MotionEvent.ACTION_MOVE:
                    // touch move code
                    break;

                case MotionEvent.ACTION_UP:
                    // touch up code
                    if (view.getId() == R.id.btnSignup) {
                        btnSignup.setTextColor(btnSignup.getTextColors().withAlpha(255));
                    } else if (view.getId() == R.id.btnSignupCompany) {
                        btnSignupCompany.setTextColor(btnSignupCompany.getTextColors().withAlpha(255));
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        isScreenUncompatibilityShowed = false;
        formPolicyProvider = new FormPolicyProvider(this);
        maxRowProvider = new MaxRowProvider(this);
        activity = this;
        timer = null;
        sliderTimer = null;
        isSliderSuccess = false;
        isLanguageSuccess = false;
        isHomeSuccess = false;
        isContentNetworkFailure = false;
        isContentFailed = false;
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnSignupCompany = findViewById(R.id.btnSignupCompany);
        divider = findViewById(R.id.divider);

        btnSignup.setOnTouchListener(touchTransparent);
        btnSignupCompany.setOnTouchListener(touchTransparent);

        dummyImage.setOnTouchListener(
                new View.OnTouchListener() {
                    private boolean moved;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        //onFiveClick();
                        return false;
                    }
                }
        );
        if (AppStateModel.isFirstRun()) {
            SliderProvider.init();
            LanguageProvider.init(this);
            AppStateModel.setFirstRun(false);
            UserLogin.init();
        }
        if (LanguageModel.getAll().size() == 0) {
            LanguageProvider.init(this);
        }
        if (UserLogin.isLogin()) {
            Intent intent = new Intent(SliderActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return;
        }
        applyLocalization();
        showTnCAfterUpdate();
    }

    private void showTnCAfterUpdate() {
        if (VersionModel.getAll().size() == 1) {
            VersionModel oldVersion = VersionModel.getAll().get(0);
            String localVersion = "1" + String.valueOf(BuildConfig.VERSION_MAJOR) + String.valueOf(BuildConfig.VERSION_MINOR) + String.valueOf(BuildConfig.VERSION_REVISION);
            String serverVersion = "1" + String.valueOf(oldVersion.getMajor()) + String.valueOf(oldVersion.getMinor()) + String.valueOf(oldVersion.getRevision());
            if (Integer.parseInt(localVersion) > Integer.parseInt(serverVersion)) {
                Intent intent = new Intent(SliderActivity.this, TncAppUpdateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("isHome", false);
                startActivity(intent);
            } else {
                getLatestContentVersion();
            }

        } else {
            //Insert OldVersion
            VersionModel.clear();
            VersionModel versionModel = new VersionModel();
            versionModel.setMajor(BuildConfig.VERSION_MAJOR);
            versionModel.setMinor(BuildConfig.VERSION_MINOR);
            versionModel.setRevision(BuildConfig.VERSION_REVISION);
            versionModel.setTNCRead(false);
            versionModel.insert();
            getLatestContentVersion();

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void getLatestContentVersion() {
        showLoading();
        maxRowProvider.getMaxRow((maxRowModel) -> runOnUiThread(() -> {
            formPolicyProvider.getFormPolicy((formPolicyModel -> runOnUiThread(() -> {
                ContentProvider.refreshContent(SliderActivity.this, homeService, new ContentProvider.ContentRefreshListener() {
                    @Override
                    public void onContentSliderRefreshSuccess() {
                        isSliderSuccess = true;
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                loadSlider();
                            }
                        });
                        next();
                    }

                    @Override
                    public void onContentLanguageRefreshSuccess() {
                        isLanguageSuccess = true;
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                applyLocalization();
                            }
                        });
                        next();
                    }

                    @Override
                    public void onContentRefreshFailed(String message) {
                        isContentFailed = true;
                        next();
                    }

                    @Override
                    public void onContentRefreshNetworkFailure(Throwable t) {
                        isContentNetworkFailure = true;
                        next();
                    }

                    @Override
                    public void onContentHomeRefreshSuccess() {
                        isHomeSuccess = true;
                        next();
                    }
                });
            })));
        }));
    }

    private void next() {
        setLanguage();
        if (isSliderSuccess && isLanguageSuccess && isHomeSuccess) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    hideLoading();
                    AppUpdaterUtil.checkVersion(ANDROID_APPLICATION_TYPE_BED, homeService, activity);
                    NemuriConstantsUtil.Sync(nemuriScanService);
                }
            });
        } else if (isContentFailed || isContentNetworkFailure) {
            hideLoading();
        } else {
            hideLoading();
        }
    }

    private void showDisplayUncompatibilityAlert() {
        DialogUtil.createSimpleOkDialog(this,
                "",
                LanguageProvider.getLanguage(LanguageProvider.getLanguage("UI000100C001").equals("UI000100C001") ? "本アプリはご利用のデバイスをサポートしておりません。続行できますが、このデバイスではアプリが正常に動作しない可能性があります" : LanguageProvider.getLanguage("UI000100C001")),
                LanguageProvider.getLanguage(LanguageProvider.getLanguage("UI000100C002").equals("UI000100C002") ? "OK" : LanguageProvider.getLanguage("UI000100C002")), ((dialog, which) -> {
                    dialog.dismiss();
                    isScreenUncompatibilityShowed = true;
                }));
    }

    @Override
    protected void onStop() {
        super.onStop();
        IOSDialogRight.Dismiss();
    }

    private int count = 0;
    private long startMillis = 0;

    private void loadSlider() {
        try {
            if (SliderModel.getAll().size() == 0) {
                SliderProvider.init();
            }
            ArrayList<SliderModel> allSliderModel = SliderModel.getAll();
            if (allSliderModel.size() == 0) {
                //if 0, do nothing, show dummy image for all eternity

                return;

            }
            //hide cover
            dummyImage.setVisibility(View.GONE);
            //set data and pager
            for (SliderModel sliderModel : allSliderModel) {
                sliderFragments.add(SliderItemFragment.newInstance(sliderModel));
                if (sliderFragments.size() > 1) break;
            }
            sliderFragmentAdapter = new SliderFragmentAdapter(getSupportFragmentManager(), allSliderModel);
            viewPager.setAdapter(sliderFragmentAdapter);
            viewPager.setScrollDurationFactor(5);
            viewPager.setOffscreenPageLimit(1);
            // somewhere where you setup your viewPager add this
            viewPager.setOnTouchListener(
                    new View.OnTouchListener() {
                        private boolean moved;

                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (BaseActivity.IS_DEVELOPMENT) {
                                // onFiveClick();
                            }
                            return false;
                        }
                    }
            );


            //region Change text slider from API : Modified by Angga Fachri Hamdani @2018-12-04 15:02 (UTC +7)
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    try {
                        changeTextColour(SliderModel.getAll().get(position % SliderModel.getAll().size()).getTextColor());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //endregion
            //set indicator
            indicator.setViewPager(viewPager, sliderFragmentAdapter);
            sliderFragmentAdapter.registerDataSetObserver(indicator.getDataSetObserver());


            //add "buffer" for swipe back
            viewPager.setCurrentItem(sliderFragmentAdapter.getRealCount() * 4, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFiveClick() {
        //get system current milliseconds
        long time = System.currentTimeMillis();


        //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
        if (startMillis == 0 || (time - startMillis > 3000)) {
            startMillis = time;
            count = 1;
        }
        //it is not the first, and it has been  less than 3 seconds since the first
        else { //  time-startMillis< 3000
            count++;
        }

        if (count == 5) {
            Intent a = new Intent(SliderActivity.this, ServerUtil.class);
            a.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(a);
        }

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_slider;
    }

    @Override
    protected int getStatusBarTheme() {
        return this.STATUS_BAR_LIGHT;
    }

    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            SliderActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    //region Change text slider from API : Modified by Angga Fachri Hamdani @2018-12-04 15:02 (UTC +7)
    public static void changeTextColour(String HexColour) {
        try {
            divider.setBackgroundColor(Color.parseColor(HexColour));
            btnSignup.setTextColor(Color.parseColor(HexColour));
            btnSignupCompany.setTextColor(Color.parseColor(HexColour));
        } catch (Exception e) {
            divider.setBackgroundColor(Color.BLACK);
            btnSignup.setTextColor(Color.BLACK);
            btnSignupCompany.setTextColor(Color.BLACK);
        }
    }
    //endregion

    @Override
    public void onResume() {
        super.onResume();
        //set auto slide
        if (timer == null && sliderTimer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(sliderTimer == null ? new SliderTimer() : sliderTimer, 6000, 6000);
        }

        loginClicked = false;
        loadSlider();
        if (!isSupportedScreen(SliderActivity.this) && ApiClient.LogData.getLoginStatus(SliderActivity.this) == 0 && !isScreenUncompatibilityShowed) {
            showDisplayUncompatibilityAlert();
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    //region Tutorial : Last Update Angga Fachri Hamdani @ 2018-11-24
    public static int FLAG_IMG_NUM;
    public static int FLAG_IMG_POS;
    public static TutorialDeviceResponse[] data;

    public void getTutorial() {
        homeService.getTutorial(1).enqueue(new Callback<TutorialResponse>() {
            @Override
            public void onResponse(Call<TutorialResponse> call, Response<TutorialResponse> response) {
                TutorialResponse tutorialResponse = response.body();
                if (tutorialResponse != null) {
                    if (tutorialResponse.isSucces()) {
                        data = tutorialResponse.getData();
                        TutorialImageModel.clear();
                        FLAG_IMG_NUM = 1;
                        FLAG_IMG_POS = 1;
                        saveTutorialData(data[FLAG_IMG_POS], 0);
                    } else {
                        hideLoading();
                        DialogUtil.serverFailed(SliderActivity.this, "UI000802C081", "UI000802C082", "UI000802C083", "UI000802C084");
                    }

                } else {
                    hideLoading();
                    DialogUtil.serverFailed(SliderActivity.this, "UI000802C081", "UI000802C082", "UI000802C083", "UI000802C081");
                }
            }

            @Override
            public void onFailure(Call<TutorialResponse> call, Throwable t) {
                hideLoading();
                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
//                    DialogUtil.offlineDialog(SliderActivity.this, getApplicationContext());
                } else {
                    DialogUtil.serverFailed(SliderActivity.this, "UI000802C081", "UI000802C082", "UI000802C083", "UI000802C084");
                }
            }
        });
    }

    public void saveTutorialData(TutorialDeviceResponse response, int type) {
        String iurl;
        int iimgtype, iimgid;
        switch (type) {
            case 0:
                iurl = response.getData().getHome1();
                iimgtype = 0;
                iimgid = 1;
                break;
            case 1:
                iurl = response.getData().getHome2();
                iimgtype = 0;
                iimgid = 2;
                break;
            case 2:
                iurl = response.getData().getHome3();
                iimgtype = 0;
                iimgid = 3;
                break;
            case 3:
                iurl = response.getData().getRemote1();
                iimgtype = 1;
                iimgid = 1;
                break;
            case 4:
                iurl = response.getData().getRemote2();
                iimgtype = 1;
                iimgid = 2;
                break;
            case 5:
                iurl = response.getData().getRemote3();
                iimgtype = 1;
                iimgid = 3;
                break;
            case 6:
                iurl = response.getData().getRemote4();
                iimgtype = 1;
                iimgid = 4;
                break;
            case 7:
                iurl = response.getData().getRemote4();
                iimgtype = 1;
                iimgid = 5;
                break;
            default:
                iurl = response.getData().getHome1();
                iimgtype = 0;
                iimgid = 1;
                break;

        }

        int finalIimgtype = iimgtype;
        int finalIimgid = iimgid;
        String finalIurl = iurl;

        Glide.with(SliderActivity.this)
                .asBitmap()
                .load(iurl)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        hideLoading();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (resource == null) return;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] imageByte = stream.toByteArray();
//                        resource.recycle();

                        //region Save Data To Database
                        TutorialImageModel home = new TutorialImageModel();
                        home.setTutorialId(UUID.randomUUID().toString());
                        home.setDevice(response.getDevice());
                        home.setImageType(finalIimgtype);
                        home.setImageId(finalIimgid);
                        home.setImageUrl(finalIurl);
                        home.setImageBytes(imageByte);
                        home.insert();
                        //endregion
                        if (type < 7) {
                            saveTutorialData(response, type + 1);
                        } else {
//                            FLAG_IMG_POS++;
//                            if (FLAG_IMG_POS < FLAG_IMG_NUM) {
//                                saveTutorialData(data[FLAG_IMG_POS], 0);
//                            } else {
                            hideLoading();
//                            }
                        }
                    }
                });
    }
    //endregion Tutorial : Last Update Angga Fachri Hamdani @ 2018-11-24

    public void setLanguage() {
        btnLogin.setText(LanguageProvider.getLanguage("UI000200C001"));
        btnSignup.setText(LanguageProvider.getLanguage("UI000200C003"));
        btnSignupCompany.setText(LanguageProvider.getLanguage("UI000200C002"));
    }

    public boolean checkIfUserExist() {
        if (StatusLogin.getUserLogin() != null) {
            return true;
        }
        return false;
    }

    public void clearExistingUser() {
//        UserLogin.clear();
//        ValidationPhoneModel.clear();
//        ValidationEmailModel.clear();
//        AnswerResult.clear();
//        UserLogin.init();
//        StatusLogin.clear();
//        TutorialShowModel.clear();
    }

    public void doSignUpCompany(boolean isClearing) {
        String message = LanguageProvider.getLanguage("UI000200C005");
        String title = "";
        DialogUtil.createCustomYesNo(SliderActivity.this, title, message, LanguageProvider.getLanguage("UI000200C006"), (dialogInterface, i) -> {
            dialogInterface.dismiss();
        }, LanguageProvider.getLanguage("UI000200C007"), (dialogInterface, i) -> {
            dialogInterface.dismiss();
            if (isNetworkConnected(this)) {
                Intent welcomeIntent = new Intent(SliderActivity.this, CompanyRegistrationActivity.class);
                welcomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                welcomeIntent.putExtra(IntentUtil.User.IS_KICK_USER, isClearing);
                SliderActivity.this.startActivity(welcomeIntent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                RegistrationStepActivity.IS_COMPANY_REGISTER = 1;
            } else {
                DialogUtil.createSimpleOkDialog(this,
                        "",
                        LanguageProvider.getLanguage("UI000200C009"),
                        LanguageProvider.getLanguage("UI000200C010"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });
    }

    public void doSignUpPersonal(boolean isClearing) {
        String message = LanguageProvider.getLanguage("UI000200C005");
//            String title = LanguageProvider.getLanguage("UI000200C004");
        String title = "";
        DialogUtil.createCustomYesNo(SliderActivity.this, title, message, LanguageProvider.getLanguage("UI000200C006"), (dialogInterface, i) -> {
            dialogInterface.dismiss();
        }, LanguageProvider.getLanguage("UI000200C007"), (dialogInterface, i) -> {
            dialogInterface.dismiss();
            if (isNetworkConnected(this)) {
                Intent welcomeIntent = new Intent(SliderActivity.this, TncActivity.class);
                welcomeIntent.putExtra("companyId", 0);
                welcomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                welcomeIntent.putExtra(IntentUtil.User.IS_KICK_USER, isClearing);
                SliderActivity.this.startActivity(welcomeIntent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                RegistrationStepActivity.IS_COMPANY_REGISTER = 0;
                UserRegistrationModel.clear();
            } else {
                DialogUtil.createSimpleOkDialog(this,
                        "",
                        LanguageProvider.getLanguage("UI000200C009"),
                        LanguageProvider.getLanguage("UI000200C010"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }

        });
    }

    private boolean isSupportedScreen(Context context) {
        return DisplayUtils.SCREEN.isSupportedScreen(context, new DisplayUtils.SCREEN.SupportScreenListener() {
            @Override
            public void isHD(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isHD2(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isHD2Plus(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isWXGA(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isHDPlus(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD2(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isFHDPlus(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD3(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD4(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD5(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD6(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isQHD(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isQHD2(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isWQHDPlus(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isWQHD2Plus(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isWQHD3(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isWQHD4(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isUHDMin(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isQHDPlus(DisplayUtils.DisplayProperty displayProperty) {

            }

            @Override
            public void isUHD(DisplayUtils.DisplayProperty displayProperty) {

            }
        });
    }
}
