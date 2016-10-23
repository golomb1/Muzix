package com.apps.golomb.muzix.interfaces.mediaoperator;

import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.mediaplayer.MusicService;

import java.util.List;

/**
 * Created by tomer on 14/10/2016.
 * This class is a proxy class for the music service.
 */

public class MediaPlayerOperator implements IMediaPlayerOperator {
    private MusicService service;

    public void setService(MusicService service) {
        this.service = service;
    }

    @Override
    public boolean seekTo(int position) {
        return service != null && service.seekTo(position);
    }

    @Override
    public void playPause() {
        if (service != null) {
            service.playPause();
        }
    }

    @Override
    public void stop() {
        if (service != null) {
            service.stop();
        }
    }

    @Override
    public void startPlayer() {
        service.startPlayer();
    }

    @Override
    public void pause() {
        if (service != null) {
            service.pause();
        }
    }

    @Override
    public void play(PlaylistData playlistData, List<MuzixSong> list, int position) {
        if (service != null) {
            service.play(playlistData, list, position);
        }
    }

    @Override
    public void play(MuzixSong song) {
        if (service != null) {
            service.play(song);
        }
    }

    @Override
    public void play(int position) {
        if (service != null) {
            service.play(position);
        }
    }

    @Override
    public boolean flipMixedMode() {
        return service != null && service.flipMixedMode();
    }

    @Override
    public boolean isMixedMode() {
        return service != null && service.isMixedMode();
    }

    @Override
    public void advanceRepeatMode() {
        if (service != null) {
            service.advanceRepeatMode();
        }
    }

    @Override
    public void setRepeatMode(RepeatMode mode) {
        if (service != null) {
            service.setRepeatMode(mode);
        }
    }

    @Override
    public void playPrevSong() {
        if (service != null) {
            service.playPrevSong();
        }
    }

    @Override
    public void playNextSong() {
        if (service != null) {
            service.playNextSong();
        }
    }

    @Override
    public boolean remove(MuzixSong song) {
        return service != null && service.remove(song);
    }

    @Override
    public boolean remove(int position) {
        return service != null && service.remove(position);
    }

    @Override
    public void append(List<MuzixSong> songs) {
        if (service != null) {
            service.append(songs);
        }
    }

    @Override
    public void append(MuzixSong song) {
        if (service != null) {
            service.append(song);
        }
    }

    @Override
    public void playAsNext(List<MuzixSong> songs) {
        if (service != null) {
            service.playAsNext(songs);
        }
    }

    @Override
    public void playAsNext(MuzixSong song) {
        if (service != null) {
            service.playAsNext(song);
        }
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (service != null) {
            service.moveItem(fromPosition, toPosition);
        }
    }

    public int getDuration() {
        if (service != null) {
            return this.service.getDuration();
        }
        return 0;
    }

    public int getPosition() {
        if (service != null) {
            return this.service.getPosition();
        }
        return 0;
    }

    public boolean isPlaying() {
        return service != null && service.isPlaying();
    }

    @Override
    public int getListId() {
        return service != null ? service.getListId() : 0;
    }

    @Override
    public List<MuzixSong> getPlaylist() {
        return service != null ? service.getPlaylist() : null;
    }

    @Override
    public int getListPosition() {
        return service != null ? service.getListPosition() : 0;
    }

    @Override
    public PlaylistData getPlaylistData() {
        return service != null ? service.getPlaylistData() : null;
    }

    @Override
    public boolean isPaused() {
        return service != null && service.isPaused();
    }

    @Override
    public void playShuffle(PlaylistData playlistData, List<MuzixSong> elementList) {
        if(service != null){
            service.playShuffle(playlistData,elementList);
        }
    }

    @Override
    public void externalModeItem(int from, int to) {
        if(service != null){
            service.externalModeItem(from,to);
        }
    }

    @Override
    public void insert(int position, MuzixSong item) {
        if(service != null){
            service.insert(position,item);
        }
    }

    @Override
    public void insert(int position, MuzixSong item, boolean isLocal) {
        if(service != null){
            service.insert(position,item,isLocal);
        }
    }

    @Override
    public void insert(int position, List<MuzixSong> items, boolean isLocal) {
        if(service != null){
            service.insert(position,items,isLocal);
        }
    }

    @Override
    public void removeFromList(int position) {
        if(service != null){
            service.removeFromList(position);
        }
    }

    @Override
    public void externalModeItem(int from, int to, boolean turnTemp) {
        if(service != null){
            service.externalModeItem(from,to,turnTemp);
        }
    }

    @Override
    public boolean isStopped() {
        return service != null && service.isStopped();
    }

    @Override
    public MuzixSong getCurrentSong() {
        return service != null ? service.getCurrentSong() : null;
    }

    public boolean boundToService() {
        return service != null;
    }
}
