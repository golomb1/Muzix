package com.apps.golomb.muzix.interfaces.dataoperator;

import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.FlexiblePair;
import com.apps.golomb.muzix.entities.PlaylistData;

import java.util.List;

/**
 * Created by tomer on 21/10/2016.
 * This class represent a DataManager class, that contains access for all the data.
 */
public interface DataProvider {

    void createNewPlaylist(String name, String details, List<MuzixSong> payload);

    //**********************************************************************************************
    //                  Creation of data
    //**********************************************************************************************
    void createNewPlaylist();

    void addPlaylist(int position , PlaylistData data, List<MuzixSong> payload);

    void createNewPlaylist(List<MuzixSong> payload);

    //**********************************************************************************************
    //                  getters of data
    //**********************************************************************************************
    List<PlaylistData> getAllPlaylists();

    List<MuzixSong> getPlaylist(int id);

    List<PlaylistData> getAllAlbums();

    List<PlaylistData> getAllArtists();

    //**********************************************************************************************
    //                  modification of data
    //**********************************************************************************************

    void addToPlaylist(FlexiblePair iFlexible, List<MuzixSong> payload);

    void addToPlaylist(List<MuzixSong> songs);

    void addToPlaylist(MuzixSong song);

    void moveItem(PlaylistData mPlaylistData, int fromPosition, int toPosition);

    void removeSong(PlaylistData mPlaylistData, int position);

    void remove(PlaylistData data);

    void insert(int playlistId, int position, MuzixSong item);

    //**********************************************************************************************
    //                  Observer & subscribers
    //**********************************************************************************************

    void register(Object subscriber);

    void unregister(Object subscriber);

    void moveItem(int playlistId, int from, int to, boolean undo);
}
