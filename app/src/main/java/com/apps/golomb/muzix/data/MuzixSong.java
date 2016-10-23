package com.apps.golomb.muzix.data;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.interfaces.dataoperator.DataProvider;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerOperator;
import com.apps.golomb.muzix.recyclerHelper.DragItemEvent;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;
import com.apps.golomb.muzix.recyclerHelper.SelectableViewHolder;
import com.apps.golomb.muzix.utils.MediaUtils;
import com.apps.golomb.muzix.utils.Utils;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by golomb on 15/07/2016.
 * represent a song file
 */
public class MuzixSong extends MuzixEntity<MuzixSong.MuzixViewHolder>{

    /**********************************************************************************************/
    //                                      class logic                                           //
    /**********************************************************************************************/
    private String album;
    private long albumId;

    public MuzixSong(long id, String title, String artist, int duration, long albumId) {
        super(id, title, duration, artist);
        this.albumId = albumId;
    }

    public MuzixSong(MuzixSong muzixSong) {
        super(muzixSong.getId(), muzixSong.getTitle(), muzixSong.getDuration(), muzixSong.getArtist());
    }

    public MuzixSong(long id, String title, int duration, long artistId, String artist, long albumId, String album) {
        super(id, title, duration, artistId, artist);
        this.album = album;
        this.albumId = albumId;
    }


    public String getDetails() {
        return getArtist() + " ∙ " + Utils.formatMillis(this.getDuration());
    }

    public String getDurationString() {
        return Utils.formatMillis(getDuration());
    }


    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    /**********************************************************************************************/
    //                                   Flexible Logic                                           //
    /**********************************************************************************************/

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public boolean isSwipeable() {
        return true;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_item;
    }

    /**
     * The Adapter is provided, because it will become useful for the MyViewHolder.
     * The unique instance of the LayoutInflater is also provided to simplify the
     * creation of the VH.
     */
    @Override
    public MuzixViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
                                            ViewGroup parent) {
        return new MuzixViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    /**
     * Also here the Adapter is provided to get more specific information from it.
     * NonNull Payload is provided as well, you should use it more often.
     */
    @Override
    public void bindViewHolder(FlexibleAdapter adapter, MuzixViewHolder holder, int position,
                               List payloads) {
        holder.bind(this);
    }

    /**********************************************************************************************/
    //                                          View Holder                                       //
    /**********************************************************************************************/


    class MuzixViewHolder extends SelectableViewHolder {

        private final TextView mTitle;
        private final TextView mDetails;
        private final ImageView mAlbum;
        private final ImageButton mMore;
        private final ImageView mDragHolder;
        private final View frontView;
        private final View rearLeftView;
        private final View rearRightView;

        private MuzixSong song;
        private MediaPlayerOperator operator;


        MuzixViewHolder(View view, final FlexibleAdapter adapter) {
            super(view, adapter);

            // inflating view
            frontView = itemView.findViewById(R.id.front_view);
            rearLeftView = itemView.findViewById(R.id.rear_left_view);
            rearRightView = itemView.findViewById(R.id.rear_right_view);

            mTitle = (TextView) itemView.findViewById(R.id.item_title);
            mTitle.setSelected(true);
            mDetails = (TextView) itemView.findViewById(R.id.item_details);
            mTitle.setSelected(true);
            mMore = (ImageButton) itemView.findViewById(R.id.more);
            mDragHolder = (ImageView) itemView.findViewById(R.id.drag_holder);
            mAlbum = (ImageView) itemView.findViewById(R.id.album);

            // set up views
            setDragHandleView(mDragHolder);

            /** create popup **/
            if(mAdapter instanceof PlaylistAdapter) {
                operator = ((PlaylistAdapter) adapter).getMediaOperator();
            }

            final PopupMenu popup = new PopupMenu(itemView.getContext(), mMore);
            popup.getMenuInflater().inflate(R.menu.popup_item_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (operator != null) {
                        switch (item.getItemId()) {
                            case R.id.play_now:
                                itemView.performClick();
                                break;
                            case R.id.play_as_next: {
                                if (song != null) {
                                    Log.d("TGolomb","Play as next view Holder");
                                    operator.playAsNext(song);
                                }
                            }
                                break;
                            case R.id.append_to_list: {
                                if (song != null)
                                    operator.append(song);
                            }
                                break;
                            case R.id.add_to_playlist:
                                if(mAdapter instanceof PlaylistAdapter && song != null){
                                    DataProvider dataOperator = ((PlaylistAdapter) adapter).getDataOperator();
                                    if(dataOperator != null){
                                        dataOperator.addToPlaylist(song);
                                    }
                                }
                                /* TODO add more options */
                                break;
                        }
                    }
                    return true;
                }
            });

            mMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });
        }


        void bind(MuzixSong muzixSong) {
            this.song = muzixSong;
            this.mTitle.setText(muzixSong.getTitle());
            this.mDetails.setText(muzixSong.getDetails());
            MediaUtils.loadSongArt(itemView.getContext(),mAlbum,muzixSong.getAlbumId());
        }

        @Override
        public void onItemReleased(int position) {
            super.onItemReleased(position);
            EventBus.getDefault().post(DragItemEvent.getInstance(DragItemEvent.Event.END));
        }

        @Override
        public void onActionStateChanged(int position, int actionState) {
            super.onActionStateChanged(position, actionState);
            if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
                EventBus.getDefault().post(DragItemEvent.getInstance(DragItemEvent.Event.START));
            }
            else if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                EventBus.getDefault().post(DragItemEvent.getInstance(DragItemEvent.Event.START));
            }
            else if(actionState == ItemTouchHelper.ACTION_STATE_IDLE){
                EventBus.getDefault().post(DragItemEvent.getInstance(DragItemEvent.Event.END));
            }
        }

        @Override
        public View getFrontView() {
            return frontView;//default itemView
        }

        @Override
        public View getRearLeftView() {
            return rearLeftView;//default null
        }

        @Override
        public View getRearRightView() {
            return rearRightView;//default null
        }
    }
}
