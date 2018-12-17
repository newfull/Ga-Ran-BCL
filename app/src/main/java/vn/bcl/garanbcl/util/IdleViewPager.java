package vn.bcl.garanbcl.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class IdleViewPager extends ViewPager {

        public IdleViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            return false;
        }
}
