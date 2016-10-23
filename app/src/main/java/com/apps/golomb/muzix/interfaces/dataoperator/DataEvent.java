package com.apps.golomb.muzix.interfaces.dataoperator;

import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.PlaylistData;

import java.util.List;

/**
 * Created by tomer on 22/10/2016.
 * This class is factory of media player events.
 */
public class DataEvent {
    // TODO - make a cache and a pool to prevent the creation of those objects.
    public static PlaylistAdded getPlaylistAdded(int position,PlaylistData data){ return new PlaylistAdded(position, data);}
    public static PlaylistRemoved getPlaylistRemoved(int position, PlaylistData data, List<MuzixSong> list){ return new PlaylistRemoved(position, data, list);}
    public static ListItemRemoved getListItemRemoved(int playlistId, int position, MuzixSong song){ return new ListItemRemoved(playlistId,position,song); }
    public static ListItemInserted getListItemInserted(int playlistId, int position, MuzixSong song){ return new ListItemInserted(playlistId,position,song); }
    public static ListItemsInserted getListItemsInserted(int listId, int position, List<MuzixSong> songs){ return new ListItemsInserted(listId,position,songs); }
    public static ListItemMoved getListItemMoved(int listId, int from, int to, boolean undo){ return new ListItemMoved(listId,from,to,undo); }

    /**********************************************************************************************/
    //                      Event classes
    /**********************************************************************************************/
    public static class PlaylistRemoved{
        private final int position;
        private final PlaylistData data;
        private final List<MuzixSong> list;

        public PlaylistRemoved(int position, PlaylistData data, List<MuzixSong> list) {
            this.position = position;
            this.data = data;
            this.list = list;
        }

        public int getPosition() {
            return position;
        }

        public PlaylistData getData() {
            return data;
        }

        public List<MuzixSong> getList() {
            return list;
        }
    }
    public static class PlaylistAdded{
        private final int position;
        private final PlaylistData data;

        public PlaylistAdded(int position, PlaylistData data) {
            this.position = position;
            this.data = data;
        }

        public int getPosition() {
            return position;
        }

        public PlaylistData getData() {
            return data;
        }
    }
    public static class ListItemRemoved{

        private final int position;
        private final int playlistId;
        private final MuzixSong item;

        ListItemRemoved(int playlistId, int position, MuzixSong song){
            this.playlistId = playlistId;
            this.position = position;
            this.item = song;
        }

        public int getPlaylistId() {
            return playlistId;
        }

        public int getPosition() {
            return position;
        }

        public MuzixSong getItem() {
            return item;
        }
    }
    public static class ListItemsInserted{
        private final int position;
        private final int playlistId;
        private final List<MuzixSong> items;

        ListItemsInserted(int playlistId, int position, List<MuzixSong> songs){
            this.playlistId = playlistId;
            this.position = position;
            this.items = songs;
        }

        public int getPlaylistId() {
            return playlistId;
        }

        public int getPosition() {
            return position;
        }

        public List<MuzixSong> getItems() {
            return items;
        }
    }
    public static class ListItemInserted{
        private final int position;
        private final int playlistId;
        private final MuzixSong item;

        ListItemInserted(int playlistId, int position, MuzixSong song){
            this.playlistId = playlistId;
            this.position = position;
            this.item = song;
        }

        public int getPlaylistId() {
            return playlistId;
        }

        public int getPosition() {
            return position;
        }

        public MuzixSong getItem() {
            return item;
        }
    }
    public static class ListItemMoved{
        private final int listId;
        private final int from;
        private final int to;
        private final boolean undo;
        ListItemMoved(int listId,int from, int to){
            this(listId,from,to,false);
        }
        ListItemMoved(int listId,int from, int to, boolean undo){
            this.listId = listId;
            this.from = from;
            this.to = to;
            this.undo = undo;
        }
        public int getListId(){return listId;}
        public int getFrom() {
            return from;
        }
        public int getTo() {
            return to;
        }
        public boolean isUndo() {
            return undo;
        }
    }
}
