package com.apps.golomb.muzix;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by tomer on 08/09/2016.
 */
public class MusicController  extends MediaController {

    public MusicController(Context context) {
        super(context);
    }

    // we override this methods so that the controller won't disappear after few sec
    @Override
    public void hide(){}
}
