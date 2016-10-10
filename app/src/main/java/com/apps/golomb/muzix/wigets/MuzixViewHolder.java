package com.apps.golomb.muzix.wigets;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.utils.Utils;
import com.libs.golomb.extendedrecyclerview.DataExtractor.DataExtractor;
import com.libs.golomb.extendedrecyclerview.ExtendedRecycleAdapter;
import com.libs.golomb.extendedrecyclerview.viewholder.ExtendedViewHolder;

/**
 * Created by golomb on 22/07/2016.
 * Muzix view holder
 */
public class MuzixViewHolder extends ExtendedViewHolder<MuzixSong> {

    private final ImageView mAlbum;
    private final ImageView mDragHolder;
    private final TextView mDetails;
    private final TextView mTitle;
    private int type;

    public MuzixViewHolder(View itemView, ExtendedRecycleAdapter adapter) {
        super(itemView, adapter);
        mTitle = (TextView) itemView.findViewById(R.id.item_title);
        mTitle.setSelected(true);
        mDetails = (TextView) itemView.findViewById(R.id.item_details);
        mTitle.setSelected(true);
        mDragHolder = (ImageView) itemView.findViewById(R.id.drag_holder);
        mAlbum = (ImageView) itemView.findViewById(R.id.album);
    }

    public void bind(MuzixSong muzixSong) {
        mTitle.setText(muzixSong.getTitle());
        mDetails.setText(Utils.formatMillis(muzixSong.getDuration()));
        mDragHolder.setVisibility(View.GONE);
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
        return type;
    }

    @Override
    public void bind(DataExtractor<MuzixSong, ExtendedViewHolder<MuzixSong>> mDataExtractor, int position, int itemType) {
        bind(mDataExtractor.getAt(position));
    }

    public void setType(int type) {
        this.type = type;
    }

}
