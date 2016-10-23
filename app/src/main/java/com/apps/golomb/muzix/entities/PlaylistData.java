package com.apps.golomb.muzix.entities;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.interfaces.dataoperator.DataOperator;
import com.apps.golomb.muzix.interfaces.dataoperator.DataProvider;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerOperator;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;
import com.apps.golomb.muzix.recyclerHelper.SectionHeader;
import com.apps.golomb.muzix.recyclerHelper.SelectableViewHolder;
import java.util.List;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;


/**
 * Created by tomer on 14/10/2016.
 * This class represent a playlist data, likes it's name, creator and more.
 * There are 2 special kind of lists:
 *      All Tracks - all the songs.
 *      Temp - a temporary list created by the user.
 */
public class PlaylistData
        extends AbstractFlexibleItem<PlaylistData.PlaylistViewHolder>
        implements Comparable<PlaylistData>,
        ISectionable<PlaylistData.PlaylistViewHolder,IHeader> {

    public static PlaylistData getAllTrackInstance() {
        return new PlaylistData(DataOperator.getInstance(), -2, "All Tracks","System",Type.Playlist);
    }

    public static PlaylistData getTempInstance() {
        return new PlaylistData(DataOperator.getInstance(), -1, "Temporary","User",Type.Playlist);
    }

    public static boolean isTemporaryList(PlaylistData playlistData) {
        return playlistData.id == -1;
    }

    public enum Type{
        Album, Artist, Playlist
    }

    //**********************************************************************************************
    //                                        Fields
    //**********************************************************************************************

    private final DataProvider dataOperator;


    // identifier of the this play list, this should not be shown to the user.
    private int id;
    // the name of the list.
    private String name;
    // this field is for list of albums/artists and ect.
    private String creator;
    // the type of this list.
    private Type type;

    //**********************************************************************************************
    //                                      Constructors
    //**********************************************************************************************

    public PlaylistData(DataProvider dataOperator, int id, String name, String creator, Type type) {
        this.dataOperator = dataOperator;
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.type = type;
    }


    //**********************************************************************************************
    //                                  Getters & Setters
    //**********************************************************************************************

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDetails() {
        return String.format("%s ∙ Type: %s",getCreator(),getType().toString());
    }

    public boolean hasBanner() {
        return false;
    }

    public int getBannerId() {
        return -1;
    }




    public List<MuzixSong> getList() {
        return this.dataOperator.getPlaylist(this.id);
    }

    public DataProvider getDataOperator() {
        return dataOperator;
    }


    /**********************************************************************************************/
    //                                  Flexible Header Logic                                     //
    /**********************************************************************************************/


    @Override
    public SectionHeader getHeader() {
        return SectionHeader.getInstance(this.getName().substring(0,1));
    }

    @Override
    public void setHeader(IHeader header) {

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
        return R.layout.playlist_card;
    }

    /**
     * The Adapter is provided, because it will become useful for the MyViewHolder.
     * The unique instance of the LayoutInflater is also provided to simplify the
     * creation of the VH.
     */
    @Override
    public PlaylistViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
                                               ViewGroup parent) {
        return new PlaylistViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    /**
     * Also here the Adapter is provided to get more specific information from it.
     * NonNull Payload is provided as well, you should use it more often.
     */
    @Override
    public void bindViewHolder(FlexibleAdapter adapter, PlaylistViewHolder holder, int position,
                               List payloads) {
        holder.bind(this);
    }

    /**********************************************************************************************/
    //                                         Other                                              //
    /**********************************************************************************************/

    /***
     *
     * @param o - the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(@NonNull PlaylistData o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaylistData)) return false;

        PlaylistData that = (PlaylistData) o;

        return getId() == that.getId() &&
                (getName() != null ?
                        getName().equals(that.getName()) :
                        that.getName() == null
                                &&
                                (getCreator() != null ?
                                        getCreator().equals(that.getCreator())
                                        : that.getCreator() == null &&
                                        getType() == that.getType()));

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getCreator() != null ? getCreator().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }

    //**********************************************************************************************
    //                                  PlaylistViewHolder
    //**********************************************************************************************



    class PlaylistViewHolder extends SelectableViewHolder {

        private final ImageView mPlaylistImage;
        private final TextView mPlaylistName;
        private final ImageButton mMore;
        private final TextView mCreator;

        private PlaylistData data;
        private MediaPlayerOperator operator;

        PlaylistViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            mPlaylistImage = (ImageView) itemView.findViewById(R.id.album_image);
            mPlaylistName = (TextView) itemView.findViewById(R.id.album_name);
            mMore = (ImageButton) itemView.findViewById(R.id.more);
            mCreator = (TextView) itemView.findViewById(R.id.artist);

            /** create popup **/
            if(mAdapter instanceof PlaylistAdapter) {
                operator = ((PlaylistAdapter) adapter).getMediaOperator();
            }

            final PopupMenu popup = new PopupMenu(itemView.getContext(), mMore);
            popup.getMenuInflater().inflate(R.menu.library_popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (operator != null) {
                        switch (item.getItemId()) {
                            case R.id.play_now:
                                itemView.performClick();
                                break;
                            case R.id.shuffle:
                                if(data != null) {
                                    operator.playShuffle(data,data.getList());
                                }
                                break;
                            case R.id.play_as_next: {
                                if(data != null) {
                                    operator.playAsNext(data.getList());
                                }
                            }
                            break;
                            case R.id.append_to_list: {
                                if(data != null) {
                                    operator.append(data.getList());
                                }
                            }
                            break;
                            case R.id.add_to_playlist:
                                if(mAdapter instanceof PlaylistAdapter && data != null){
                                    DataProvider dataOperator = ((PlaylistAdapter) mAdapter).getDataOperator();
                                    if(dataOperator != null){
                                        dataOperator.addToPlaylist(data.getList());
                                    }
                                }
                                break;
                            case R.id.remove:
                                if(mAdapter instanceof PlaylistAdapter && data != null){
                                    DataProvider dataOperator = ((PlaylistAdapter) mAdapter).getDataOperator();
                                    if(dataOperator != null){
                                        dataOperator.remove(data);
                                    }
                                }
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

        void bind(PlaylistData playlistData) {
            this.data = playlistData;
            this.mPlaylistName.setText(playlistData.getName());
            this.mCreator.setText(playlistData.getDetails());
            //MediaUtils.loadSongArt(itemView.getContext(),mPlaylistImage,playlistData.getImage());
        }
    }
}
