package com.apps.golomb.muzix.Example;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.golomb.muzix.ExtendedRecycleView.ExtendedRecycleAdapter;
import com.apps.golomb.muzix.ExtendedRecycleView.ExtendedViewHolder;
import com.apps.golomb.muzix.R;

/**
 * Created by golomb on 13/07/2016.
 *
 */
public class StringViewHolder extends ExtendedViewHolder<String> {

    private ImageView mDragHolder;
    public TextView mText;
    private int type;

    public StringViewHolder(View itemView,ExtendedRecycleAdapter adapter) {
        super(itemView,adapter);
        mDragHolder = (ImageView) itemView.findViewById(R.id.drag_holder);
        setDragHolder(mDragHolder);
        mText = (TextView) itemView.findViewById(R.id.item_title);
    }


    @Override
    public boolean isSwappable() {
       return true;
    }

    @Override
    public boolean isMovable() {
       return true;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
