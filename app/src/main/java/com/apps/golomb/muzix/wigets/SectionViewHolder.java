package com.apps.golomb.muzix.wigets;

import android.view.View;
import android.widget.TextView;

import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;
import com.libs.golomb.extendedrecyclerview.DataExtractor.DataExtractor;
import com.libs.golomb.extendedrecyclerview.DataExtractor.SectionListDataExtractor;
import com.libs.golomb.extendedrecyclerview.ExtendedRecycleAdapter;
import com.libs.golomb.extendedrecyclerview.viewholder.ExtendedViewHolder;

/**
 * Created by tomer on 05/10/2016.
 */

public class SectionViewHolder<K,T extends SectionListDataExtractor.SectionElement<K>> extends ExtendedViewHolder<T> {

    private TextView mSectionText;

    public SectionViewHolder(View itemView, ExtendedRecycleAdapter adapter) {
        super(itemView, adapter);
        mSectionText = (TextView) itemView.findViewById(R.id.section_text);
    }

    @Override
    public boolean isSwappable() {
        return false;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public int getType() {
        return SectionListDataExtractor.SECTION;
    }

    @Override
    public void bind(DataExtractor<T, ExtendedViewHolder<T>> mDataExtractor, int position, int itemType) {
        mSectionText.setText(mDataExtractor.getAt(position + 1).getSectionName());
    }
}
