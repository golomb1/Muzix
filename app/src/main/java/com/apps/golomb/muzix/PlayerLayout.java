package com.apps.golomb.muzix;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.mediaplayer.MediaPlayerProgressEvent;
import com.apps.golomb.muzix.mediaplayer.MediaPlayerStateEvent;
import com.apps.golomb.muzix.mediaplayer.MusicService;
import com.apps.golomb.muzix.mediaplayer.PlayerStateEvent;
import com.apps.golomb.muzix.utils.Utils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class PlayerLayout implements MediaController.MediaPlayerControl, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private MusicController controller;
    private MusicService musicSrv;
    private boolean musicBound=false;

    // Controls view
    private ImageButton topButton;
    private TextView topTitle;
    private TextView topDetails;
    private ImageButton itemList;
    private ImageButton topMore;

    private ImageView ItemImage;

    private SeekBar progressBar;
    private TextView timePassed;
    private TextView totalTime;

    private ImageButton playButton;

    /**
     * Prepare the layout from the activity.
     * @param activity - that contains the player layout.
     */
    @SuppressWarnings("WeakerAccess")
    public PlayerLayout(Activity activity) {

        // Initialize controls.
        activity.findViewById(R.id.player_control_layout).setOnClickListener(this);

        // Top bar
        topButton = (ImageButton) activity.findViewById(R.id.sliding_top_control);
        topButton.setOnClickListener(this);
        topTitle = (TextView) activity.findViewById(R.id.item_title);
        topDetails = (TextView) activity.findViewById(R.id.item_artist);
        itemList = (ImageButton) activity.findViewById(R.id.item_list);
        topMore = (ImageButton) activity.findViewById(R.id.item_more);

        // Middle screen
        ItemImage = (ImageView) activity.findViewById(R.id.item_album);


        // bottom screen
        progressBar = (SeekBar) activity.findViewById(R.id.progressBar);
        progressBar.setOnSeekBarChangeListener(this);
        timePassed = (TextView) activity.findViewById(R.id.start_time);
        totalTime = (TextView) activity.findViewById(R.id.end_time);


        // bottom screen controls
        playButton = (ImageButton) activity.findViewById(R.id.play_btm);
        playButton.setOnClickListener(this);
        ImageButton prevButton = (ImageButton) activity.findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(this);
        ImageButton nextButton = (ImageButton) activity.findViewById(R.id.next_btn);
        nextButton.setOnClickListener(this);
        ImageButton likeButton = (ImageButton) activity.findViewById(R.id.like_btn);
        likeButton.setOnClickListener(this);
        ImageButton dislikeButton = (ImageButton) activity.findViewById(R.id.dislike_btn);
        dislikeButton.setOnClickListener(this);

        // register to player updates
        EventBus eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    public void destroy(){
        EventBus.getDefault().unregister(this);
    }

    public void setUp(MuzixSong song){
        topTitle.setText(song.getTitle());
        topDetails.setText(song.getArtist());
        totalTime.setText(song.getDetails());
        progressBar.setMax(song.getDuration());
        topTitle.setSelected(true);
        topDetails.setSelected(true);
        //TODO
    }

    @SuppressWarnings("WeakerAccess")
    public void setController(Activity activity, View songListView){
        //set the controller up
        controller = new MusicController(activity);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(songListView);
        controller.setEnabled(true);
    }

    //play next
    private void playNext(){
        musicSrv.playNext();
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        musicSrv.playPrev();
        controller.show(0);
    }

    @Override
    public void start() {
        musicSrv.startPlayer();
    }

    @Override
    public void pause() {
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getPosition();
        else return 0;
    }

    @Override
    public void seekTo(int position) {
        musicSrv.seek(position);
    }

    @Override
    public boolean isPlaying() {
        return musicSrv != null && musicBound && musicSrv.isPng();
    }

    @Override
    public int getBufferPercentage() {
        // we can leave this alone
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        // we can leave this alone
        return 0;
    }

    @SuppressWarnings("WeakerAccess")
    public void setService(MusicService service) {
        this.musicSrv = service;
    }

    @SuppressWarnings("WeakerAccess")
    public void setMusicBound(boolean musicBound) {
        this.musicBound = musicBound;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sliding_top_control:
            case R.id.play_btm: {
                musicSrv.playOrPause();
                break;
            }
            case R.id.next_btn:{
                musicSrv.playNext();
                break;
            }
            case R.id.prev_btn:{
                musicSrv.playPrev();
                break;
            }
            case R.id.like_btn:{
                // TODO
                break;
            }
            case R.id.dislike_btn:{
                // TODO
                break;
            }

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            Logger.d("seek bar changed", progress);
            musicSrv.seek(progress);
            timePassed.setText(Utils.formatMillis(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerStateChange(MediaPlayerStateEvent event) {
        if(event.getState() == MediaPlayerStateEvent.PLAYING){
            playButton.setImageResource(R.drawable.ic_pause_black_24dp);
            topButton.setImageResource(R.drawable.ic_pause_black_24dp);
        }
        if(event.getState() == MediaPlayerStateEvent.PAUSE){
            playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            topButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        if(event.getState() == MediaPlayerStateEvent.STOPPED){
            playButton.setImageResource(R.drawable.ic_stop_black_24dp);
            topButton.setImageResource(R.drawable.ic_stop_black_24dp);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProgressUpdate(MediaPlayerProgressEvent event) {
        timePassed.setText(Utils.formatMillis(event.getPosition()));
        progressBar.setProgress(event.getPosition());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerStateEvent event) {
        topTitle.setText(event.getCurrentSong().getTitle());
        topDetails.setText(event.getCurrentSong().getArtist());
        // TODO load song image
    }
}

