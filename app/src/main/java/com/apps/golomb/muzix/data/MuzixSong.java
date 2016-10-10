package com.apps.golomb.muzix.data;

import com.apps.golomb.muzix.utils.Utils;
import com.libs.golomb.extendedrecyclerview.DataExtractor.SectionListDataExtractor;

/**
 * Created by golomb on 15/07/2016.
 * represent a song file
 */
public class MuzixSong extends MuzixEntity implements SectionListDataExtractor.SectionElement<String> {

    public MuzixSong(long id, String title, String artist, int duration) {
        super(id, title, duration,artist);
    }


    public String getDetails() {
        return Utils.formatMillis(this.getDuration());
    }

    @Override
    public MuzixSong getFirst() {
        return this;
    }

    @Override
    public String getSection() {
        return String.valueOf(this.title.charAt(0));
    }

    @Override
    public String getSectionName() {
        return getSection();
    }
}
