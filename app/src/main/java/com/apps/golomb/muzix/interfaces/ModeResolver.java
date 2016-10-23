package com.apps.golomb.muzix.interfaces;

import android.support.design.widget.FloatingActionButton;

import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerOperator;
import com.apps.golomb.muzix.mediaplayer.MusicService;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.ActionModeHelper;

/**
 * Created by tomer on 19/10/2016.
 * This interface contains the relatives operations that need to be perform when in a specific mode.
 */

public interface ModeResolver {
    PlaylistAdapter getAdapter();

    void toggleSelectedElement(int position);

    boolean handleClick(int position, ActionModeHelper mActionModeHelper);

    void enterActionMode();

    void exitActionMode();

    void onScroll(boolean scrolling);

    ModeManager.Mode getMode();

    void destroy(MusicService musicSrv);

    int getPlaylistId();
}
