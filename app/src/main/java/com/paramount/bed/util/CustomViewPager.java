package com.paramount.bed.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by github/fiyyanputra on 12/21/2018.
 */

public class CustomViewPager extends ViewPager {
    private boolean swipeLocked;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean getSwipeLocked() {
        return swipeLocked;
    }

    public void setSwipeLocked(boolean swipeLocked) {
        this.swipeLocked = swipeLocked;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return !getSwipeLocked() && super.onTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return !getSwipeLocked() && super.onInterceptTouchEvent(event);
        }catch (Exception e){
            e.printStackTrace();
            return !getSwipeLocked();
        }
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return !getSwipeLocked() && super.canScrollHorizontally(direction);
    }
}
