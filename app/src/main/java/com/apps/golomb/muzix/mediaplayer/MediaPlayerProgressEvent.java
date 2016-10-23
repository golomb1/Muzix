package com.apps.golomb.muzix.mediaplayer;

/**
 * This class represent a progress in a media player.
 * Created by tomer on 01/10/2016.
 */

public class MediaPlayerProgressEvent {
    private int position;

    MediaPlayerProgressEvent(int position) {
        this.position = position;
    }
    public int getPosition() {
        return position;
    }
}
