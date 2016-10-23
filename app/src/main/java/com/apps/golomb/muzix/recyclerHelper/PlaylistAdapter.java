package com.apps.golomb.muzix.recyclerHelper;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.dataoperator.DataProvider;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerEvent;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerOperator;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;

/**
 * Created by tomer on 10/10/2016.
 * This class extend FlexibleAdapter2 in order to support more functionality like fast-scroll.
 * Functionality:
 * fast scroll
 */

@SuppressWarnings("unchecked")
public class PlaylistAdapter<T extends IFlexible> extends FlexibleAdapter<T> {

    private List<T> elements;
    private boolean isInActionMode = false;

    private PlaylistData playlistData;
    // The list id of that this adapter is represent
    private int playedIndex = -1;

    private SparseArray<ExtendedViewHolder> viewHolders;
    private List<Integer> selectedPositions;
    private List<T> selectedItems;

    private GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return PlaylistAdapter.this.getSpanSize(position);
        }
    };

    private MediaPlayerOperator mediaOperator;
    private DataProvider dataOperator;

    private IHeader mTopHeader;
    private Integer itemType;
    private ViewGroup stickySectionHeadersHolder;
    private boolean showBubble;

    /**********************************************************************************************/
    //                                      Constrictors                                          //
    /**********************************************************************************************/

    @SuppressWarnings("unused")
    public PlaylistAdapter(@Nullable List items,int itemType, PlaylistData playlistData) {
        super(items);
        this.playlistData = playlistData;
        init(items);
        this.itemType = itemType;
    }

    @SuppressWarnings("unused")
    public PlaylistAdapter(@Nullable List items,int itemType, PlaylistData playlistData, @Nullable Object listeners) {
        super(items, listeners);
        this.playlistData = playlistData;
        init(items);
        this.itemType = itemType;
    }

    @SuppressWarnings("unused")
    public PlaylistAdapter(@Nullable List items,int itemType, PlaylistData playlistData, @Nullable Object listeners, boolean stableIds) {
        super(items, listeners, stableIds);
        this.playlistData = playlistData;
        init(items);
        this.itemType = itemType;
    }

    @SuppressWarnings("unused")
    public PlaylistAdapter(int itemType, @Nullable Object listeners, boolean stableIds, PlaylistData playlistData) {
        super(new ArrayList<T>(), listeners, stableIds);
        this.playlistData = playlistData;
        init(null);
        this.itemType = itemType;
    }

    private void init(List<T> items) {
        setElementList(items);
        selectedPositions = new ArrayList<>();
        selectedItems = new ArrayList<>();
        viewHolders = new SparseArray<>();
        showBubble = false;
    }

    @Override
    public void updateDataSet(@Nullable List<T> items, boolean animate) {
        ExtendedViewHolder holder = viewHolders.get(playedIndex, null);
        if (holder != null && holder.isInPlayingMode()) {
            holder.unbindInPlayedMode();
        }
        setElementList(items);
        super.updateDataSet(items, animate);
        if (mTopHeader != null) {
            super.addSection(mTopHeader);
        }
    }

    public void setTopHeader(IHeader mTopHeader) {
        this.mTopHeader = mTopHeader;
        if (mTopHeader != null) {
            super.addSection(mTopHeader);
        }
    }

    private void setElementList(List<T> items) {
        if (items != null) {
            elements = new ArrayList<>(items);
        } else {
            elements = new ArrayList<>();
        }
    }

    public List<T> getElementList() {
        return elements;
    }

    //**********************************************************************************************
    //                                 Adapter State
    //**********************************************************************************************

    public void setPlaylistData(PlaylistData playlistData) {
        this.playlistData = playlistData;
    }


    public PlaylistData getPlaylistData() {
        return playlistData;
    }

    private int getListId() {
        if (playlistData != null) {
            return playlistData.getId();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    //**********************************************************************************************
    //                                 Media player operator
    //**********************************************************************************************


    public void setMediaOperator(MediaPlayerOperator mediaOperator) {
        this.mediaOperator = mediaOperator;
    }

    public MediaPlayerOperator getMediaOperator() {
        return this.mediaOperator;
    }


    public DataProvider getDataOperator() {
        return dataOperator;
    }

    public void setDataOperator(DataProvider dataOperator) {
        this.dataOperator = dataOperator;
    }

    /**********************************************************************************************/
    //                                       Fast scroll                                          //
    /**********************************************************************************************/

    @Override
    public String onCreateBubbleText(int position) {
        if(!showBubble){
            return null;
        }
        else {
            IFlexible iFlexible = getItem(position);
            if (iFlexible instanceof ISectionable) {
                return ((ISectionable) iFlexible).getHeader().toString().substring(0, 1).toUpperCase();
            } else if (iFlexible instanceof IHeader) {
                return (iFlexible).toString().substring(0, 1).toUpperCase();
            } else {
                return null;
            }
        }
    }


    //**********************************************************************************************
    //                                   Span Grid View
    //**********************************************************************************************

    private int getSpanSize(int position) {
        IFlexible iFlexible = getItem(position);
        RecyclerView.LayoutManager manager = this.getRecyclerView().getLayoutManager();
        if (iFlexible instanceof SpanFlexible && manager instanceof GridLayoutManager) {
            return ((SpanFlexible) iFlexible).getSpan((GridLayoutManager) manager);
        }
        return 1;
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return spanSizeLookup;
    }


    //**********************************************************************************************
    //                                       Playing
    //**********************************************************************************************

    /**
     * This function will add the view holder to the pool of the view holders with it's new index
     * also, this method if the view holder was in played previously then exit the played mode
     * or if the item is equal to the playing song then force it to enter playing mode
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ExtendedViewHolder) {
            ExtendedViewHolder extendedViewHolder = (ExtendedViewHolder) holder;
            int oldIndex = getItemCountOfTypesUntil(position, getItemTypes());
            super.onBindViewHolder(holder, position);
            int holderNewIndex = getItemCountOfTypesUntil(position, getItemTypes());
            register(oldIndex, holderNewIndex, extendedViewHolder);
            if (mediaOperator != null && !mediaOperator.isStopped()) {
                if (getListId() == mediaOperator.getListId()) {
                    if (oldIndex == playedIndex) {
                        extendedViewHolder.unbindInPlayedMode();
                    }
                    if (holderNewIndex == playedIndex) {
                        extendedViewHolder.bindInPlayedMode();
                    }
                }
            }
            if (selectedPositions.contains(oldIndex)) {
                extendedViewHolder.unbindInActiveMode();
            }
            if (selectedPositions.contains(holderNewIndex)) {
                extendedViewHolder.bindInActiveMode(selectedPositions.indexOf(holderNewIndex));
            }
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (holder instanceof ExtendedViewHolder) {
            ExtendedViewHolder extendedViewHolder = (ExtendedViewHolder) holder;
            int oldIndex = extendedViewHolder.getItemPosition();
            super.onBindViewHolder(holder, position, payloads);
            int holderNewIndex = getItemCountOfTypesUntil(position, getItemTypes());
            register(oldIndex, holderNewIndex, extendedViewHolder);
            extendedViewHolder.setItemPosition(holderNewIndex);
            if (extendedViewHolder.isInPlayingMode()) {
                extendedViewHolder.unbindInPlayedMode();
            }
            if (mediaOperator != null && !mediaOperator.isStopped()) {
                if (getListId() == mediaOperator.getListId()) {
                    if (holderNewIndex == playedIndex) {
                        extendedViewHolder.bindInPlayedMode();
                    }
                }
            }
            extendedViewHolder.unbindInActiveMode();
            Log.d("TGolomb","Bind!!! " + holderNewIndex);
            if (selectedPositions.contains(holderNewIndex)) {
                extendedViewHolder.bindInActiveMode(selectedPositions.indexOf(holderNewIndex));
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    private void register(int oldIndex, int newIndex, ExtendedViewHolder viewHolder) {
        if (viewHolders.get(oldIndex, null) == viewHolder) {
            viewHolders.remove(oldIndex);
            viewHolders.put(newIndex, viewHolder);
        } else {
            viewHolders.put(newIndex, viewHolder);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSongChanged(MediaPlayerEvent.ListPositionEvent event) {
        if (mediaOperator != null) {
            if (mediaOperator.getListId() == this.getListId()) {
                turnOffPlayed();
                playedIndex = mediaOperator.getListPosition();
                turnOnPlayed();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListChanged(MediaPlayerEvent.ListChangeEvent event) {
        Log.d("TGolomb","onListChanged");
        if (mediaOperator != null) {
            if (mediaOperator.getListId() == this.getListId()) {
                turnOffPlayed();
                playedIndex = mediaOperator.getListPosition();
                turnOnPlayed();
            } else {
                turnOffPlayed();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemRemoved(MediaPlayerEvent.ListItemRemoved event) {
        if (mediaOperator != null) {
            Log.d("TGolomb","this is my id" + this.getListId());
            Log.d("TGolomb","this is played id" + mediaOperator.getListId() );
            if (mediaOperator.getListId() == this.getListId()) {
                if (playedIndex == event.getPosition()) {
                    turnOffPlayed();
                    playedIndex = mediaOperator.getListPosition();
                    updateRegistrationOnDelete(event.getPosition());
                    turnOnPlayed();
                }
                else {
                    updateRegistrationOnDelete(event.getPosition());
                    playedIndex = mediaOperator.getListPosition();
                }
            } else {
                Log.d("TGolomb","AAAAAAAAAAAAAAAAAAAAA" );
                turnOffPlayed();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemInserted(MediaPlayerEvent.ListItemInserted event) {
        if (mediaOperator != null) {
            if (mediaOperator.getListId() == this.getListId()) {
                Log.d("TGolomb","OnListItemInserted : true");
                updateRegistrationOnInsert(event.getPosition(),1);
                if (playedIndex <= event.getPosition()) {
                    playedIndex = mediaOperator.getListPosition();
                }
            } else {
                Log.d("TGolomb","OnListItemInserted : false");
                turnOffPlayed();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemsInserted(MediaPlayerEvent.ListItemsInserted event) {
        if (mediaOperator != null) {
            if (mediaOperator.getListId() == this.getListId()) {
                updateRegistrationOnInsert(event.getPosition(),event.getItems().size());
                if (playedIndex <= event.getPosition()) {
                    playedIndex = mediaOperator.getListPosition();
                }
            } else {
                turnOffPlayed();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemMoved(final MediaPlayerEvent.ListItemMoved event) {
        if (mediaOperator != null) {
            if (mediaOperator.getListId() == this.getListId()) {
                turnOffPlayed();
            }
            else{
                turnOffPlayed();
            }
        }
    }

    private void updateRegistrationOnDelete(int position) {
        SparseArray<ExtendedViewHolder> newViewHolderSet = new SparseArray<>();
        for (int i = 0; i < viewHolders.size(); i++) {
            ExtendedViewHolder holder = viewHolders.valueAt(i);
            int index = viewHolders.keyAt(i);
            if (index > position) {
                newViewHolderSet.put(index - 1, holder);
            } else if (index != position)  {
                newViewHolderSet.put(index, holder);
            }
        }
        viewHolders = newViewHolderSet;
    }

    private void updateRegistrationOnInsert(int position, int offset) {
        SparseArray<ExtendedViewHolder> newViewHolderSet = new SparseArray<>();
        for (int i = 0; i < viewHolders.size(); i++) {
            ExtendedViewHolder holder = viewHolders.valueAt(i);
            int index = viewHolders.keyAt(i);
            if (index >= position) {
                newViewHolderSet.put(index +offset, holder);
            } else if (index < position)  {
                newViewHolderSet.put(index, holder);
            }
        }
        viewHolders = newViewHolderSet;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerStateChanged(MediaPlayerEvent.PlayerStateEvent event) {
        if (mediaOperator != null) {
            if (mediaOperator.getListId() == this.getListId()) {
                if (mediaOperator.isStopped()) {
                    turnOffPlayed();
                } else {
                    this.playedIndex = mediaOperator.getListPosition();
                    turnOnPlayed();
                }
            }
        }
    }

    private void turnOnPlayed() {
        Log.d("TGolomb", "turnOnPlayed - playedIndex : " + playedIndex);
        ExtendedViewHolder holder = viewHolders.get(playedIndex, null);
        if (holder != null && !holder.isInPlayingMode()) {
            holder.enterPlayedMode();
        }
    }

    private void turnOffPlayed() {
        Log.d("TGolomb", "turnoff - playedIndex : " + playedIndex);
        ExtendedViewHolder holder = viewHolders.get(playedIndex, null);
        if (holder != null && holder.isInPlayingMode()) {
            holder.exitPlayedMode();
        }
    }

    //**********************************************************************************************
    //                                       Selecting
    //**********************************************************************************************


    @SuppressWarnings("SuspiciousMethodCalls")
    public void toggleSelectedElement(@IntRange(from = 0L) int position) {
        int index = getItemCountOfTypesUntil(position, getItemTypes());
        if (index != -1) {
            if (selectedPositions.contains(index)) {
                // remove and update next
                this.selectedItems.remove(getItem(position));
                int selectedNo = selectedPositions.indexOf(index);
                int inactiveViewHolderIndex = selectedPositions.remove(selectedNo);
                ExtendedViewHolder removedHolder = viewHolders.get(inactiveViewHolderIndex, null);
                if (removedHolder != null) {
                    removedHolder.exitActiveMode();
                }
                while (selectedNo < selectedPositions.size()) {
                    ExtendedViewHolder holder = viewHolders.get(selectedPositions.get(selectedNo), null);
                    if (holder != null) {
                        holder.updateActiveMode(selectedNo);
                    }
                    selectedNo++;
                }
            } else {
                selectedPositions.add(index);
                this.selectedItems.add(getItem(position));
                ExtendedViewHolder holder = viewHolders.get(index, null);
                if (holder != null) {
                    holder.enterActiveMode(selectedPositions.size() - 1);
                }
            }
        }
    }

    @Override
    public void clearSelection() {
        super.clearSelection();
        for (Integer index : selectedPositions) {
            ExtendedViewHolder holder = viewHolders.get(index, null);
            if (holder != null && holder.isAttachedToWindows()) {
                holder.exitActiveMode();
            }
        }
        selectedPositions.clear();
        this.selectedItems.clear();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(holder instanceof ExtendedViewHolder){
            ((ExtendedViewHolder)holder).attachedToWindow();
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder instanceof ExtendedViewHolder){
            ((ExtendedViewHolder)holder).detachedFromWindow();
        }
    }

    public List<T> getSelectedItems() {
        return selectedItems;
    }


    //**********************************************************************************************
    //                                       Headers
    //**********************************************************************************************

    public IHeader getHeaderOf(@NonNull T item) {
        if (item instanceof ISectionable) {
            return ((ISectionable) item).getHeader();
        }
        return null;
    }

    private Integer getItemTypes() {
        return itemType;
    }

    public int getItemPosition(int position) {
        return getItemCountOfTypesUntil(position, getItemTypes());
    }

    public void enterActionMode() {
        isInActionMode = true;
    }


    public void exitActionMode() {
        isInActionMode = false;
    }

    public boolean isInActionMode() {
        return isInActionMode;
    }

    public void setStickySectionHeadersHolder(ViewGroup stickySectionHeadersHolder) {
        this.stickySectionHeadersHolder = stickySectionHeadersHolder;
    }

    @Override
    public ViewGroup getStickySectionHeadersHolder() {
        if(this.stickySectionHeadersHolder != null){
            return stickySectionHeadersHolder;
        }
        return super.getStickySectionHeadersHolder();
    }

    public void removeElementItem(int position) {
        this.elements.remove(position);
        int globalPosition = position;
        if(mTopHeader != null) {
            globalPosition++;
        }
        removeItem(globalPosition);
    }

    public void addElementItem(int position, T item) {
        int globalPosition = position;
        if(mTopHeader != null) {
            globalPosition++;
        }
        if(globalPosition >= 0) {
            Log.d("TGolomb","1111GP : " + globalPosition + "/" + getItemCount());
            addItem(globalPosition, item);
            elements.add(position,item);
        }
    }

    public void addElementItems(int position, List<T> items) {
        int globalPosition = position;
        if(mTopHeader != null) {
            globalPosition++;
        }
        if(globalPosition >= 0) {
            Log.d("TGolomb","GP : " + globalPosition + "/" + getItemCount());
            addItems(globalPosition, items);
            elements.addAll(position,items);
        }
    }

    public void moveElementItem(int from, int to) {
        elements.add(to,elements.remove(from));
        if(mTopHeader != null) {
            updateDataSet(elements);
        }
        else{
            updateDataSet(elements);
        }
    }

    @Override
    protected boolean filterObject(T item, String constraint) {
        if (item instanceof MuzixSong) {
            MuzixSong filterable = (MuzixSong) item;
            return filterable.getTitle().toLowerCase().contains(constraint);
        }
        return false;
    }

    public boolean isShowBubble() {
        return showBubble;
    }

    public void setShowBubble(boolean showBubble) {
        this.showBubble = showBubble;
    }
}
