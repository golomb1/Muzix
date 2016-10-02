package com.apps.golomb.muzix.mediaplayer;

/**
 * This class represent a progress in a media player.
 * Created by tomer on 01/10/2016.
 */

public class MediaPlayerProgressEvent {
    private int position;
    private int duration;

    public MediaPlayerProgressEvent(int position, int duration) {
        this.position = position;
        this.duration = duration;
    }

    public int getDuration(){
        return duration;
    }

    public int getPosition() {
        return position;
    }
}
