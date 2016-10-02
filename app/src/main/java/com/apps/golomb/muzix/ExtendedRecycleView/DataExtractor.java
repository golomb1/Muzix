package com.apps.golomb.muzix.ExtendedRecycleView;

/**
 * Created by golomb on 13/07/2016.
 * represent a data source
 * TODO - include Header, footer, items, filters
 * @param <T> the data type
 */
public interface DataExtractor<T,VH extends ExtendedViewHolder<T>>{

    public static final int ITEM = 0;
    public static final int NO_ITEM = 1;
    public static final int FOOTER = 2;
    public static final int HEADER = 3;

    /***
     * Get the item type of the item in the position position.
     * @param position of the item
     * @return the type
     */
    int GetItemType(int position);

    /***
     * @return the size of all the data set, without the playlist_header & footer
     */
    int size();

    /***
     * This method return the item data in a given position,
     * this method does not include the headers or footer, only the items.
     * for example, position 0 is a data item even if there is a header.
     * @param position ot the item
     * @return the item
     */
    T get(int position);

    /**
     * @param position of the item
     * @return the id
     */
    long getItemId(int position);


    void bindViewHolder(VH holder, int position);


    void onItemMove(ExtendedRecycleAdapter<T, VH> adapter, int fromPosition, int toPosition);


    void onItemSwap(ExtendedRecycleAdapter<T, VH> adapter, int position);

    boolean hasFooter();

    boolean hasHeader();

}
