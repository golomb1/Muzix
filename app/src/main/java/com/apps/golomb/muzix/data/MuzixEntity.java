package com.apps.golomb.muzix.data;

import android.support.annotation.NonNull;

/**
 * Created by golomb on 15/07/2016.
 * Represent a muzix entity.
 */
public abstract class MuzixEntity implements Comparable<MuzixEntity> {

    public static long PLAYLIST = -1;

    protected long id;
    protected String title;
    private int duration;
    private String artist;


    public MuzixEntity(long id, String title, int duration, String artist) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.artist = artist;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    /***
     *
     * @param o - the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(@NonNull MuzixEntity o) {
        return getTitle().compareTo(o.getTitle());
    }

    public abstract MuzixSong getFirst();
}
