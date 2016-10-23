package com.apps.golomb.muzix.mediaplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.apps.golomb.muzix.MainActivity;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.mediaoperator.IMediaPlayerOperator;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerEvent;
import com.apps.golomb.muzix.interfaces.mediaoperator.RepeatMode;
import com.apps.golomb.muzix.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/***
 * This class responsible to play music, this class should operate without the need of an a activity.
 * by default when creating this class, the activity need to specify the playlist it's want to use. and position.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, IMediaPlayerOperator {

    // The data of the current playlist
    private PlaylistData playlistData;
    // current playlist being played.
    private List<MuzixSong> playlist;
    // the position of the song being played in the playlist.
    private int listPosition;

    private RepeatMode repeatMode;
    private boolean isMixedMode;

    // media player
    private MediaPlayer mediaPlayer;
    private final IBinder musicBind = new MusicService.MusicBinder();
    private boolean isPause;

    private EventBus eventBus;
    private MediaObserver progressObserver;
    private boolean isBeingObserved;

    /***********************************************/
    //                 On Create                   //
    /***********************************************/


    @Override
    public void onCreate() {
        //create the service
        super.onCreate();
        // init properties
        eventBus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .build();
        isPause = false;
        isMixedMode = Utils.getMixedMode();
        repeatMode = Utils.getRepeatMode();
        listPosition = 0;
        playlistData = null;
        playlist = new ArrayList<>();

        // create the player
        mediaPlayer = new MediaPlayer();

        //set player properties
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);




        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        stopPlayer();
        mediaPlayer.release();
        Log.d("Service","OnDestroy");
        super.onDestroy();
    }

    //***********************************************
    //                Observers data
    //***********************************************

    public void addObserver(Object observer) {
        eventBus.register(observer);
    }

    public void removeObserver(Object observer) {
        eventBus.unregister(observer);
    }

    public boolean hasObserver(Object observer) {
        return eventBus.isRegistered(observer);
    }

    private void notifyObservers(Object event) {
        if (Utils.DEBUG) {
            Log.d("TGolomb", "notify on media player event: " + event.getClass().getSimpleName());
        }
        eventBus.post(event);
    }

    //***********************************************
    //                List data
    //***********************************************

    public List<MuzixSong> getPlaylist() {
        return new ArrayList<>(this.playlist);
    }

    public int getListId() {
        if (playlistData != null) {
            return playlistData.getId();
        }
        return Integer.MIN_VALUE;
    }

    public void setList(PlaylistData playlistData, List<MuzixSong> list) {
        this.playlist = new ArrayList<>(list);
        this.playlistData = playlistData;
        notifyObservers(MediaPlayerEvent.getListChangeEvent());
    }

    /***********************************************/
    //              Song position                  //

    /***********************************************/

    @Override
    public int getListPosition() {
        return listPosition;
    }

    public void setSong(int songIndex) {
        listPosition = songIndex;
        notifyObservers(MediaPlayerEvent.getListPositionEvent());
    }

    //***********************************************
    //                Other data
    //***********************************************

    @Override
    public PlaylistData getPlaylistData() {
        return playlistData;
    }

    @Override
    public MuzixSong getCurrentSong() {
        if (playlist != null && listPosition >= 0 && listPosition < playlist.size()) {
            return playlist.get(listPosition);
        }
        return null;
    }

    @Override
    public void playShuffle(PlaylistData playlistData, List<MuzixSong> elementList) {
        // TODO - shuffle
        play(playlistData,elementList,0);
    }

    //**********************************************************************************************
    //                 progress observer
    //**********************************************************************************************

    public void createProgressObserver() {
        if (progressObserver == null) {
            progressObserver = new MediaObserver(this.mediaPlayer, this.eventBus);
            isBeingObserved = true;
        }
    }

    public void destroyProgressObserver() {
        if (progressObserver != null) {
            isBeingObserved = false;
        }
    }

    /***********************************************/
    //                media player                 //

    /***********************************************/


    private void playSong() {
        //play a song
        // first we need to reset the media player
        mediaPlayer.reset();
        isPause = false;
        // get song
        MuzixSong playSong = playlist.get(listPosition);
        //get id
        long currSong = playSong.getId();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try {
            mediaPlayer.setDataSource(this, trackUri);
            mediaPlayer.prepareAsync();
            if (progressObserver != null && isBeingObserved) {
                progressObserver.start();
            }
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
    }

    // create a communication channel to the activity
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    // free resources when service is unbound
    // This will execute when the user exits the app, at which point we will stop the service.
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (progressObserver != null) {
            progressObserver.stop();
        }
        if (repeatMode.equals(RepeatMode.REPEAT_SINGLE)) {
            playSong();
        } else if (repeatMode.equals(RepeatMode.REPEAT_LIST)) {
            playNextSong();
        } else {
            stopPlayer();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        if (progressObserver != null) {
            progressObserver.stop();
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // the media player is ready to start playing
        // start the playback
        startPlayer();
    }

    public void playPause() {
        if (mediaPlayer.isPlaying()) {
            pausePlayer();
        } else {
            startPlayer();
        }
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (progressObserver != null) {
                progressObserver.stop();
            }
            isPause = true;
            notifyObservers(MediaPlayerEvent.getPlayerStateEvent());
        }
    }

    public void stopPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            if (progressObserver != null) {
                progressObserver.stop();
            }
            isPause = false;
            notifyObservers(MediaPlayerEvent.getPlayerStateEvent());
        }
    }

    public void startPlayer() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            if (progressObserver != null && isBeingObserved) {
                progressObserver.start();
            }
            notifyObservers(MediaPlayerEvent.getPlayerStateEvent());
        }
    }

    /***********************************************/
    //            media player operator            //
    /***********************************************/

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        this.playlist.add(toPosition, this.playlist.remove(fromPosition));
        this.playlistData = PlaylistData.getTempInstance();
        notifyObservers(MediaPlayerEvent.getListItemMoved(fromPosition,toPosition));
        if (fromPosition < listPosition && toPosition >= listPosition) {
            setSong(listPosition - 1);
        } else if (fromPosition > listPosition && toPosition <= listPosition) {
            setSong(listPosition + 1);
        } else if (fromPosition == listPosition) {
            setSong(toPosition);
        }
    }

    @Override
    public void externalModeItem(int fromPosition, int toPosition) {
        externalModeItem(fromPosition,toPosition,true);
    }

    @Override
    public void externalModeItem(int fromPosition, int toPosition, boolean turnTemp) {
        this.playlist.add(toPosition, this.playlist.remove(fromPosition));
        if(turnTemp){
            this.playlistData = PlaylistData.getTempInstance();
        }
        notifyObservers(MediaPlayerEvent.getListItemMoved(fromPosition,toPosition,true));
        if (fromPosition < listPosition && toPosition >= listPosition) {
            setSong(listPosition - 1);
        } else if (fromPosition > listPosition && toPosition <= listPosition) {
            setSong(listPosition + 1);
        } else if (fromPosition == listPosition) {
            setSong(toPosition);
        }
    }

    @Override
    public void insert(int position , MuzixSong song) {
        insert(position,song,true);
    }

    @Override
    public void insert(int position, MuzixSong song, boolean isLocal) {
        playlist.add(position, song);
        if(position <= listPosition){
            listPosition+=1;
        }
        if(isLocal){ this.playlistData = PlaylistData.getTempInstance(); }
        notifyObservers(MediaPlayerEvent.getListItemInserted(position,song));
    }

    @Override
    public void insert(int position, List<MuzixSong> items, boolean isLocal) {
        playlist.addAll(position, items);
        if(position <= listPosition){
            listPosition+=1;
        }
        if(isLocal){ this.playlistData = PlaylistData.getTempInstance(); }
        notifyObservers(MediaPlayerEvent.getListItemsInserted(position,items));
    }


    @Override
    public void playAsNext(MuzixSong song) {
        insert(listPosition+1,song);
    }


    @Override
    public void playAsNext(List<MuzixSong> songs) {
        int index = this.listPosition + 1;
        playlist.addAll(index, songs);
        this.playlistData = PlaylistData.getTempInstance();
        notifyObservers(MediaPlayerEvent.getListItemsInserted(index, songs));
    }

    @Override
    public void append(MuzixSong song) {
        int index = this.playlist.size();
        playlist.add(song);
        this.playlistData = PlaylistData.getTempInstance();
        notifyObservers(MediaPlayerEvent.getListItemInserted(index,song));
    }

    @Override
    public void append(List<MuzixSong> songs) {
        int index = playlist.size();
        playlist.addAll(songs);
        this.playlistData = PlaylistData.getTempInstance();
        notifyObservers(MediaPlayerEvent.getListItemsInserted(index,songs));
    }

    @Override
    public boolean remove(int position) {
        return remove(position,true);
    }

    @Override
    public boolean remove(MuzixSong song) {
        return remove(playlist.indexOf(song));
    }


    @Override
    public void removeFromList(int position) {
        remove(position,false);
    }

    private boolean remove(int position, boolean isTemp){
        boolean currentSong = listPosition == position;
        MuzixSong item = playlist.remove(position);
        if (item != null) {
            if(isTemp && !PlaylistData.isTemporaryList(this.playlistData)) {
                this.playlistData = PlaylistData.getTempInstance();
            }
            notifyObservers(MediaPlayerEvent.getListItemRemoved(item,position,isTemp));
            if(position < listPosition){
                setSong(listPosition - 1);
            }
            if (currentSong && playlist.size() > 0) {
                setSong(listPosition);
                playSong();
            }
            if(playlist.size() == 0){
                stopPlayer();
                mediaPlayer.reset();
            }
            return true;
        }
        return false;
    }

    @Override
    public void playNextSong() {
        // TODO mixed version
        int size = playlist.size();
        if(size > 0) {
            setSong((listPosition + 1) % size);
            playSong();
        }
    }

    @Override
    public void playPrevSong() {
        // TODO mixed version
        int size = playlist.size();
        if(size > 0) {
            setSong((listPosition - 1 + size) % size);
            playSong();
        }
    }

    @Override
    public void setRepeatMode(RepeatMode mode) {
        this.repeatMode = mode;
    }

    @Override
    public void advanceRepeatMode() {
        RepeatMode[] values = RepeatMode.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(repeatMode)) {
                repeatMode = values[(i + 1) % values.length];
                return;
            }
        }
    }

    @Override
    public boolean isMixedMode() {
        return isMixedMode;
    }

    @Override
    public boolean flipMixedMode() {
        isMixedMode = !isMixedMode;
        return isMixedMode;
    }

    @Override
    public void play(int position) {
        if (position >= 0 && position < playlist.size()) {
            setSong(position);
            playSong();
        }
    }

    @Override
    public void play(MuzixSong song) {
        play(playlist.indexOf(song));
    }

    @Override
    public void play(PlaylistData playlistData, List<MuzixSong> list, int position) {
        if (this.playlistData != null && this.playlistData.equals(playlistData)) {
            play(position);
        } else {
            setList(playlistData, list);
            play(position);
        }
    }

    @Override
    public void pause() {
        pausePlayer();
    }

    @Override
    public void stop() {
        stopPlayer();
    }

    @Override
    public boolean seekTo(int position) {
        mediaPlayer.seekTo(position);
        return true;
    }


    @Override
    public boolean isPaused() {
        return isPause;
    }

    @Override
    public boolean isStopped() {
        return !isPause && !mediaPlayer.isPlaying();
    }

}
