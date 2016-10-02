package com.apps.golomb.muzix.data;

import com.apps.golomb.muzix.utils.Utils;

/**
 * Created by golomb on 15/07/2016.
 * represent a song file
 */
public class MuzixSong extends MuzixEntity{
    private String artist;

    public MuzixSong(long id, String title, String artist, int duration) {
        super(id, title, duration);
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDetails() {
        return Utils.formatMillis(this.getDuration());
    }

    @Override
    public MuzixSong getFirst() {
        return this;
    }
}
