package com.apps.golomb.muzix.interfaces;

import com.apps.golomb.muzix.entities.PlaylistData;

/**
 * Created by tomer on 20/10/2016.
 * This interface was made in order to allows ModeResolvers to change the mode if necessary.
 */

public interface ModeManager {
    public enum Mode{
        ALL_TRACKS, LIBRARY, ALBUMS, ARTIST, LIST
    }
    void switchMode(PlaylistData data);
    void restoreMode();
}
