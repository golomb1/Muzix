package com.apps.golomb.muzix.mediaplayer;

import android.media.MediaPlayer;
import com.orhanobut.logger.Logger;
import org.greenrobot.eventbus.EventBus;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle the observation on a media player.
 * Created by tomer on 01/10/2016.
 */

class MediaObserver implements Runnable {
    private MediaPlayer player;
    private AtomicBoolean isRunning = new AtomicBoolean(false);


    MediaObserver(MediaPlayer player){
        this.player = player;
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
        Logger.d("Observer Start Running");
        while (!isRunning.get()) {
            try {
                if (player.isPlaying())
                    sendMsgToUI(player.getCurrentPosition(),
                            player.getDuration());
            } catch (Exception e){e.printStackTrace();}
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        Logger.d("Observer stop Running");
    }

    //handles all the background threads things for you
    private void sendMsgToUI(int position, int duration) {
        MediaPlayerProgressEvent event = new MediaPlayerProgressEvent(position,duration);
        EventBus.getDefault().post(event);
    }
}
