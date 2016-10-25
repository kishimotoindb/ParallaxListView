package com.bearabitcf.parallaxeffectheaderlistview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by bearabit on 2016/9/10 19:10.
 */
public class ParallaxEffectHeaderListView extends ListView {
    private static final String TAG = "ParallaxEffectHeaderListView";
    private ImageView mHeaderImageView;
    private float mHeightScale = 0.8f;
    private int mHeaderDefaultHeight;
    private ValueAnimator mHeaderBackAnimator;

    public ParallaxEffectHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //将ImageView对象加入ListView的header
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        mHeaderImageView = new ImageView(getContext());
        mHeaderImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        mHeaderImageView.setLayoutParams(lp);
        relativeLayout.addView(mHeaderImageView);
        addHeaderView(relativeLayout);
    }

    public void setHeaderImageByResId(int resId) {
        //获取图片的原始高
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, opts);
        int imageIntrinsicHeight = opts.outHeight;

        //设置ImageView的布局参数
        mHeaderDefaultHeight = (int) (imageIntrinsicHeight * mHeightScale);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(MATCH_PARENT, mHeaderDefaultHeight);
        mHeaderImageView.setLayoutParams(lp);

        //设置图片
        mHeaderImageView.setImageResource(resId);
    }

    @Override
    // TODO: 2016/9/11 16:10 尝试重写onOverScroll()方法，将View提供的overScrollBy()方法看懂
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
      /*  Log.i(TAG, "overScrollBy: deltaY " + deltaY);
        Log.i(TAG, "overScrollBy: scrollY " + scrollY);
        Log.i(TAG, "overScrollBy: scrollRangeY " + scrollRangeY);
        Log.i(TAG, "overScrollBy: maxOverScrollY " + maxOverScrollY);
        Log.i(TAG, "overScrollBy: isTouchEvent " + isTouchEvent);*/
        //列表已经到顶部，并且是手指在往下拉，而不是之前的手指滑动惯性导致列表滑动到顶端，展开图片
        if (isTouchEvent) {
            if (deltaY < 0) {
                if (mHeaderImageView.getDrawable() == null) {
                    throw new RuntimeException("还没有设置图片");
                }

                int oldHeight = mHeaderImageView.getLayoutParams().height;
                int newHeight = oldHeight += Math.abs(deltaY);
                float magnification = 1.4f;
                if (newHeight > mHeaderDefaultHeight * magnification) {
                    newHeight = (int) (mHeaderDefaultHeight * magnification);
                }
                mHeaderImageView.getLayoutParams().height = newHeight;
                mHeaderImageView.requestLayout();
            }
        }
        // TODO: 2016/9/11 16:11 这里的返回值有什么作用
        return true;
//        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Utils.logEventDispatch("parallax", "dispatchTouchEvent", ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Utils.logEventDispatch("parallax", "onInterceptTouchEvent", ev);
        int actionMasked = ev.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                if (mHeaderBackAnimator != null) {
                    if (mHeaderBackAnimator.isRunning()) {
                        mHeaderBackAnimator.cancel();
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Utils.logEventDispatch("parallax", "onTouchEvent", ev);
        int actionMasked = ev.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_UP:
                if (mHeaderImageView.getLayoutParams().height != mHeaderDefaultHeight) {
                    excuteBackAnimation();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void excuteBackAnimation() {
        mHeaderBackAnimator = ValueAnimator.ofInt(mHeaderImageView.getLayoutParams().height, mHeaderDefaultHeight);
        mHeaderBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mHeaderImageView.getLayoutParams().height = (int) animation.getAnimatedValue();
                mHeaderImageView.requestLayout();
            }
        });
        mHeaderBackAnimator.setDuration(2000);
        mHeaderBackAnimator.setInterpolator(new OvershootInterpolator(5));
        mHeaderBackAnimator.start();
    }
}
