package com.apps.golomb.muzix.mediaplayer;

import com.apps.golomb.muzix.data.MuzixSong;

/**
 * This class represent an media Player state.
 * Created by tomer on 01/10/2016.
 */

public class MediaPlayerStateEvent {
    public static final int PLAYING = 0;
    public static final int STOPPED = 1;
    public static final int PAUSE   = 2;

    private int state;

    public MediaPlayerStateEvent(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
