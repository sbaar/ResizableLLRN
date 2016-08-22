package com.makemojireactnative;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.makemojireactnative.R;

/**
 * Created by Scott Baar on 1/19/2016.
 */
public class ResizeableLL  extends LinearLayout implements View.OnTouchListener{
    public ResizeableLL(Context context) {
        this(context, null);
        init();
    }

    public ResizeableLL(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ResizeableLL(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init();
    }
    void init(){
        Log.d(VIEW_LOG_TAG,"init");
        setOrientation(HORIZONTAL);
        inflate(getContext(), R.layout.resizable,this);

        leftView = findViewById(R.id.left_buttons);
        recyclerView = (RecyclerView) findViewById(R.id.recylcer_view);

        minSize = (int)(50 *density);

        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        setPrimaryContentWidth(150);
        setOnTouchListener(this);
    }



    float density = 3;
    View leftView;
    RecyclerView recyclerView;
    int minSize, maxSize;
    int expandSizeThreshold =(int) (100 * density);//bigger than this  after drag -> expand
    boolean lastStateOpened = false; //affinity for snap behavior open or close

    int mTouchSlop;
    ImageView flashButton;
    boolean enableScroll = true;
    boolean showLeft = true;
    public void setEnableScroll(boolean enable){
        enableScroll = enable;
    }
    public void setShowLeft(boolean show){
        showLeft = show;
        if (leftView!=null) leftView.setVisibility(show?VISIBLE:GONE);

    }
    void setMaxSize(int max){
        maxSize = max - (int)(density *45);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        //leftView.setOnTouchListener(this);

      //  flashButton = (ImageView) findViewById(R.id._mm_flashtag_button);

    }
    @Override
    public void onSizeChanged (int w,
                        int h,
                        int oldw,
                        int oldh){
        if (w>maxSize)maxSize = w-150;
    }
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (changedView!=this)return;
        if (visibility==View.VISIBLE)
            maxSize = getWidth() - 150;
    }


    public synchronized void jiggle(){
        if (!enableScroll |!showLeft) return;
        ValueAnimator animator = ValueAnimator.ofInt(minSize + (int)(60 * density),minSize);
        animator.setInterpolator(new OvershootInterpolator());
        animator.setDuration(1000);
        animator.setStartDelay(100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!mDragging)
                    setPrimaryContentWidth((int)animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    private boolean mDragging;
    private float mDragStartX;

    final private int MAXIMIZED_VIEW_TOLERANCE_DIP = (int)(30 * density);
    final private int MINIMIZED_VIEW_TOLERANCE_DIP = (int)(70 * density);

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (!enableScroll |!showLeft) return false;
        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll.
            mDragging = false;
            return false; // Do not intercept touch event, let the child handle it
        }
        else if (action==MotionEvent.ACTION_DOWN){
            mDragStartX = ev.getRawX();
            mStartWidth = leftView.getMeasuredWidth();
        }
        else if (action==MotionEvent.ACTION_MOVE){
            if (mDragging) return true;
            final int xDiff = Math.abs((int)(mDragStartX - ev.getRawX()));

            boolean rvhandled = rvHandlesMotion(ev);
            if (xDiff > mTouchSlop && !rvhandled) {
                // Start scrolling!
                if (mDragStartX>ev.getRawX()) mDragStartX-=mTouchSlop;//adjust for slop to remove jank on scroll start
                else if (mDragStartX<ev.getRawX()) mDragStartX+=mTouchSlop;
                mDragging = true;
                return true;
            }
        }
        return false;

    }
    //if true, recycler view will handle the motion.
    boolean canScrollLeft,canScrollRight;
    float newX;
    private boolean rvHandlesMotion(MotionEvent ev){
        newX = ev.getRawX();
        int[] l = new int[2];
        recyclerView.getLocationOnScreen(l);
        int x = l[0];
        int w = recyclerView.getWidth();
        if ((ev.getRawX()< x || ev.getRawX()> x + w ))
            return false;
        canScrollLeft = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition()!=0;

        if (newX<mDragStartX)//drag right
            return true;
        if (newX>mDragStartX && canScrollLeft)
            return true;

        return false;

    }

    int mStartWidth;
    @Override
    public boolean onTouch(View view, MotionEvent me) {
        int nw = mStartWidth - (int)(mDragStartX - me.getRawX());
        Log.d(VIEW_LOG_TAG,"touch");
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            mDragStartX = me.getRawX();
            mStartWidth = leftView.getMeasuredWidth();
            mDragging=true;
            return true;
        }
        else if ((me.getAction() == MotionEvent.ACTION_UP || me.getAction()==MotionEvent.ACTION_CANCEL) && mDragging) {
            mDragging = false;
            snapOpenOrClose(nw);
            return true;

        }
        else if (me.getAction() == MotionEvent.ACTION_MOVE && mDragging) {
            setPrimaryContentWidth(nw);
        }


        return false;
    }

    private boolean setPrimaryContentWidth(int newWidth) {
        if (!enableScroll |!showLeft) return false;
        newWidth=  Math.max((int)(minSize *.5),newWidth);
        if (maxSize>minSize)newWidth = Math.min(maxSize,newWidth);// clamp >--(◣_◢)--<
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) leftView.getLayoutParams();
        //Log.d("ll","width " + newWidth);
        lp.width=newWidth;
        leftView.setLayoutParams(lp);
        leftView.requestLayout();
        Log.d(VIEW_LOG_TAG,"requestLayout");

        //change snap affinity when the open or close is done.
        if (maxSize == newWidth)
            lastStateOpened = true;
        if (minSize == newWidth)
            lastStateOpened = false;

        return true;
    }

    ValueAnimator animator;
    public void snapOpen(){
        if (!enableScroll |!showLeft) return;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) leftView.getLayoutParams();
        int currentWidth = lp.width;
        if (currentWidth==minSize)return;
        animator = ValueAnimator.ofInt(currentWidth,minSize);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setPrimaryContentWidth((int)animation.getAnimatedValue());
            }
        });
        animator.start();
    }
    private void snapOpenOrClose(int currentWidth){
        if (!enableScroll |!showLeft) return;
        int goalWidth = maxSize;
        if ((lastStateOpened && maxSize-currentWidth>MAXIMIZED_VIEW_TOLERANCE_DIP)//closed enough to snap close
                || (!lastStateOpened && minSize+MINIMIZED_VIEW_TOLERANCE_DIP>currentWidth))//not open enough to snap open.
            goalWidth = minSize;


        animator = ValueAnimator.ofInt(currentWidth,goalWidth);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        if (goalWidth==minSize)
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setPrimaryContentWidth((int)animation.getAnimatedValue());
            }
        });
        animator.start();

    }


}
