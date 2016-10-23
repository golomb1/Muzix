package com.apps.golomb.muzix.recyclerHelper;

import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.RelativeLayout;

import com.apps.golomb.muzix.R;
import com.sothree.slidinguppanel.ScrollableViewHelper;

/**
 * Created by tomer on 23/10/2016.
 */

public class NestedScrollableViewHelper extends ScrollableViewHelper {


    private int id;

    public NestedScrollableViewHelper(int id) {
        this.id = id;
    }


    public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
        if (scrollableView != null && scrollableView.getId() == id) {
            if(isSlidingUp){
                return scrollableView.getScrollY();
            } else {
                RelativeLayout nsv = ((RelativeLayout) scrollableView);
                View child = nsv.getChildAt(0);
                return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
            }
        } else {
            return 0;
        }
    }
}