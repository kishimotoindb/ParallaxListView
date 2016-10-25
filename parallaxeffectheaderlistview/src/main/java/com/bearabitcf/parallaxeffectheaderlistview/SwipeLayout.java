package com.bearabitcf.parallaxeffectheaderlistview;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by bearabit on 2016/9/10 10:23.
 */
public class SwipeLayout extends RelativeLayout {
    private static final String TAG = "SwipeLayout";
    private ViewDragHelper helper;
    private ViewGroup mFrontView;
    private ViewGroup mBackView;
    private int mBackViewMeasuredWidth;
    private int mDragXSlop;
    private boolean mStart;
    private final int mXvelSlop = 400;

    private enum FrontState {
        OPEN, CLOSE, SMOOTHING
    }

    private FrontState mFrontState = FrontState.CLOSE;

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        helper = ViewDragHelper.create(this, new MyHelperCallBack());
        mDragXSlop = Utils.dp2px(getContext(), 40);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFrontView = (ViewGroup) getChildAt(1);
        mBackView = (ViewGroup) getChildAt(0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBackViewMeasuredWidth = mBackView.getMeasuredWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Utils.logEventDispatch(TAG, "onTouchEvent", event);
        //因为在clampViewPositionHorizontal中要使用down事件child的起始坐标，所以在这里要提供一个起始状态变量
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mStart = true;
        }
        helper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Utils.logEventDispatch(TAG, "onInterceptTouchEvent", ev);
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Utils.logEventDispatch(TAG, "dispatchTouchEvent", ev);
        return super.dispatchTouchEvent(ev);
    }

    private class MyHelperCallBack extends ViewDragHelper.Callback {

        private int mStartX;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mFrontView ? true : false;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 用户在移动了一定距离之后才能滑开item，这样的话，listview在刚开始上下滑动的时候
            // 就不会出现item也发生左右移动的问题。
            if (mStart) {
                mStart = false;
                mStartX = child.getLeft();
            }

            mStartX += dx;
            int leftDelay = 0;
            if (mStartX > -mDragXSlop) {
                leftDelay = 0;
            } else if (mStartX < -mBackViewMeasuredWidth) {
                leftDelay = -mBackViewMeasuredWidth;
            } else {
                leftDelay = mStartX;
            }
            return leftDelay;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //如果打开，就将backview显示出来
            if (mFrontState == FrontState.CLOSE && left < 0) {
                mFrontState = FrontState.SMOOTHING;
            } else if (mFrontState == FrontState.SMOOTHING && left == 0) {
                //如果关闭，就将backView隐藏起来
                mFrontState = FrontState.CLOSE;
            } else if (mFrontState == FrontState.SMOOTHING && left == -mBackViewMeasuredWidth) {
                mFrontState = FrontState.OPEN;
            }
        }

        @Override
        //松手的时候即使手指有轻微滑动，也会被速度阈值过滤掉，所以静止状态下离开屏幕xvel就是0.（已打印log证实）
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Utils.log(TAG, "xvel is " + xvel);
            //根据速度和位置将frontView移动到两端
            if (releasedChild == mFrontView) {
                int releaseLeft = releasedChild.getLeft();
                //速度<0，打开
                if (xvel < -mXvelSlop) {
                    open(releasedChild);
                } else if (xvel > mXvelSlop) {
                    //速度>mXvelSlop，关闭
                    close(releasedChild);
                } else {
                    //速度=mXvelSlop，根据frontView的当前位置决定打开还是关闭
                    if (releaseLeft < -mBackViewMeasuredWidth / 2.0f) {
                        open(releasedChild);
                    } else {
                        close(releasedChild);
                    }
                }
            }
        }

    }

    private void open(View releasedChild) {
        //将FrontView从当前位置自动滑动到左端
        helper.smoothSlideViewTo(releasedChild, -mBackViewMeasuredWidth, 0);
        invalidate();
    }

    private void close(View releasedChild) {
        //将FrontView从当前位置自动滑动到右端
        helper.smoothSlideViewTo(releasedChild, 0, 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (helper.continueSettling(true)) {
            invalidate();
        }
    }

    private void changeFrontState(FrontState state) {
        switch (state) {
            case OPEN:
                mFrontState = FrontState.OPEN;
                break;
            case CLOSE:
                mFrontState = FrontState.CLOSE;
                break;
            case SMOOTHING:
                mFrontState = FrontState.SMOOTHING;
                break;
        }
    }

}
