package com.apps.golomb.muzix.ExtendedRecycleView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

/**
 * Created by golomb on 13/07/2016.
 */
public class ExtendedTouchCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperAdapter mItemTouchHelperAdapter;
    private RecyclerView.ViewHolder header;
    private RecyclerView.ViewHolder footer;

    public ExtendedTouchCallback(ItemTouchHelperAdapter itemTouchHelperAdapter) {
        mItemTouchHelperAdapter = itemTouchHelperAdapter;
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof ExtendedViewHolder) {
            int type = ((ExtendedViewHolder)viewHolder).getType();
            if(type == DataExtractor.ITEM) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }
        return makeMovementFlags(0, 0);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        if(!(target instanceof ExtendedViewHolder) || ((ExtendedViewHolder)target).getType() != DataExtractor.ITEM)
            return false;
        return super.canDropOver(recyclerView, current, target);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if(!(target instanceof ExtendedViewHolder) || ((ExtendedViewHolder)target).getType() != DataExtractor.ITEM)
            return false;
        if (viewHolder instanceof ExtendedViewHolder && ((ExtendedViewHolder)viewHolder).isMovable()) {
            mItemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }
        return false;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof ExtendedViewHolder && ((ExtendedViewHolder)viewHolder).isSwappable()) {
            mItemTouchHelperAdapter.onItemSwap(viewHolder.getLayoutPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof ExtendedViewHolder && (((ExtendedViewHolder)viewHolder).isSwappable() || ((ExtendedViewHolder)viewHolder).isMovable())) {
            if((header == null || viewHolder.itemView.getTop() + dY >= header.itemView.getBottom()) && (footer == null || viewHolder.itemView.getBottom() + dY <= footer.itemView.getTop()))
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof ExtendedViewHolder && (((ExtendedViewHolder)viewHolder).isSwappable() || ((ExtendedViewHolder)viewHolder).isMovable())) {
            if((header == null || viewHolder.itemView.getTop() + dY >= header.itemView.getBottom()) && (footer == null || viewHolder.itemView.getBottom() + dY <= footer.itemView.getTop()))
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }


    public void setHeader(RecyclerView.ViewHolder header) {
        this.header = header;
    }

    public void setFooter(RecyclerView.ViewHolder footer) {
        this.footer = footer;
    }
}