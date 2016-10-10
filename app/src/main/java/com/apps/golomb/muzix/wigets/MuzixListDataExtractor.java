package com.apps.golomb.muzix.wigets;

import com.apps.golomb.muzix.data.MuzixSong;
import com.libs.golomb.extendedrecyclerview.DataExtractor.SimpleListDataExtractor;
import com.libs.golomb.extendedrecyclerview.viewholder.ExtendedViewHolder;

import java.util.List;

/**
 * Created by golomb on 22/07/2016.
 * This is a muzix entity list data extractor.
 */
public class MuzixListDataExtractor extends SimpleListDataExtractor<MuzixSong> {

    public MuzixListDataExtractor(List<? extends MuzixSong> list) {
        super(list);
    }

    public MuzixListDataExtractor(List<? extends MuzixSong> list, boolean header, boolean footer) {
        super(list,header,footer);
    }
}
