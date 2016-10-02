package com.apps.golomb.muzix.ExtendedRecycleView;

import java.util.Collections;
import java.util.List;

/**
 * Created by golomb on 13/07/2016.
 * Example
 */
public abstract class ListDataExtractor<T> implements DataExtractor<T,ExtendedViewHolder<T>> {

    private final boolean header;
    private final boolean footer;
    protected List<? extends T> list;

    public ListDataExtractor(List<? extends T> list) {
        this(list,true,true);
    }


    public ListDataExtractor(List<? extends T> list,boolean header,boolean footer) {
        this.list = list;
        this.header = header;
        this.footer = footer;
    }



    @Override
    public int GetItemType(int position) {
        if(list.size() != 0) {
            if (header && position == 0)
                    return HEADER;
            if ((header && position < list.size() + 1) || (!header && position < list.size()))
                return ITEM;
            else if(footer)
                return FOOTER;
        }
        return -1;
    }


    @Override
    public void bindViewHolder(ExtendedViewHolder<T> holder, int position) {
        bind(holder,header ? position-1 : position,GetItemType(position));
    }

    protected abstract void bind(ExtendedViewHolder<T> holder, int position, int i);

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public T get(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ITEM;
    }

    @Override
    public void onItemMove(ExtendedRecycleAdapter<T, ExtendedViewHolder<T>> adapter, int fromPosition, int toPosition) {
        int swapCount = header ? 1 : 0;
        swapDataElement(fromPosition - swapCount,toPosition - swapCount);
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    private void swapDataElement(int fromPosition, int toPosition){
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
    }

    @Override
    public void onItemSwap(ExtendedRecycleAdapter<T, ExtendedViewHolder<T>> adapter, int position) {
        list.remove(position - 1);
        if(list.size() > 0)
            adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean hasFooter() {
        return footer;
    }

    @Override
    public boolean hasHeader() {
        return header;
    }
}
