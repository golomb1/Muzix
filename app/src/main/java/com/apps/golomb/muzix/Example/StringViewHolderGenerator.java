package com.apps.golomb.muzix.Example;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.apps.golomb.muzix.ExtendedRecycleView.DataExtractor;
import com.apps.golomb.muzix.ExtendedRecycleView.ExtendedRecycleAdapter;
import com.apps.golomb.muzix.ExtendedRecycleView.IViewHolderGenerator;
import com.apps.golomb.muzix.R;

/**
 * Created by golomb on 13/07/2016.
 * Example
 */
public class StringViewHolderGenerator implements IViewHolderGenerator<StringViewHolder> {

    private Activity mActivity;

    public StringViewHolderGenerator(Activity activity) {
        mActivity = activity;
    }

    @Override
    public StringViewHolder generate(ExtendedRecycleAdapter adapter, ViewGroup parent, int viewType) {
        if(viewType == DataExtractor.ITEM) {
            View view = mActivity.getLayoutInflater().inflate(R.layout.list_item, parent, false);
            return new StringViewHolder(view, adapter);
        }
        else if(viewType == DataExtractor.FOOTER){
            View view = mActivity.getLayoutInflater().inflate(R.layout.footer, parent, false);
            return new StringViewHolder(view, adapter);
        }
        else if(viewType == DataExtractor.HEADER){
            View view = mActivity.getLayoutInflater().inflate(R.layout.playlist_header, parent, false);
            return new StringViewHolder(view, adapter);
        }
        else if(viewType == DataExtractor.NO_ITEM){
            View view = mActivity.getLayoutInflater().inflate(R.layout.no_items, parent, false);
            return new StringViewHolder(view, adapter);
        }
        return null;
    }

    @Override
    public void bindEmptyView(StringViewHolder holder) {
        // nothing to do
        holder.setType(DataExtractor.NO_ITEM);
    }
}
