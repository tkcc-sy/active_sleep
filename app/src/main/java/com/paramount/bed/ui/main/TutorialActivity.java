package com.paramount.bed.ui.main;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.TutorialImageModel;
import com.paramount.bed.data.model.TutorialShowModel;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import org.greenrobot.eventbus.EventBus;

public class TutorialActivity extends BaseActivity implements
        View.OnClickListener {

    private ImageView imageNext, imageFinish;
    private int position = 0, totalImage;
    public static TutorialFragment tutorialFragment;
    public static int TYPE = 0;
    TutorialActivity context;
    public static Boolean isOnhold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOnhold = false;
        context = this;
        imageNext = (ImageView) findViewById(R.id.image_next);
        imageFinish = (ImageView) findViewById(R.id.image_finish);
        imageNext.setOnClickListener(this);
        imageFinish.setOnClickListener(this);
        loadTutorial();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_tutorial;
    }

    private void setPage(int page) {
        if (page == 0 && totalImage > 0) {
            imageNext.setVisibility(View.VISIBLE);
            imageFinish.setVisibility(View.GONE);
        } else if (page == totalImage - 1 && totalImage > 0) {
            imageNext.setVisibility(View.GONE);
            imageFinish.setVisibility(View.VISIBLE);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_holder, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onClick(View v) {

        if (!isOnhold) {
            isOnhold = true;
            if (v == imageFinish) {
                finishTutorial();
                isOnhold = false;
            } else if (v == imageNext) {
                if ((TYPE == 0 && position >= 0 && position < 4) || (TYPE == 1 && position >= 0 && position < 5) || (TYPE == 2 && position >= 0 && position < 5) || (TYPE == 3 && position >= 0 && position < 2)) {
                    position++;
                    if ((TYPE == 0 && position >= 1 && position < 4) || (TYPE == 1 && position >= 1 && position < 5) || (TYPE == 2 && position >= 1 && position < 5) || (TYPE == 3 && position >= 1 && position < 2)) {
                        tutorialFragment = new TutorialFragment();
                        tutorialFragment.setTypeList(TYPE);
                        tutorialFragment.setImageList(position + 1);
                        replaceFragment(tutorialFragment);
                        setPage(position);
                    }
                }
                isOnhold = false;

            }
        }
    }

    public void loadTutorial() {
        int type = getIntent().getIntExtra("type", 0);
        TYPE = type;
        switch (TYPE) {
            case 0:
                totalImage = TutorialImageModel.getImageByDeviceType("6P", 0).size();
                totalImage = 4;
                break;
            case 1:
                totalImage = TutorialImageModel.getImageByDeviceType("6P", 1).size();
                totalImage = 5;
                break;
            case 2:
                totalImage = TutorialImageModel.getImageByDeviceType("6P", 1).size();
                totalImage = 5;
                break;
            case 3:
                totalImage = 2;
                break;
            default:
                totalImage = 0;
        }
        if (totalImage > 0) {
            setPage(0);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            tutorialFragment = new TutorialFragment();
            tutorialFragment.setTypeList(TYPE);
            ft.replace(R.id.fragment_holder, tutorialFragment);
            ft.commit();
        } else {
            resetFlags();
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void finishTutorial() {
        resetFlags();
        boolean isRemoteTutorial = getIntent().getBooleanExtra("isTutorialRemote", false);
        if (isRemoteTutorial) {
            Intent intent = new Intent(TutorialActivity.this, RemoteActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }else{
            //make an eventbus broadcast for non remote tutorial
            EventBus.getDefault().post(new HomeActivity.TutorialFinsihedEvent());
        }
        setResult(1);//arbitrary value just to notify HomeActivity
        finish();
    }

    private void resetFlags(){
        int type = getIntent().getIntExtra("type", 0);

        TutorialShowModel.clear();
        TutorialShowModel tutorialShowModel = new TutorialShowModel();
        tutorialShowModel.setBedShowed(false);
        if(type == 1 || type == 2 || type == 3) {
            tutorialShowModel.setRemoteShowed(false);
        }
        tutorialShowModel.insert();
    }
}
