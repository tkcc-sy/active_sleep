package com.paramount.bed.ui.main;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.paramount.bed.R;
import com.paramount.bed.ui.BaseFragment;

import static com.paramount.bed.util.LogUtil.Logx;

public class TutorialFragment extends BaseFragment {
    private Integer imageId = 1;
    private Bitmap myBitmap;
    private Integer type = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        ImageView imageTutorial = view.findViewById(R.id.image_tutorial);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.parent_fragment);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width = layout.getMeasuredWidth();
                int height = layout.getMeasuredHeight();
                Logx("WIDTH-90 ->",String.valueOf(width));
                width = (int) ((double) width * 0.9);
                height = (int) ((double) height * 1);
                imageTutorial.getLayoutParams().width = width;
                imageTutorial.getLayoutParams().height = height;
                imageTutorial.requestLayout();
            }
        });

        int DRAWABLE_TUTORIAL = 0;
        if (type == 0 && imageId == 1) {
            DRAWABLE_TUTORIAL = R.drawable.home1;
        } else if (type == 0 && imageId == 2) {
            DRAWABLE_TUTORIAL = R.drawable.home2;
        } else if (type == 0 && imageId == 3) {
            DRAWABLE_TUTORIAL = R.drawable.home3;
        } else if (type == 0 && imageId == 4) {
            DRAWABLE_TUTORIAL = R.drawable.home4;
        } else if (type == 1 && imageId == 1) {
            DRAWABLE_TUTORIAL = R.drawable.asb1;
        } else if (type == 1 && imageId == 2) {
            DRAWABLE_TUTORIAL = R.drawable.asb2;
        } else if (type == 1 && imageId == 3) {
            DRAWABLE_TUTORIAL = R.drawable.asb3;
        } else if (type == 1 && imageId == 4) {
            DRAWABLE_TUTORIAL = R.drawable.mat1;
        } else if (type == 1 && imageId == 5) {
            DRAWABLE_TUTORIAL = R.drawable.mat2;
        } else if (type == 2 && imageId == 1) {
            DRAWABLE_TUTORIAL = R.drawable.intime1;
        } else if (type == 2 && imageId == 2) {
            DRAWABLE_TUTORIAL = R.drawable.intime2;
        } else if (type == 2 && imageId == 3) {
            DRAWABLE_TUTORIAL = R.drawable.intime3;
        } else if (type == 2 && imageId == 4) {
            DRAWABLE_TUTORIAL = R.drawable.mat1;
        } else if (type == 2 && imageId == 5) {
            DRAWABLE_TUTORIAL = R.drawable.mat2;
        } else if (type == 3 && imageId == 1) {
            DRAWABLE_TUTORIAL = R.drawable.mat1;
        } else if (type == 3 && imageId == 2) {
            DRAWABLE_TUTORIAL = R.drawable.mat2;
        } else {
            DRAWABLE_TUTORIAL = R.drawable.home1;
        }

        imageTutorial.setImageResource(DRAWABLE_TUTORIAL);

        return view;
    }

    public void setImageList(Integer integer) {
        this.imageId = integer;
    }

    public void setTypeList(Integer integer) {
        this.type = integer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myBitmap != null) {
            myBitmap.recycle();
            myBitmap = null;
        }
    }
}
