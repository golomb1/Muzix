package com.apps.golomb.muzix.wigets;

import android.view.View;
import android.widget.ImageView;

import com.apps.golomb.muzix.ExtendedRecycleView.ExtendedRecycleAdapter;
import com.apps.golomb.muzix.ExtendedRecycleView.ExtendedViewHolder;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;

/**
 * Created by golomb on 22/07/2016.
 * Muzix view holder
 */
public class MuzixHeaderViewHolder extends ExtendedViewHolder<MuzixSong> {

    private final ImageView mAlbum;

    public MuzixHeaderViewHolder(View itemView, ExtendedRecycleAdapter adapter) {
        super(itemView, adapter);
        mAlbum = (ImageView) itemView.findViewById(R.id.album);
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
        return HEADER;
    }

    public void bind(MuzixSong muzixSong) {
        //TODO
    }
}
