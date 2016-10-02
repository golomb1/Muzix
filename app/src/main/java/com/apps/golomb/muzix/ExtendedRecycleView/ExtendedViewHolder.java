package com.apps.golomb.muzix.ExtendedRecycleView;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

public abstract class ExtendedViewHolder<T> extends RecyclerView.ViewHolder{
    public static final int ITEM = DataExtractor.ITEM;
    public static final int NO_ITEM = DataExtractor.NO_ITEM;
    public static final int FOOTER = DataExtractor.FOOTER;
    public static final int HEADER = DataExtractor.HEADER;

    private ExtendedRecycleAdapter mAdapter;

    public ExtendedViewHolder(View itemView, ExtendedRecycleAdapter adapter){
        super(itemView);
        mAdapter = adapter;
    }

    /**
     * this method is called when the user start to drag the view holder via the drag holder.
     */
    protected void onStartDrag(){
        mAdapter.onStartDrag(this);
    }

    /**
     * set up the drag holder and all the relevant listeners.
     * @param dragHolder to add.
     */
    public void setDragHolder(View dragHolder){
        if(dragHolder != null) {
            dragHolder.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int eventType = MotionEventCompat.getActionMasked(event);
                    if (eventType == MotionEvent.ACTION_DOWN || eventType == MotionEvent.ACTION_UP) {
                        onStartDrag();
                    }
                    return false;
                }
            });
        }
    }



    public abstract  boolean isSwappable();

    public abstract boolean isMovable();

    public abstract int getType();
}
