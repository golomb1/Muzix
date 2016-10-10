package com.apps.golomb.muzix.wigets;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.Playlist;
import com.libs.golomb.extendedrecyclerview.DataExtractor.DataExtractor;
import com.libs.golomb.extendedrecyclerview.ExtendedRecycleAdapter;
import com.libs.golomb.extendedrecyclerview.viewholder.ExtendedViewHolder;

/**
 * Created by tomer on 05/10/2016.
 * This class is a view holder for a playlist item.
 */
public class PlaylistViewHolder extends ExtendedViewHolder<Playlist> {

    private final TextView mAlbumName;
    private final ImageView mAlbumImage;
    private final TextView mArtist;

    public PlaylistViewHolder(View view, ExtendedRecycleAdapter adapter) {
        super(view, adapter);
        mAlbumName = (TextView) view.findViewById(R.id.album_name);
        mAlbumImage = (ImageView) view.findViewById(R.id.album_image);
        mArtist = (TextView) view.findViewById(R.id.artist);
        ImageButton mMore = (ImageButton) view.findViewById(R.id.more);
        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - show more option
            }
        });
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
        return 0;
    }

    @Override
    public void bind(DataExtractor<Playlist, ExtendedViewHolder<Playlist>> mDataExtractor, int position, int itemType) {
        Playlist playlist = mDataExtractor.getAt(position);
        mAlbumName.setText(playlist.getTitle());
        // TODO albumImage
        mArtist.setText(playlist.getArtist());
    }
}
