package com.apps.golomb.muzix.mediaplayer;

import com.apps.golomb.muzix.data.MuzixSong;

/**
 * This class responsible to keep the state of the player for every class that needs it, Activity, widget, ect.
 * Created by tomer on 01/10/2016.
 */

public class PlayerStateEvent {
    private MuzixSong currentSong;

    public PlayerStateEvent(MuzixSong currentSong) {
        this.currentSong = currentSong;
    }

    public MuzixSong getCurrentSong() {
        return currentSong;
    }

}
