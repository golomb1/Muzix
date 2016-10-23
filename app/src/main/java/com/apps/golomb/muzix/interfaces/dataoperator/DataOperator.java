package com.apps.golomb.muzix.interfaces.dataoperator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseArray;
import com.apps.golomb.muzix.ListDialogFragment;
import com.apps.golomb.muzix.MainActivity;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.FlexiblePair;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.CreatePlaylistDialog;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerEvent;
import com.apps.golomb.muzix.utils.MediaUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomer on 21/10/2016.
 * Sample class to data provider.
 */

public class DataOperator implements DataProvider, CreatePlaylistDialog.DialogResultReceiver {
    private EventBus eventBus;
    private MainActivity activity;
    private List<PlaylistData> playlistDataList;
    private SparseArray<List<MuzixSong>> listsData;
    private static DataProvider instance ;
    private int nextIndex;

    public static DataProvider getInstance() {
        return instance;
    }

    public DataOperator(MainActivity activity, List<MuzixSong> muzixSongList){
        instance = this;
        eventBus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .build();
        this.activity = activity;
        playlistDataList = new ArrayList<>();
        listsData = new SparseArray<>();
        nextIndex = 0;
        for(int i=0; i < 100; i++){
            PlaylistData data = new PlaylistData(this, i, (char)((i/25) + 'a') + " playlist","Artist" + i%20, PlaylistData.Type.Playlist);
            playlistDataList.add(data);
            listsData.put(i,copyToDemoList(muzixSongList));
            nextIndex++;
        }
    }

    //**********************************************************************************************
    //                      Getters
    //**********************************************************************************************

    @Override
    public List<MuzixSong> getPlaylist(int id){
        return listsData.get(id);
    }

    @Override
    public List<PlaylistData> getAllAlbums() {
        return null;
    }

    @Override
    public List<PlaylistData> getAllArtists() {
        return null;
    }

    @Override
    public List<PlaylistData> getAllPlaylists() {
        return new ArrayList<>(this.playlistDataList);
    }

    //**********************************************************************************************
    //                      Modification
    //**********************************************************************************************

    @Override
    public void removeSong(PlaylistData mPlaylistData, int position) {
        MuzixSong item = listsData.get(mPlaylistData.getId()).remove(position);
        eventBus.post(DataEvent.getListItemRemoved(mPlaylistData.getId(),position,item));
    }


    @Override
    public void insert(int playlistId, int position, MuzixSong item) {
        listsData.get(playlistId).add(position,item);
        eventBus.post(DataEvent.getListItemInserted(playlistId,position,item));
    }


    @Override
    public void addToPlaylist(FlexiblePair iFlexible, List<MuzixSong> payload) {
        int id = (int) iFlexible.getItem1();
        List<MuzixSong> list = listsData.get(id);
        int position = list.size();
        list.addAll(payload);
        eventBus.post(DataEvent.getListItemsInserted(id,position,payload));
    }

    @Override
    public void moveItem(@NonNull PlaylistData mPlaylistData, int fromPosition, int toPosition) {
        moveItem(mPlaylistData.getId(),fromPosition,toPosition,false);
    }

    @Override
    public void moveItem(int playlistId, int fromPosition, int toPosition,boolean undo) {
        List<MuzixSong> list = listsData.get(playlistId);
        list.add(toPosition,list.remove(fromPosition));
        eventBus.post(DataEvent.getListItemMoved(playlistId,fromPosition,toPosition,undo));
    }

    @Override
    public void createNewPlaylist(String name, String details, List<MuzixSong> payload) {
        addPlaylist(playlistDataList.size()
                ,new PlaylistData(this,nextIndex,name,"USER", PlaylistData.Type.Playlist)
                ,payload);
    }

    @Override
    public void addPlaylist(int position, PlaylistData data, List<MuzixSong> payload) {
        playlistDataList.add(position,data);
        if(payload == null){
            payload = new ArrayList<>();
        }
        listsData.put(nextIndex,payload);
        eventBus.post(DataEvent.getPlaylistAdded(position,data));
        nextIndex++;
    }

    @Override
    public void remove(PlaylistData data) {
        listsData.remove(data.getId());
        int index = playlistDataList.indexOf(data);
        playlistDataList.remove(data);
        eventBus.post(DataEvent.getPlaylistRemoved(index,data,data.getList()));
    }

    //**********************************************************************************************
    //                          Dialog
    //**********************************************************************************************

    @Override
    public void addToPlaylist(MuzixSong song) {
        ArrayList<MuzixSong> list = new ArrayList<>();
        list.add(song);
        createSelectPlaylistDialog(list);
    }

    @Override
    public void addToPlaylist(List<MuzixSong> songs) {
        createSelectPlaylistDialog(songs);
    }

    @Override
    public void createNewPlaylist() {
        new CreatePlaylistDialog(this).show(activity);
    }

    @Override
    public void createNewPlaylist(List<MuzixSong> payload) {
        new CreatePlaylistDialog(this,payload).show(activity);
    }

    private void createSelectPlaylistDialog(List<MuzixSong> payload) {
        FragmentManager fm = activity.getSupportFragmentManager();
        ListDialogFragment dialog = new ListDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ListDialogFragment.LIST_ARG_KEY, MediaUtils.getAllPlaylistsName());
        args.putSerializable(ListDialogFragment.LIST_PAYLOAD, (Serializable) payload);
        dialog.setArguments(args);
        dialog.show(fm, "Select a playlist");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void receiveDialogResult(String name, String details, Object payload) {
        if (payload instanceof List) {
            createNewPlaylist(name, details, (List<MuzixSong>) payload);
        } else {
            createNewPlaylist(name, details, null);
        }
    }

    //**********************************************************************************************
    //                          Observers
    //**********************************************************************************************

    @Override
    public void register(Object subscriber) {
        eventBus.register(subscriber);
    }

    @Override
    public void unregister(Object subscriber) {
        eventBus.unregister(subscriber);
    }

    //**********************************************************************************************
    //                          helper function
    //**********************************************************************************************

    private static List<MuzixSong> copyToDemoList(List<MuzixSong> muzixSongList) {
        List<MuzixSong> list = new ArrayList<>();
        for (char c = 'a'; c < 'z'; c++) {
            for (int i = 0; i < muzixSongList.size(); i++) {
                list.add(new MuzixSong(muzixSongList.get(i).getId(), c + " Song", "Artist " + 3 * i + 0, muzixSongList.get(i).getDuration(),muzixSongList.get(i).getAlbumId()));
            }
        }
        return list;
    }


}