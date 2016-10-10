package com.apps.golomb.muzix.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomer on 05/10/2016.
 */

public class Playlist extends MuzixEntity {

    private List<MuzixSong> list;

    public Playlist(long id, String title, int duration, String artist) {
        this(id, title, duration, artist, new ArrayList<MuzixSong>());
    }

    public Playlist(long id, String title, int duration, String artist, List<MuzixSong> list) {
        super(id, title, duration, artist);
        this.list = list;
    }

    @Override
    public MuzixSong getFirst() {
        return null;
    }
}
