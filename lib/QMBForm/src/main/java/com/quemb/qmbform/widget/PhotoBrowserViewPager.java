package com.quemb.qmbform.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by alvinzeng on 4/15/16.
 */
public class PhotoBrowserViewPager extends ViewPager {
    private int mode = 0;

    public PhotoBrowserViewPager(Context context) {
        super(context);
    }

    public PhotoBrowserViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 修复 java.lang.IllegalArgumentException: pointerIndex out of range pointerIndex=-1 pointerCount=1  bug
     * 传说此bug是系统bug
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = 1;
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode += 1;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (mode >= 2) {
                    return false;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
