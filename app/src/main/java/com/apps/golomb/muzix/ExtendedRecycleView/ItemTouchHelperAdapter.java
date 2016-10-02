package com.apps.golomb.muzix.ExtendedRecycleView;

/**
 * Created by golomb on 13/07/2016.
 * This interface is for indicating the data set that the items have changed.
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemSwap(int position);
}