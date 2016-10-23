package com.apps.golomb.muzix.customviews;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import static android.R.attr.direction;

/**
 * Created by tomer on 11/10/2016.
 * Flip animation abstraction
 */

public abstract class AbstractFlipAnimation extends Animation {

    public interface OnFlipListener {

        void onFlipStart(FlipLayout view);

        void onFlipEnd(FlipLayout view);
    }

    private static final float EXPERIMENTAL_VALUE = 50.f;
    private Camera camera;
    private float centerX;
    private float centerY;
    private boolean visibilitySwapped;
    private Direction direction;

    public AbstractFlipAnimation() {
        setFillAfter(true);
    }

    void setVisibilitySwapped() {
        visibilitySwapped = false;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        camera = new Camera();
        this.centerX = width / 2;
        this.centerY = height / 2;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        // Angle around the y-axis of the rotation at the given time. It is
        // calculated both in radians and in the equivalent degrees.
        final double radians = Math.PI * interpolatedTime;

        float degrees = (float) (180.0 * radians / Math.PI);

        if (direction == Direction.UP) {
            degrees = -degrees;
        }

        // Once we reach the midpoint in the animation, we need to hide the
        // source view and show the destination view. We also need to change
        // the angle by 180 degrees so that the destination does not come in
        // flipped around. This is the main problem with SDK sample, it does
        // not
        // do this.
        if (interpolatedTime >= 0.5f) {
            if (direction == Direction.UP || direction == Direction.LEFT) {
                degrees += 180.f;
            }

            if (direction == Direction.DOWN || direction == Direction.RIGHT) {
                degrees -= 180.f;
            }

            if (!visibilitySwapped) {
                toggleView();
                visibilitySwapped = true;
            }
        }

        final Matrix matrix = t.getMatrix();

        camera.save();
        //you can delete this line, it move camera a little far from view and get back
        camera.translate(0.0f, 0.0f, (float) (EXPERIMENTAL_VALUE * Math.sin(radians)));
        if (direction == Direction.UP || direction == Direction.DOWN) {
            camera.rotateX(degrees);
            camera.rotateY(0);
            camera.rotateZ(0);
        } else {
            camera.rotateX(0);
            camera.rotateY(degrees);
            camera.rotateZ(0);
        }
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }


    void flipDirection() {
        direction = direction == AbstractFlipAnimation.Direction.UP ?
                AbstractFlipAnimation.Direction.DOWN :
                AbstractFlipAnimation.Direction.UP;
    }

    protected abstract void toggleView();

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

}
