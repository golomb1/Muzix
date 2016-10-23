package com.apps.golomb.muzix.entities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.recyclerHelper.SectionHeader;

import java.util.List;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by tomer on 21/10/2016.
 * FlexiblePair of values
 */

public class FlexiblePair<K,T> implements
        IFlexible<FlexiblePair.SimpleViewHolder>,
        ISectionable<FlexiblePair.SimpleViewHolder,IHeader> {
    private K item1;
    private T item2;

    public FlexiblePair(K item1, T item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public K getItem1() {
        return item1;
    }

    public void setItem1(K item1) {
        this.item1 = item1;
    }

    public T getItem2() {
        return item2;
    }

    public void setItem2(T item2) {
        this.item2 = item2;
    }

    //**********************************************************************************************
    //              IFlexible Logic
    //**********************************************************************************************

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(boolean hidden) {

    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void setSelectable(boolean selectable) {

    }

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public void setDraggable(boolean draggable) {

    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public void setSwipeable(boolean swipeable) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.simple_list_item;
    }

    @Override
    public SimpleViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new SimpleViewHolder(inflater.inflate(getLayoutRes(),parent,false),adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, FlexiblePair.SimpleViewHolder holder, int position, List payloads) {
        holder.setText(getItem2().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlexiblePair)) return false;

        FlexiblePair<?, ?> pair = (FlexiblePair<?, ?>) o;

        return getItem1() != null ?
                getItem1().equals(pair.getItem1()) :
                pair.getItem1() == null
                        &&
                        (getItem2() != null ?
                                getItem2().equals(pair.getItem2()) :
                                pair.getItem2() == null);

    }

    @Override
    public int hashCode() {
        int result = getItem1() != null ? getItem1().hashCode() : 0;
        result = 31 * result + (getItem2() != null ? getItem2().hashCode() : 0);
        return result;
    }

    @Override
    public IHeader getHeader() {
        return SectionHeader.getInstance(this.getItem2().toString().substring(0,1));
    }

    @Override
    public void setHeader(IHeader header) {
    }

    class SimpleViewHolder extends FlexibleViewHolder{
        private final TextView mTextView;

        SimpleViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            mTextView = (TextView) itemView.findViewById(R.id.item_text);
        }

        public void setText(String text) {
            mTextView.setText(text);
        }
    }
}
