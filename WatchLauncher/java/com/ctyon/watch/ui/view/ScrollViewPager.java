package com.ctyon.watch.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ctyon.watch.R;

/**
 * Created by Administrator on 2018/1/25.
 */

public class ScrollViewPager extends ViewPager {

    private boolean isVertical = false;
    private boolean noScroll = true;
    public ScrollViewPager(Context context) {
        super(context);
        init();
    }

    public ScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
        init();
    }

    public ScrollViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initAttrs(attrs, defStyle);
        init();
    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalVerticalViewPager, defStyle, 0);
        isVertical = a.getBoolean(R.styleable.HorizontalVerticalViewPager_isVertical, false);
        a.recycle();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
    }


    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
        init();
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }


    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (noScroll)
            return false;
        if (isVertical) {
            boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
            //swapXY(ev); // return touch coordinates to original reference frame for any child views
            return intercepted;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (noScroll)
            return false;
        if (isVertical) {
            return super.onTouchEvent(swapXY(ev));
        } else {
            return super.onTouchEvent(ev);
        }
    }
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        //去除页面切换时的滑动翻页效果
        super.setCurrentItem(item, false);
    }

}
