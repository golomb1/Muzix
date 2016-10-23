package com.apps.golomb.muzix.interfaces.mediaoperator;

import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.PlaylistData;

import java.util.List;

/**
 * Created by tomer on 14/10/2016.
 * This interface contains operation that can be preform on a media player.
 */

public interface IMediaPlayerOperator {

    /**
     * Move a song int the list in 'fromPosition' to 'toPosition'
     * This operation need to change the list type to TemporaryList.
     * @param fromPosition - the old position of the item.
     * @param toPosition - the new position of the item.
     */
    void moveItem(int fromPosition, int toPosition);

    /**
     * add 'song' to the current playing list as the next element in the queue.
     * This operation need to change the list type to TemporaryList.
     * @param song to be added.
     */
    void playAsNext(MuzixSong song);

    /**
     * add all the songs in 'songs' to the current playing list as the next elements in the queue.
     * This operation need to change the list type to TemporaryList.
     * @param songs to be added.
     */
    void playAsNext(List<MuzixSong> songs);

    /**
     * add 'song' to the current playing list as the last element in the queue.
     * This operation need to change the list type to TemporaryList.
     * @param song to be added.
     */
    void append(MuzixSong song);

    /**
     * add all the songs in 'songs' to the current playing list as the last elements in the queue.
     * This operation need to change the list type to TemporaryList.
     * @param songs to be added.
     */
    void append(List<MuzixSong> songs);

    /**
     * Remove the 'position' song in the currently played list from it.
     * This operation need to change the list type to TemporaryList.
     * This operation cannot be preform on the current played song.
     * @param position of the item to remove.
     * @return true if the item was removed, false otherwise.
     */
    boolean remove(int position);

    /**
     * Remove  'song' from the list currently being played.
     * This operation need to change the list type to TemporaryList.
     * This operation cannot be preform on the current played song.
     * @param song of the item to remove.
     * @return true if the item was removed, false otherwise.
     */
    boolean remove(MuzixSong song);

    /**
     * Play the next song in the list.
     */
    void playNextSong();

    /**
     * Play the previous song in the list.
     */
    void playPrevSong();

    /**
     * set the repeat mode of the media layer.
     * @param mode of the new repeat mode.
     */
    void setRepeatMode(RepeatMode mode);

    /**
     * advance the repeatMode of the media player to the next in a chronological way.
     */
    void advanceRepeatMode();

    /**
     * @return if mixed mode is active.
     */
    boolean isMixedMode();

    /**
     * flip the value of the current mixed mode.
     * @return the new mixed mode
     */
    boolean flipMixedMode();

    /**
     * play the 'position' song in the current being played list.
     * @param position of the song to play in the list.
     */
    void play(int position);

    /**
     * play the 'song' in the current being played list.
     * @param song to be played.
     */
    void play(MuzixSong song);

    /**
     * play a new list 'list' associate with 'playlistId' identifier, starting from 'position'
     * @param playlistData - the data of the list.
     * @param list - the new list to play.
     * @param position - of the first song to play.
     */
    void play(PlaylistData playlistData, List<MuzixSong> list, int position);

    /**
     * pause the plating of the current played list.
     */
    void pause();


    /**
     * stop the plating of the current played list.
     */
    void stop();

    /**
     * resume the playing
     */
    void startPlayer();

    /**
     * seekTo the execution of the current song being player to position specified by 'seekValue'
     * @param seekValue to where to seekTo.
     */
    boolean seekTo(int seekValue);

    /**
     * flip the play pause mode, if playing then pause the view else start playing.
     */
    void playPause();

    /**
     * @return if the media player is paused.
     */
    boolean isPaused();

    /**
     * @return if the media player is stopped.
     */
    boolean isStopped();

    /**
     * @return the list Id of the current playing list.
     */
    int getListId();

    /**
     * @return the current list being played.
     */
    List<MuzixSong> getPlaylist();

    /**
     * @return the index of the song being played in the current list.
     */
    int getListPosition();

    /**
     * @return the data of the current playlist.
     */
    PlaylistData getPlaylistData();

    /**
     * @return the song being played now.
     */
    MuzixSong getCurrentSong();


    void playShuffle(PlaylistData playlistData, List<MuzixSong> elementList);

    void externalModeItem(int from, int to);

    void insert(int position, MuzixSong item);
    void insert(int position, MuzixSong item, boolean isLocal);
    void insert(int position, List<MuzixSong> item, boolean isLocal);

    void removeFromList(int position);

    void externalModeItem(int from, int to, boolean turnTemp);
}
