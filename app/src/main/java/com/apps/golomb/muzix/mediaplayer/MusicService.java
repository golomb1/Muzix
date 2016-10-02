package com.apps.golomb.muzix.mediaplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.apps.golomb.muzix.data.MuzixSong;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.List;




/***
 * This class responsible to play music, this class should operate without the need of an a activity.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private EventBus eventBus = EventBus.getDefault();

    private MediaObserver observer;
    private List<MuzixSong> playList;
    private int songPosition = 0;
    // media player
    private MediaPlayer mediaPlayer;
    private final IBinder musicBind = new MusicService.MusicBinder();




    public MusicService() {
    }




    @Override
    public void onCreate(){
        //create the service
        super.onCreate();
        songPosition = 0;

        // create the player
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();

        observer = new MediaObserver(mediaPlayer);
    }


    /**
     * initialize the music player.
     */
    public void initMusicPlayer(){
        //set player properties
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }


    public List<MuzixSong> getList() {
        return playList;
    }

    public void setList(List<MuzixSong> list){
        playList = list;
    }

    public int getSongPosition(){
        return songPosition;
    }

    public void setSong(int songIndex){
        songPosition=songIndex;
    }



    public void playSong(){
        //play a song
        // first we need to reset the media player
        mediaPlayer.reset();

        // get song
        MuzixSong playSong = playList.get(songPosition);
        //get id
        long currSong = playSong.getId();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
            mediaPlayer.prepareAsync();
            eventBus.postSticky(new PlayerStateEvent(playSong));
            Logger.d("Start Playing");
        }
        catch(Exception e){
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
    public boolean onUnbind(Intent intent){
        stopPlayer();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopPlayer();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // the media player is ready to start playing
        // start the playback
        startPlayer();
    }

    public void playOrPause() {
        Logger.d("Play or pause" + mediaPlayer.isPlaying(), mediaPlayer.isPlaying());
        if(mediaPlayer.isPlaying()){
            pausePlayer();
        }
        else{
            startPlayer();
        }
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public int getPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mediaPlayer.getDuration();
    }

    public boolean isPng(){
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            observer.stop();
            eventBus.post(new MediaPlayerStateEvent(MediaPlayerStateEvent.PAUSE));
        }
    }

    public void stopPlayer(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            observer.stop();
            eventBus.post(new MediaPlayerStateEvent(MediaPlayerStateEvent.STOPPED));
        }
    }

    public void startPlayer(){
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            observer.start();
            eventBus.post(new MediaPlayerStateEvent(MediaPlayerStateEvent.PLAYING));
        }
    }

    public void seek(int position){
        mediaPlayer.seekTo(position);
    }

    public void playPrev(){
        // TODO
        songPosition--;
        if(songPosition < 0){
            songPosition = playList.size()-1;
        }
        playSong();
    }

    //skip to next song
    public void playNext(){
        // TODO
        songPosition++;
        if(songPosition >= playList.size()) {
            songPosition=0;
        }
        playSong();
    }
}
