package com.apps.golomb.muzix.mediaplayer;

import android.media.MediaPlayer;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle the observation on a media player.
 * Created by tomer on 01/10/2016.
 */

class MediaObserver implements Runnable {
    private MediaPlayer player;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private EventBus eventBus;

    MediaObserver(MediaPlayer player, EventBus eventBus){
        this.player = player;
        this.eventBus = eventBus;
    }

    void stop() {
        isRunning.set(false);
    }


    public void start() {
        if(!isRunning.get()){
            new Thread(this).start();
        }
    }

    @Override public void run() {
        Log.d("TGolomb","Observer Start Running");
        while (!isRunning.get()) {
            try {
                if (player.isPlaying())
                    sendMsgToUI(player.getCurrentPosition()
                    );
            } catch (Exception e){e.printStackTrace();}
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        Log.d("TGolomb","Observer stop Running");
    }

    //handles all the background threads things for you
    private void sendMsgToUI(int position) {
        MediaPlayerProgressEvent event = new MediaPlayerProgressEvent(position);
        eventBus.post(event);
    }
}
