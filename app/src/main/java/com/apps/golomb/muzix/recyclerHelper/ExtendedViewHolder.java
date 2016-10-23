package com.apps.golomb.muzix.recyclerHelper;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.ItemTouchHelperCallback;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by tomer on 10/10/2016.
 * Extend the FlexibleViewHolder to add support of drag holder visibility
 */

public abstract class ExtendedViewHolder extends FlexibleViewHolder implements ItemTouchHelperCallback.ViewHolderCallback {
    private boolean isPlaying;
    private boolean isActive;
    private int itemPosition;
    private boolean isAttached = false;


    public ExtendedViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        isPlaying = false;
        isActive = false;
        itemPosition = -1;
    }

    public ExtendedViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
        super(view, adapter, stickyHeader);
        isPlaying = false;
        isActive = false;
        itemPosition = -1;
    }

    //With next release it will become available to be customized.
    @Override
    protected void setDragHandleView(@NonNull View view) {
        if (mAdapter.isHandleDragEnabled()) {
            view.setVisibility(View.VISIBLE);
            super.setDragHandleView(view);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    // this operation is called when the view holder is being viewed,
    // and during that time he is marked as being played.
    @CallSuper
    public void enterPlayedMode(){
        isPlaying = true;
    }
    // this operation is called when the view holder is being viewed as played,
    // and during that time he is marked as no longer being played.
    @CallSuper
    public void exitPlayedMode(){
        isPlaying = false;
    }

    // this operation is called when binding this view holder to item being played.
    @CallSuper
    public void unbindInPlayedMode(){
        isPlaying = false;
    }
    // this operation is called when binding this view holder to item that isn't played.
    @CallSuper
    public void bindInPlayedMode(){
        isPlaying = true;
    }

    @CallSuper
    public void enterActiveMode(int position){
        isActive = true;
    }

    @CallSuper
    public void exitActiveMode(){
        isActive = false;
    }

    @CallSuper
    public void bindInActiveMode(int position){
        isActive = true;
    }

    @CallSuper
    public void unbindInActiveMode(){
        isActive = false;
    }

    public abstract void updateActiveMode(int value);

    public boolean isInPlayingMode(){
        return isPlaying;
    }

    public boolean isInActiveMode(){
        return isActive;
    }

    public int getItemPosition() {
        return itemPosition;
    }
    public void setItemPosition(int position) {
        itemPosition = position;
    }

    public void attachedToWindow() {
        this.isAttached = true;
    }

    public void detachedFromWindow() {
        this.isAttached = false;
    }

    public boolean isAttachedToWindows() {
        return isAttached;
    }
}
