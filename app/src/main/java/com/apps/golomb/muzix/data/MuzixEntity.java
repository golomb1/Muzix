package com.apps.golomb.muzix.data;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.apps.golomb.muzix.recyclerHelper.ExtendedViewHolder;
import com.apps.golomb.muzix.recyclerHelper.SectionHeader;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;

/**
 * Created by golomb on 15/07/2016.
 * Represent a muzix entity.
 */
public abstract class MuzixEntity<T extends RecyclerView.ViewHolder>
        extends AbstractFlexibleItem<T>
        implements Comparable<MuzixEntity>,
        ISectionable<T,IHeader> {

    public static long PLAYLIST = -1;

    protected long id;
    protected String title;
    private int duration;
    private long artistId;
    private String artist;


    MuzixEntity(long id, String title, int duration, String artist) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.artist = artist;
    }

    public MuzixEntity(long id, String title, int duration, long artistId, String artist) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.artistId = artistId;
        this.artist = artist;
    }

    /**********************************************************************************************/
    //                                      class Logic                                           //
    /**********************************************************************************************/

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



    /**********************************************************************************************/
    //                                  Flexible Header Logic                                     //
    /**********************************************************************************************/


    @Override
    public SectionHeader getHeader() {
        return SectionHeader.getInstance(this.title.substring(0,1));
    }

    @Override
    public void setHeader(IHeader header) {

    }

    /**********************************************************************************************/
    //                                         Other                                              //
    /**********************************************************************************************/

    /***
     *
     * @param o - the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(@NonNull MuzixEntity o) {
        return getTitle().compareTo(o.getTitle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MuzixEntity)) return false;

        MuzixEntity<?> that = (MuzixEntity<?>) o;

        return getId() == that.getId() &&
                getDuration() == that.getDuration() &&
                getTitle().equals(that.getTitle()) &&
                getArtist().equals(that.getArtist());

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getDuration();
        result = 31 * result + getArtist().hashCode();
        return result;
    }
}
