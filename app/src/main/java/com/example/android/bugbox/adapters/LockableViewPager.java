package com.example.android.bugbox.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

//class copied from https://stackoverflow.com/questions/41656902/disable-swipe-in-fragmentpageradapter-android

public class LockableViewPager extends ViewPager {
    private boolean swipeable;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipeable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.swipeable) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.swipeable) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    public void setSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
    }
}