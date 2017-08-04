package cn.etsoft.smarthomephone.weidget;

/**
 * Created by Say GoBay on 2016/8/30.
 */

import android.support.v4.widget.ScrollerCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;


public class SwipeItemLayout extends FrameLayout {
    //这个是内容的item，也就是不左滑的时候的布局
    private View contentView = null;
    //这个是左滑之后显示的那个部分，即多出的部分
    private View menuView = null;
    //这个是动画的速度控制器，其实没用到
    private Interpolator closeInterpolator = null;
    private Interpolator openInterpolator = null;
    //控制控件滑动的，会平滑滑动，一个开一个关
    private ScrollerCompat mOpenScroller;
    private ScrollerCompat mCloseScroller;
    //左滑之后，contentView左边距离屏幕左边的距离，基线，用于滑回
    private int mBaseX;
    //手指点击的初始位置
    private int mDownX;
    //当前item的状态，open和close两种
    private int state = STATE_CLOSE;

    private static final int STATE_CLOSE = 0;
    private static final int STATE_OPEN = 1;
    //构造函数
    public SwipeItemLayout(View contentView,View menuView,Interpolator closeInterpolator, Interpolator openInterpolator){
        super(contentView.getContext());
        this.contentView = contentView;
        this.menuView = menuView;
        this.closeInterpolator = closeInterpolator;
        this.openInterpolator = openInterpolator;

        init();
    }

    private void init(){
        //设置一个item的宽和高，其实就是设置宽充满而已
        setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        //初始化mCloseScroller和mOpenScroller
        if (closeInterpolator != null) {
            mCloseScroller = ScrollerCompat.create(getContext(),
                    closeInterpolator);
        } else {
            mCloseScroller = ScrollerCompat.create(getContext());
        }
        if (openInterpolator != null) {
            mOpenScroller = ScrollerCompat.create(getContext(),
                    openInterpolator);
        } else {
            mOpenScroller = ScrollerCompat.create(getContext());
        }
        //这也是设置宽和高
        LayoutParams contentParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(contentParams);

        menuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        //将这两个布局都add到这个view中
        addView(contentView);
        addView(menuView);
    }
    //这个类就是当用户在界面上滑动的时候，通过ListView的onTouch方法，将MotionEvent的动作传到这里来，通过这个函数执行操作
    public boolean onSwipe(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录当前手指点击的x的坐标
                mDownX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                // Log.i("byz", "downX = " + mDownX + ", moveX = " + event.getX());
                //当手指移动的时候，获取这个差值
                int dis = (int) (mDownX - event.getX());
                //这个地方，当状态是open的时候，为啥要执行 += 这个操作，我们在下面就会找到答案的
                if (state == STATE_OPEN) {
                    dis += menuView.getWidth();
                }
                //这个函数在下面说
                swipe(dis);
                break;
            case MotionEvent.ACTION_UP:
                //这里其实是一个判断，当用户滑了menuView的一半的时候，自动滑出来，否则滑进去
                if ((mDownX - event.getX()) > (menuView.getWidth() / 2)) {
                    // open
                    // 平滑的滑出
                    smoothOpenMenu();
                } else {
                    // close
                    // 平滑的滑进
                    smoothCloseMenu();
                    return false;
                }
                break;
        }
        //这个地方一定要return true，才能保证这个动作不会继续往下传递
        return true;
    }
    // 判断是否滑出的状态
    public boolean isOpen() {
        return state == STATE_OPEN;
    }
    //这个方法就是滑动dis的距离，还记得那个 += 吗，如果dis > menuView.getWidth()的 话，dis = menuView.getWidth().这样，当滑到最大限度的时候，就不会滑动了
    private void swipe(int dis) {
        if (dis > menuView.getWidth()) {
            dis = menuView.getWidth();
        }
        if (dis < 0) {
            dis = 0;
        }
        // layout的四个参数分别是(l,t,r,b),这样实现contentView的移动
        contentView.layout(-dis, contentView.getTop(),
                contentView.getWidth() - dis, getMeasuredHeight());
        menuView.layout(contentView.getWidth() - dis, menuView.getTop(),
                contentView.getWidth() + menuView.getWidth() - dis,
                menuView.getBottom());
    }
    //这个方法是系统的方法，就是执行一个刷新而已
    @Override
    public void computeScroll() {
        if (state == STATE_OPEN) {
            if (mOpenScroller.computeScrollOffset()) {
                swipe(mOpenScroller.getCurrX());
                postInvalidate();
            }
        } else {
            if (mCloseScroller.computeScrollOffset()) {
                swipe(mBaseX - mCloseScroller.getCurrX());
                postInvalidate();
            }
        }
    }

    public void smoothCloseMenu() {
        state = STATE_CLOSE;
        mBaseX = -contentView.getLeft();
        System.out.println(mBaseX);
        mCloseScroller.startScroll(0, 0, mBaseX, 0, 350);
        postInvalidate();
    }

    public void smoothOpenMenu() {
        state = STATE_OPEN;
        mOpenScroller.startScroll(-contentView.getLeft(), 0,
                menuView.getWidth(), 0, 350);
        postInvalidate();
    }
    public void closeMenu() {
        if (mCloseScroller.computeScrollOffset()) {
            mCloseScroller.abortAnimation();
        }
        if (state == STATE_OPEN) {
            state = STATE_CLOSE;
            swipe(0);
        }
    }

    public void openMenu() {
        if (state == STATE_CLOSE) {
            state = STATE_OPEN;
            swipe(menuView.getWidth());
        }
    }

    public View getContentView() {
        return contentView;
    }

    public View getMenuView() {
        return menuView;
    }
    //这个方法 其实就是获取menuView的宽和高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        menuView.measure(MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight(), MeasureSpec.EXACTLY));
    }
    //这个方法就把两个控件的相对布局表现出来了
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        contentView.layout(0, 0, getMeasuredWidth(),
                contentView.getMeasuredHeight());
        menuView.layout(getMeasuredWidth(), 0,
                getMeasuredWidth() + menuView.getMeasuredWidth(),
                contentView.getMeasuredHeight());
        // setMenuHeight(mContentView.getMeasuredHeight());
        // bringChildToFront(mContentView);
    }
}

