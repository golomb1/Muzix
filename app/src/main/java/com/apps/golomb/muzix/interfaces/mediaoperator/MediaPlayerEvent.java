package com.apps.golomb.muzix.interfaces.mediaoperator;

import com.apps.golomb.muzix.data.MuzixSong;

import java.util.List;

/**
 * Created by tomer on 14/10/2016.
 * This class is factory of media player events.
 */

public class MediaPlayerEvent {
    // TODO - make a cache and a pool to prevent the creation of those objects.
    public static ListChangeEvent getListChangeEvent(){ return new ListChangeEvent();}
    public static ListPositionEvent getListPositionEvent() {
        return new ListPositionEvent();
    }
    public static PlayerStateEvent getPlayerStateEvent() {
        return new PlayerStateEvent();
    }
    public static ListItemRemoved getListItemRemoved(MuzixSong song, int position,boolean isLocal){ return new ListItemRemoved(song,position,isLocal); }
    public static ListItemMoved getListItemMoved(int from, int to){ return new ListItemMoved(from,to); }
    public static ListItemMoved getListItemMoved(int from, int to,boolean undo){ return new ListItemMoved(from,to,undo); }
    public static ListItemInserted getListItemInserted(int position, MuzixSong song){ return new ListItemInserted(song,position); }
    public static ListItemsInserted getListItemsInserted(int position, List<MuzixSong> song){ return new ListItemsInserted(song,position); }

    /**********************************************************************************************/
    //                      Event classes
    /**********************************************************************************************/

    public static class ListChangeEvent{ }
    public static class ListPositionEvent{ }
    public static class PlayerStateEvent{ }
    public static class ListItemRemoved{
        private final boolean isLocal;
        private final int position;
        private final MuzixSong item;

        ListItemRemoved(MuzixSong item, int position,boolean isLocal){
            this.item = item;
            this.position = position;
            this.isLocal = isLocal;
        }

        public boolean isLocal(){return isLocal;}
        public MuzixSong getItem() {
            return item;
        }

        public int getPosition() {
            return position;
        }
    }
    public static class ListItemInserted{

        private final int position;
        private final MuzixSong item;

        ListItemInserted(MuzixSong item, int position){
            this.item = item;
            this.position = position;
        }

        public MuzixSong getItem() {
            return item;
        }

        public int getPosition() {
            return position;
        }
    }
    public static class ListItemsInserted{

        private final int position;
        private final List<MuzixSong> items;

        ListItemsInserted(List<MuzixSong> items, int position){
            this.items = items;
            this.position = position;
        }

        public List<MuzixSong> getItems() {
            return items;
        }

        public int getPosition() {
            return position;
        }
    }
    public static class ListItemMoved{

        private final int from;
        private final int to;
        private final boolean external;

        ListItemMoved(int from, int to){
            this(from,to,false);
        }

        ListItemMoved(int from, int to, boolean external){
            this.from = from;
            this.to = to;
            this.external = external;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public boolean isExternal() {
            return external;
        }
    }
}
