package com.apps.golomb.muzix.customviews;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by tomer on 11/10/2016.
 * Flip animation abstraction
 */

public class SimpleFlipAnimation extends Animation {

    private static final float EXPERIMENTAL_VALUE = 50.f;
    private Direction direction;
    private View view;

    public SimpleFlipAnimation(View view) {
        setFillAfter(true);
        this.view = view;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        // Angle around the y-axis of the rotation at the given time. It is
        // calculated both in radians and in the equivalent degrees.
        Rect rect = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            rect = view.getClipBounds();
            rect.top = (int) (interpolatedTime * view.getMeasuredHeight()) + rect.bottom;
            view.setClipBounds(rect);
        }
    }

    public enum Direction {
        UP, DOWN
    }

}
