package com.apps.golomb.muzix.recyclerHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.golomb.muzix.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by tomer on 16/10/2016.
 * Header for the all the list.
 */
public class SimpleListHeader extends AbstractHeaderItem<FlexibleViewHolder> {

    /**
     * The Adapter is provided, because it will become useful for the MyViewHolder.
     * The unique instance of the LayoutInflater is also provided to simplify the
     * creation of the VH.
     */
    @Override
    public SimpleListHeader.ListHeaderViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
                                                                  ViewGroup parent) {
        return new SimpleListHeader.ListHeaderViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter, true);
    }


    /**
     * Also here the Adapter is provided to get more specific information from it.
     * NonNull Payload is provided as well, you should use it more often.
     */
    @Override
    public void bindViewHolder(FlexibleAdapter adapter, FlexibleViewHolder holder, int position,
                               List payloads) {
        // nothing
    }


    /**
     * For the list_item type we need an int value: the layoutResID is sufficient.
     */
    @Override
    public int getLayoutRes() {
        return R.layout.simple_header_list_view;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof SimpleListHeader;
    }

    @SuppressWarnings("unchecked")
    private class ListHeaderViewHolder extends FlexibleViewHolder {

        ListHeaderViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
        }
    }
}
