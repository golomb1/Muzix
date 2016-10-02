package com.apps.golomb.muzix.ExtendedRecycleView;

import android.view.View;
import android.widget.TextView;

import com.apps.golomb.muzix.R;

/**
 * Created by golomb on 22/07/2016.
 */
public class EmptyViewHolder<T> extends ExtendedViewHolder<T>{

    public EmptyViewHolder(View itemView, ExtendedRecycleAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    public boolean isSwappable() {
        return false;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public int getType() {
        return NO_ITEM;
    }
}
