package com.apps.golomb.muzix.Example;

import com.apps.golomb.muzix.ExtendedRecycleView.DataExtractor;
import com.apps.golomb.muzix.ExtendedRecycleView.ExtendedRecycleAdapter;
import com.apps.golomb.muzix.data.MuzixEntity;

import java.util.Collections;
import java.util.List;

/**
 * Created by golomb on 13/07/2016.
 * Example
 */
public class StringDataExtractor implements DataExtractor<String , StringViewHolder> {

    List<String> stringList;

    public StringDataExtractor(List<String> stringList) {
        this.stringList = stringList;
    }

    @Override
    public int GetItemType(int position) {
        if(stringList.size() != 0) {
            if (position == 0)
                return HEADER;
            if (position < stringList.size() + 1)
                return ITEM;
            else
                return FOOTER;
        }
        return -1;
    }

    @Override
    public int size() {
        return stringList.size();
    }

    @Override
    public String get(int s) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void bindViewHolder(StringViewHolder holder, int position) {
        if(stringList.size() != 0 && position != 0 && position < stringList.size() + 1) {
            holder.mText.setText(stringList.get(position - 1));
        }
        holder.setType(GetItemType(position));
    }

    @Override
    public void onItemMove(ExtendedRecycleAdapter<String, StringViewHolder> adapter, int fromPosition, int toPosition) {
        swapDataElement(fromPosition - 1,toPosition - 1);

        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    private void swapDataElement(int fromPosition, int toPosition){
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(stringList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(stringList, i, i - 1);
            }
        }
    }

    @Override
    public void onItemSwap(ExtendedRecycleAdapter<String, StringViewHolder> adapter, int position) {
        stringList.remove(position - 1);
        if(stringList.size() > 0)
            adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean hasFooter() {
        return true;
    }

    @Override
    public boolean hasHeader() {
        return true;
    }
}
