package com.example.overscroll.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Scroller;

public class ListViewEx extends ListView implements OnScrollListener {

    private float lastX;
    private float lastY;
    private final static float RATIO = 2.25f;
    private int startY;
    private int state = 0;

    private boolean isRecored;

    private Scroller mScroller;

    public static final int STATUS_FOOTER_IDLE = 0;
    public static final int STATUS_FOOTER_LOADING = 1;
    public static final int STATUS_FOOTER_DONE = 2;
    public static final int STATUS_FOOTER_NET_ERROR = 3;
    public static final int STATUS_FOOTER_REFRESHING = 4;
    private int footerStatus = STATUS_FOOTER_IDLE;

    private int lastPos, totalCount;
    private int height;
    private int paddingTop;
    private int paddingBottom;

    private float lastDx;
    private float lastDy;

    private boolean isBottom = false;
    private boolean isTop = true;

    public int getFooterStatus() {
        return footerStatus;
    }

    public void setFooterStatus(int footerStatus) {
        this.footerStatus = footerStatus;
    }

    public int getState() {
        return state;
    }

    public ListViewEx(Context context) {
        this(context, true);
    }

    public ListViewEx(Context context, boolean isEnabled) {
        super(context);
        init(context);
    }

    public ListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(getContext());
        setOnScrollListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        super.onLayout(changed, l, t, r, b);
        height = getHeight();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {

        if (firstVisibleItem == 0) {
            isTop = true;
        } else {
            isTop = false;
        }

        lastPos = view.getLastVisiblePosition();
        totalCount = totalItemCount;
        if (lastPos == totalCount - 1) {
            isBottom = true;

        } else {
            isBottom = false;
        }

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDx = x;
                lastDy = y;

                break;
            case MotionEvent.ACTION_MOVE: {
                final float deltaX = Math.abs(x - lastDx);
                final float deltaY = Math.abs(y - lastDy);

                // if (deltaX > 0 && deltaX > deltaY) {
                // record = true;
                // } else {
                // record = false;
                // }
            }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isRecored = false;
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaX = Math.abs(x - lastX);
                final float deltaY = Math.abs(y - lastY);
                if (deltaX > 0 && deltaX > deltaY) {
                    return false;

                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                requestDisallowInterceptTouchEvent(false);
                break;
        }

        return super.onInterceptTouchEvent(ev);

    }

    private int distance;

    @Override
    public void computeScroll() {

        // 先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            // 这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            // 必须调用该方法，否则不一定能看到滚动效果
            invalidate();
        }
        super.computeScroll();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getChildCount() == 0) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (!isRecored) {
                    isRecored = true;
                    startY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isTop || isBottom) {
                    changeViewByState();
                }

                isRecored = false;
                state = 0;
                break;

            case MotionEvent.ACTION_MOVE:

                int tempY = (int) ev.getY();
                if (!isRecored) {
                    isRecored = true;
                    startY = tempY;
                }
                if (isBottom && isTop) {
                    if (paddingTop == getChildAt(0).getTop()) {
                        if (state != 1) {
                            state = 1;
                        }
                        distance = (int) ((tempY - startY) / RATIO);
                        smoothTo(0, -distance);

                    } else if ((height - paddingBottom) >= (getChildAt(getChildCount() - 1).getBottom())) {
                        if (state != 2) {
                            state = 2;

                        }
                        distance = (int) ((tempY - startY) / RATIO);

                        smoothTo(0, -distance);

                    }

                } else {
                    if (isTop) {

                        if (paddingTop == getChildAt(0).getTop()) {
                            if (state != 3) {

                                if (distance != 0) {
                                    smoothScrollToNormal();

                                }
                                startY = (int) ev.getY();
                                state = 3;
                            }

                            distance = (int) ((tempY - startY) / RATIO);
                            Log.v("解密jianchuanli", "distance:"+distance);
                            if (tempY - startY > 0 && Math.abs(mScroller.getCurrY()) != 0) {
                                setSelectionFromTop(0, 0);
                            }
                            if (distance >= 0) {
                                smoothTo(0, -distance);

                            } else {
                                return super.onTouchEvent(ev);
                            }

                        }

                    } else if (isBottom) {

                        if ((height - paddingBottom) >= (getChildAt(getChildCount() - 1)
                                .getBottom())) {
                            if (state != 4) {

                                if (distance != 0) {
                                    smoothScrollToNormal();

                                }
                                startY = (int) ev.getY();
                                state = 4;
                            }

                            distance = (int) ((tempY - startY) / RATIO);
                            Log.v("解密jianchuanli", "distance:"+distance);
                            if (tempY - startY < 0 && Math.abs(mScroller.getCurrY()) != 0) {
                                setSelectionFromTop(lastPos, 0);
                            }
                            if (distance <= 0) {
                                smoothTo(0, -distance);
                            } else {
                                return super.onTouchEvent(ev);
                            }

                        }

                    }
                }

                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private final void smoothScrollToNormal() {
        smoothTo(0, 0);
    }

    private void smoothTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();
    }

    private void changeViewByState() {
        smoothScrollToNormal();
        distance = 0;
    }

}
