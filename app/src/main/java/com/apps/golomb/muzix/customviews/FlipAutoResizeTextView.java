package com.apps.golomb.muzix.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.lb.auto_fit_textview.AutoResizeTextView;

/**
 * Created by tomer on 11/10/2016.
 * Text view that auto resize and flip animation on setText
 */

public class FlipAutoResizeTextView extends AutoResizeTextView{
    private CharSequence setText;
    private BufferType setTextType;
    private TextFlipAnimation animator;
    public static final int ANIM_DURATION_MILLIS = 500;
    private static final Interpolator fDefaultInterpolator = new DecelerateInterpolator();

    public FlipAutoResizeTextView(Context context) {
        super(context);
        init(context);
    }

    public FlipAutoResizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlipAutoResizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        animator = new TextFlipAnimation();
        animator.setInterpolator(fDefaultInterpolator);
        animator.setDuration(ANIM_DURATION_MILLIS);
        animator.setDirection(AbstractFlipAnimation.Direction.DOWN);
        setSoundEffectsEnabled(true);
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        if (getText() == null || getText().equals("")) {
            super.setText(text, type);
        } else {
            setText = text;
            setTextType = type;
            startAnimation();
        }
    }


    public void startAnimation() {
        if(animator != null) {
            animator.setVisibilitySwapped();
            animator.setDirection(AbstractFlipAnimation.Direction.LEFT);
            startAnimation(animator);
        }
    }

    public void setText(String s, boolean b) {
        if (b) {
            setText(s);
        }
        else{
            super.setText(s,BufferType.NORMAL);
        }
    }

    private class TextFlipAnimation extends AbstractFlipAnimation{

        @Override
        protected void toggleView() {
            FlipAutoResizeTextView.super.setText(setText,setTextType);
        }
    }
}
