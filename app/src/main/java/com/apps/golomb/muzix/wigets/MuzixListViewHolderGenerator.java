package com.apps.golomb.muzix.wigets;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.apps.golomb.muzix.MainActivity;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.Playlist;
import com.libs.golomb.extendedrecyclerview.DataExtractor.DataExtractor;
import com.libs.golomb.extendedrecyclerview.ExtendedRecycleAdapter;
import com.libs.golomb.extendedrecyclerview.IViewHolderGenerator;
import com.libs.golomb.extendedrecyclerview.viewholder.EmptyViewHolder;
import com.libs.golomb.extendedrecyclerview.viewholder.ExtendedViewHolder;
import com.libs.golomb.extendedrecyclerview.viewholder.ListFooterViewHolder;

import java.security.InvalidParameterException;

/**
 * Created by tomer on 05/10/2016.
 */
public class MuzixListViewHolderGenerator implements IViewHolderGenerator<ExtendedViewHolder<Playlist>> {

    private Activity mActivity;

    public MuzixListViewHolderGenerator(Activity activity) {
        mActivity = activity;
    }

    @Override
    public ExtendedViewHolder<Playlist> generate(ExtendedRecycleAdapter adapter, ViewGroup parent, int viewType) {
        if(viewType == DataExtractor.ITEM) {
            View view = mActivity.getLayoutInflater().inflate(R.layout.playlist_card, parent, false);
            return new PlaylistViewHolder(view, adapter);
        }
        else if(viewType == DataExtractor.FOOTER){
            View view = mActivity.getLayoutInflater().inflate(R.layout.playlist_footer, parent, false);
            return new ListFooterViewHolder<>(view, adapter);
        }
        else if(viewType == DataExtractor.NO_ITEM){
            View view = mActivity.getLayoutInflater().inflate(R.layout.no_items, parent, false);
            return new EmptyViewHolder<>(view, adapter);
        }
        throw new InvalidParameterException("Invalid type " + viewType);
    }

    @Override
    public void bindEmptyView(ExtendedViewHolder<Playlist> holder) {
        // nothing to do
    }
}
