package com.apps.golomb.muzix.ExtendedRecycleView;

import android.view.View;
import android.widget.TextView;

import com.apps.golomb.muzix.R;

/**
 * Created by golomb on 22/07/2016.
 */
public class ListFooterViewHolder<T> extends ExtendedViewHolder<T>{

    private static final String MESSAGE = " Items";
    private final TextView mText;

    public ListFooterViewHolder(View itemView, ExtendedRecycleAdapter adapter) {
        super(itemView, adapter);
        mText = (TextView) itemView.findViewById(R.id.text);
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
        return FOOTER;
    }

    public void setText(String text){
        mText.setText(text);
    }

    public void setText(int numOfItem){
        setText(numOfItem + MESSAGE);
    }
}
