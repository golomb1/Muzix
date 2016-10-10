package com.apps.golomb.muzix.wigets;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;


import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;
import com.libs.golomb.extendedrecyclerview.DataExtractor.DataExtractor;
import com.libs.golomb.extendedrecyclerview.DataExtractor.SectionListDataExtractor;
import com.libs.golomb.extendedrecyclerview.ExtendedRecycleAdapter;
import com.libs.golomb.extendedrecyclerview.IViewHolderGenerator;
import com.libs.golomb.extendedrecyclerview.viewholder.EmptyViewHolder;
import com.libs.golomb.extendedrecyclerview.viewholder.ExtendedViewHolder;
import com.libs.golomb.extendedrecyclerview.viewholder.ListFooterViewHolder;

import java.security.InvalidParameterException;

/**
 * Created by golomb on 13/07/2016.
 * Example
 */
public class MuzixViewHolderGenerator implements IViewHolderGenerator<ExtendedViewHolder<MuzixSong>> {

    private Activity mActivity;

    public MuzixViewHolderGenerator(Activity activity) {
        mActivity = activity;
    }

    @Override
    public ExtendedViewHolder<MuzixSong> generate(ExtendedRecycleAdapter adapter, ViewGroup parent, int viewType) {
        if(viewType == DataExtractor.ITEM) {
            View view = mActivity.getLayoutInflater().inflate(R.layout.list_item, parent, false);
            return new MuzixViewHolder(view, adapter);
        }
        else if(viewType == DataExtractor.FOOTER){
            View view = mActivity.getLayoutInflater().inflate(R.layout.playlist_footer, parent, false);
            return new ListFooterViewHolder<>(view, adapter);
        }
        else if(viewType == DataExtractor.HEADER){
            View view = mActivity.getLayoutInflater().inflate(R.layout.playlist_header, parent, false);
            return new MuzixHeaderViewHolder(view, adapter);
        }
        else if(viewType == SectionListDataExtractor.SECTION){
            View view = mActivity.getLayoutInflater().inflate(R.layout.section, parent, false);
            return new SectionViewHolder<String,MuzixSong>(view, adapter);
        }
        else if(viewType == DataExtractor.NO_ITEM){
            View view = mActivity.getLayoutInflater().inflate(R.layout.no_items, parent, false);
            return new EmptyViewHolder<>(view, adapter);
        }
        throw new InvalidParameterException("Invalid type");
    }

    @Override
    public void bindEmptyView(ExtendedViewHolder<MuzixSong> holder) {
        // nothing to do
    }
}
