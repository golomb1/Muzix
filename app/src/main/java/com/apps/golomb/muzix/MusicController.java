package com.apps.golomb.muzix;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by tomer on 08/09/2016.
 * This class was made in order to override the media controller for the sake of the media player.
 */
public class MusicController  extends MediaController {

    public MusicController(Context context) {
        super(context);
    }

    // we override this methods so that the controller won't disappear after few sec
    @Override
    public void hide(){}
}
