package com.apps.golomb.muzix.interfaces;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.dataoperator.DataProvider;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerOperator;
import com.apps.golomb.muzix.mediaplayer.MusicService;
import com.apps.golomb.muzix.recyclerHelper.DragItemEvent;
import com.apps.golomb.muzix.recyclerHelper.ListHeader;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;
import com.apps.golomb.muzix.utils.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.helpers.ActionModeHelper;
import eu.davidea.flexibleadapter.items.IFlexible;
import static android.view.View.GONE;

/**
 * Created by tomer on 20/10/2016.
 * This class represent the control of a playlist view.
 */

public class PlaylistResolver implements ModeResolver,
        FlexibleAdapter.OnItemMoveListener,
        FlexibleAdapter.OnItemSwipeListener {

    private final MediaPlayerOperator mOperator;
    private final SlidingUpPanelLayout mSlidingUpPanelLayout;
    private final DataProvider mDataProvider;
    private PlaylistAdapter<MuzixSong> mAdapter;
    private FloatingActionButton mMainFab;
    private FloatingActionButton mSecFab1;
    private FloatingActionButton mSecFab2;
    private PlaylistData mPlaylistData;
    private int dragEventFromPosition = -1;
    private int dragEventToPosition = -1;

    public PlaylistResolver(@NonNull DataProvider dataProvider,
                            @NonNull PlaylistAdapter<MuzixSong> adapter,
                            MediaPlayerOperator operator,
                            @NonNull MusicService service,
                            FloatingActionButton headerFab,
                            FloatingActionButton mainFab,
                            FloatingActionButton secFab1,
                            FloatingActionButton secFab2,
                            SlidingUpPanelLayout slidingUpPanelLayout,
                            PlaylistData playlistData){

        this.mAdapter = adapter;
        this.mOperator = operator;
        this.mSlidingUpPanelLayout = slidingUpPanelLayout;
        this.mPlaylistData = playlistData;
        this.mDataProvider = dataProvider;

        dragEventToPosition = -1;
        dragEventFromPosition = -1;

        mAdapter.setMediaOperator(operator);
        mAdapter.setDataOperator(dataProvider);
        mAdapter.setPlaylistData(playlistData);

        EventBus.getDefault().register(this);
        service.addObserver(mAdapter);
        mAdapter.initializeListeners(this);

        mainFab.setVisibility(GONE);
        secFab1.setVisibility(GONE);
        secFab2.setVisibility(GONE);

        if(headerFab != null) {
            headerFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // floating button was pressed.
                    if (mOperator != null) {
                        mOperator.play(mPlaylistData, mPlaylistData.getList(), 0);
                        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                }
            });
        }

        mMainFab = mainFab;
        mMainFab.setImageResource(R.drawable.ic_playlist_play);
        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // floating button was pressed.
                if(mOperator != null) {
                    mOperator.play(PlaylistData.getTempInstance(), mAdapter.getSelectedItems(),0);
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });
        mSecFab1 = secFab1;
        mSecFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOperator != null){
                    mOperator.playAsNext(mAdapter.getSelectedItems());
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });
        mSecFab2 = secFab2;
        mSecFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOperator != null){
                    mOperator.append(mAdapter.getSelectedItems());
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });

    }


    @Override
    public void destroy(MusicService service) {
        service.removeObserver(mAdapter);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getPlaylistId() {
        return mPlaylistData.getId();
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean handleClick(int position, ActionModeHelper actionModeHelper) {
        if (mAdapter.getMode() != SelectableAdapter.MODE_IDLE && actionModeHelper != null) {
            mAdapter.toggleSelectedElement(position);
            return actionModeHelper.onClick(position);
        } else {
            //Handle the item click listener
            if (mOperator != null && mOperator.boundToService()) {
                IFlexible iFlexible = mAdapter.getItem(position);
                if (iFlexible instanceof MuzixSong) {
                    List<MuzixSong> playlist = mAdapter.getElementList();
                    int index = mAdapter.getItemPosition(position);
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    mOperator.play(mAdapter.getPlaylistData(), playlist, index);
                    return true;
                }
                else if(iFlexible instanceof ListHeader){
                    mOperator.playShuffle(mAdapter.getPlaylistData(),mAdapter.getElementList());
                }
            }
        }
        //We don't need to activate anything
        return false;
    }

    @Override
    public PlaylistAdapter<MuzixSong> getAdapter() {
        return mAdapter;
    }

    @Override
    public void toggleSelectedElement(int position) {
        mAdapter.toggleSelectedElement(position);
    }

    @Override
    public void enterActionMode() {
        mAdapter.exitActionMode();
        Utils.setVisibility(mMainFab,View.VISIBLE);
        Utils.setVisibility(mSecFab1,View.VISIBLE);
        Utils.setVisibility(mSecFab2,View.VISIBLE);
    }

    @Override
    public void exitActionMode() {
        mAdapter.exitActionMode();
        Utils.setVisibility(mMainFab,View.GONE);
        Utils.setVisibility(mSecFab1,View.GONE);
        Utils.setVisibility(mSecFab2,View.GONE);
    }

    @Override
    public void onScroll(boolean scrolling) {
        if (scrolling) {
            if(mAdapter.isInActionMode()){
                Utils.setVisibility(mMainFab, View.GONE);
                Utils.setVisibility(mSecFab1, View.GONE);
                Utils.setVisibility(mSecFab2, View.GONE);
            }
        } else {
            if(mAdapter.isInActionMode()){
                Utils.setVisibility(mMainFab, View.VISIBLE);
                Utils.setVisibility(mSecFab1, View.VISIBLE);
                Utils.setVisibility(mSecFab2, View.VISIBLE);
            }
        }
    }

    @Override
    public ModeManager.Mode getMode() {
        return ModeManager.Mode.LIST;
    }

    @Override
    public boolean shouldMoveItem(int fromPosition, int toPosition) {
        return toPosition >= 1;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //mDataProvider.moveItem(this.mPlaylistData,fromPosition,toPosition);
        // This method is being called on every move of items, should wait to finish of the operation, while saving the positions.
        if (dragEventFromPosition == -1) {
            dragEventFromPosition = mAdapter.getItemPosition(fromPosition);
        }
        dragEventToPosition = mAdapter.getItemPosition(toPosition);
    }

    @Override
    public void onItemSwipe(int position, int direction) {
        Log.d("PlaylistResolver","onItemSwipe");
        mDataProvider.removeSong(this.mPlaylistData,mAdapter.getItemPosition(position));
    }

    @Override
    public void onActionStateChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    @Subscribe
    public void onDragEnd(DragItemEvent event) {
        if (event.getEvent().equals(DragItemEvent.Event.END)) {
            if (mDataProvider != null) {
                if (dragEventFromPosition != dragEventToPosition) {
                    mDataProvider.moveItem(mPlaylistData,dragEventFromPosition, dragEventToPosition);
                }
            }
        } else {
            dragEventFromPosition = -1;
            dragEventToPosition = -1;
        }
    }
}
