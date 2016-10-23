package com.apps.golomb.muzix.recyclerHelper;


import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.apps.golomb.muzix.R;
import java.util.HashMap;
import java.util.List;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by tomer on 10/10/2016.
 * This class is Section Element & ViewHolder as a set together with pool of singletons.
 */

@SuppressWarnings("WeakerAccess")
public class SectionHeader extends AbstractHeaderItem<FlexibleViewHolder> implements SpanFlexible {

    /**********************************************************************************************/
    //                                      Static logic                                          //
    /**********************************************************************************************/
    private static HashMap<String,SectionHeader> sectionHashMap = new HashMap<>();

    public static SectionHeader getInstance(String section) {
        if(sectionHashMap.containsKey(section)){
            return sectionHashMap.get(section);
        }
        else{
            SectionHeader stringSection = new SectionHeader(section);
            sectionHashMap.put(section, stringSection);
            return stringSection;
        }
    }


    /**********************************************************************************************/
    //                                      instance Logic                                        //
    /**********************************************************************************************/

    private String header;

    public SectionHeader(String header) {
        this.header = header;
        setHidden(false);
    }

    public String getHeaderValue() {
        return header;
    }

    /**
     * The Adapter is provided, because it will become useful for the MyViewHolder.
     * The unique instance of the LayoutInflater is also provided to simplify the
     * creation of the VH.
     */
    @Override
    public SectionViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
                                                    ViewGroup parent) {
        return new SectionViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter,true);
    }

    /**
     * Also here the Adapter is provided to get more specific information from it.
     * NonNull Payload is provided as well, you should use it more often.
     */
    @Override
    public void bindViewHolder(FlexibleAdapter adapter, FlexibleViewHolder holder, int position,
                               List payloads) {
        if(holder instanceof SectionViewHolder) {
            ((SectionViewHolder)holder).bind(this);
        }
    }


    /**
     * For the list_item type we need an int value: the layoutResID is sufficient.
     */
    @Override
    public int getLayoutRes() {
        return R.layout.list_section;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectionHeader)) return false;
        SectionHeader that = (SectionHeader) o;
        return getHeaderValue() != null ? getHeaderValue().equals(that.getHeaderValue()) : that.getHeaderValue() == null;
    }

    @Override
    public int hashCode() {
        return getHeaderValue() != null ? getHeaderValue().hashCode() : 0;
    }

    @Override
    public String toString() {
        return header;
    }

    @Override
    public int getSpan(GridLayoutManager manager) {
        return manager.getSpanCount();
    }



    public class SectionViewHolder extends FlexibleViewHolder{

        private final TextView mSectionText;

        public SectionViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
            mSectionText = (TextView) view.findViewById(R.id.section_text);
        }

        public void bind(SectionHeader sectionHeader) {
            mSectionText.setText(sectionHeader.getHeaderValue());
        }
    }
}

