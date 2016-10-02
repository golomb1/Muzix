package com.apps.golomb.muzix.wigets;

import com.apps.golomb.muzix.ExtendedRecycleView.ExtendedViewHolder;
import com.apps.golomb.muzix.ExtendedRecycleView.ListDataExtractor;
import com.apps.golomb.muzix.ExtendedRecycleView.ListFooterViewHolder;
import com.apps.golomb.muzix.data.MuzixEntity;
import com.apps.golomb.muzix.data.MuzixSong;

import java.util.List;

/**
 * Created by golomb on 22/07/2016.
 * This is a muzix entity list data extractor.
 */
public class MuzixListDataExtractor extends ListDataExtractor<MuzixSong> {

    public MuzixListDataExtractor(List<? extends MuzixSong> list) {
        super(list);
    }

    public MuzixListDataExtractor(List<? extends MuzixSong> list, boolean header, boolean footer) {
        super(list,header,footer);
    }

    @Override
    protected void bind(ExtendedViewHolder<MuzixSong> holder, int position, int type) {
        if(type == ITEM){
            if(holder instanceof MuzixViewHolder) {
                ((MuzixViewHolder) holder).bind(get(position));
                ((MuzixViewHolder) holder).setType(type);
            }
        }
        if(type == FOOTER && hasFooter()){
            if(holder instanceof ListFooterViewHolder) {
                ((ListFooterViewHolder) holder).setText(list.size());
            }
        }
        if(type == HEADER && hasHeader()){
            if(holder instanceof MuzixHeaderViewHolder) {
                // TODO
                // ((MuzixHeaderViewHolder) holder).bind(albumImage);
            }
        }
    }
}
