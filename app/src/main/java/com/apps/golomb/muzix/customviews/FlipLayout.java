package com.apps.golomb.muzix.customviews;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;


/**
 * Created by tomer on 11/10/2016.
 * Flip layout that have 2 children and swap between them.
 */

public class FlipLayout extends FrameLayout implements Animation.AnimationListener{
    public static final int ANIM_DURATION_MILLIS = 500;
    private static final Interpolator fDefaultInterpolator = new DecelerateInterpolator();
    private AbstractFlipAnimation.OnFlipListener listener;
    private FlipAnimator animator;
    private boolean isFlipped;
    private View frontView, backView;

    public FlipLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public FlipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlipLayout(Context context) {
        super(context);
        init(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FlipLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        animator = new FlipAnimator();
        animator.setAnimationListener(this);
        animator.setInterpolator(fDefaultInterpolator);
        animator.setDuration(ANIM_DURATION_MILLIS);
        animator.setDirection(AbstractFlipAnimation.Direction.DOWN);
        setSoundEffectsEnabled(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() > 2) {
            throw new IllegalStateException("FlipLayout can host only two direct children");
        }

        frontView = getChildAt(0);
        backView = getChildAt(1);
        reset();
    }

    private void toggleView() {
        if (frontView == null || backView == null) {
            return;
        }

        if (isFlipped) {
            frontView.setVisibility(View.VISIBLE);
            backView.setVisibility(View.GONE);
        } else {
            frontView.setVisibility(View.GONE);
            backView.setVisibility(View.VISIBLE);
        }

        isFlipped = !isFlipped;
    }

    public void reset() {
        isFlipped = false;
        animator.setDirection(AbstractFlipAnimation.Direction.DOWN);
        frontView.setVisibility(View.VISIBLE);
        backView.setVisibility(View.GONE);
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        if(activated){
            toggleUp();
        }
        else{
            toggleDown();
        }
    }

    public void toggleUp() {
        animator.setDirection(AbstractFlipAnimation.Direction.UP);
        startAnimation();
    }

    public void toggleDown() {
        animator.setDirection(AbstractFlipAnimation.Direction.DOWN);
        startAnimation();
    }

    public void startAnimation() {
        animator.setVisibilitySwapped();
        startAnimation(animator);
    }

    @Override public void onAnimationStart(Animation animation) {
        if (listener != null) {
            listener.onFlipStart(this);
        }
    }

    @Override public void onAnimationEnd(Animation animation) {
        if (listener != null) {
            listener.onFlipEnd(this);
        }
        animator.flipDirection();
    }

    @Override public void onAnimationRepeat(Animation animation) {
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        animator.setAnimationListener(listener);
    }

    private class FlipAnimator extends AbstractFlipAnimation{

        @Override
        protected void toggleView() {
            FlipLayout.this.toggleView();
        }
    }

}