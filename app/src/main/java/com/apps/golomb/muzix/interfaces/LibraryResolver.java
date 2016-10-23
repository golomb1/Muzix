package com.apps.golomb.muzix.interfaces;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import com.apps.golomb.muzix.MainActivity;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.dataoperator.DataProvider;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerOperator;
import com.apps.golomb.muzix.mediaplayer.MusicService;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;
import com.apps.golomb.muzix.utils.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import java.util.ArrayList;
import java.util.List;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.helpers.ActionModeHelper;
import eu.davidea.flexibleadapter.items.IFlexible;
import static android.view.View.GONE;

/**
 * Created by tomer on 19/10/2016.
 * Library of playlists, this class responsible to handle all the logic that needed.
 */

public class LibraryResolver implements ModeResolver {
    private PlaylistAdapter<PlaylistData> mAdapter;
    private MediaPlayerOperator mOperator;

    private final FloatingActionButton mMainFab;
    private final FloatingActionButton mSecFab1;
    private final FloatingActionButton mSecFab2;
    private final SlidingUpPanelLayout mSlidingUpPanelLayout;
    private final ModeManager mManager;
    private final DataProvider mDataProvider;

    private static AnimatedVectorDrawable playlistToPlusAVD;
    private static AnimatedVectorDrawable plusToPlaylistAVD;

    public LibraryResolver( @NonNull final MainActivity activity,
                            @NonNull ModeManager manager,
                            @NonNull final PlaylistAdapter<PlaylistData> mAdapter,
                            @NonNull MediaPlayerOperator operator,
                            @NonNull DataProvider dataProvider,
                            SlidingUpPanelLayout slidingUpPanelLayout,
                            FloatingActionButton mainFab,
                            FloatingActionButton secFab1,
                            FloatingActionButton secFab2) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(playlistToPlusAVD == null) {
                playlistToPlusAVD = (AnimatedVectorDrawable) activity.getDrawable(R.drawable.playlist_to_plus);
            }
            if(plusToPlaylistAVD == null) {
                plusToPlaylistAVD = (AnimatedVectorDrawable) activity.getDrawable(R.drawable.plus_to_playlist);
            }
        }

        this.mManager = manager;
        this.mAdapter = mAdapter;
        this.mOperator = operator;

        mAdapter.setMediaOperator(operator);
        mAdapter.setDataOperator(dataProvider);

        mDataProvider = dataProvider;

        this.mSlidingUpPanelLayout = slidingUpPanelLayout;

        mainFab.setVisibility(View.VISIBLE);
        secFab1.setVisibility(GONE);
        secFab2.setVisibility(GONE);

        mMainFab = mainFab;
        mMainFab.setImageResource(R.drawable.ic_add_white);
        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // floating button was pressed.
                if(mOperator != null) {
                    if(mAdapter.isInActionMode()) {
                        mOperator.play(PlaylistData.getTempInstance(), playJoin(mAdapter.getSelectedItems()),0);
                        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                    else {
                        mDataProvider.createNewPlaylist();
                    }
                }
            }
        });
        mSecFab1 = secFab1;
        mSecFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOperator != null){
                    mOperator.playAsNext(playJoin(mAdapter.getSelectedItems()));
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });
        mSecFab2 = secFab2;
        mSecFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOperator != null){
                    mOperator.append(playJoin(mAdapter.getSelectedItems()));
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });
    }

    @Override
    public void destroy(MusicService service) {

    }

    @Override
    public int getPlaylistId() {
        return Integer.MIN_VALUE;
    }

    private List<MuzixSong> playJoin(List<PlaylistData> lists) {
        List<MuzixSong> joinList = new ArrayList<>();
        for (PlaylistData data : lists) {
            joinList.addAll(data.getList());
        }
        return joinList;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean handleClick(int position, ActionModeHelper actionModeHelper) {
        //Action on elements are allowed if Mode is IDLE, otherwise selection has priority
        if (mAdapter.getMode() != SelectableAdapter.MODE_IDLE && actionModeHelper != null) {
            mAdapter.toggleSelectedElement(position);
            return actionModeHelper.onClick(position);
        } else {
            //Handle the item click listener
            if (mOperator != null && mOperator.boundToService()) {
                IFlexible iFlexible = mAdapter.getItem(position);
                if (iFlexible instanceof PlaylistData) {
                    PlaylistData playlistData = (PlaylistData) iFlexible;
                    Log.d("TGolomb","header btn will play list with id " + playlistData.getId());
                    mManager.switchMode(playlistData);
                }
            }
        }
        //We don't need to activate anything
        return false;
    }




    @Override
    public void enterActionMode() {
        mAdapter.exitActionMode();
        mAdapter.enterActionMode();
        if (plusToPlaylistAVD != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMainFab.setImageDrawable(plusToPlaylistAVD);
            plusToPlaylistAVD.start();
        }
        else{
            mMainFab.setImageResource(R.drawable.ic_playlist_play);
        }
        Utils.setVisibility(mSecFab1,View.VISIBLE);
        Utils.setVisibility(mSecFab2,View.VISIBLE);
    }

    @Override
    public void exitActionMode() {
        mAdapter.exitActionMode();
        if (playlistToPlusAVD != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMainFab.setImageDrawable(playlistToPlusAVD);
            playlistToPlusAVD.start();
        }
        else{
            mMainFab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        Utils.setVisibility(mSecFab1,View.GONE);
        Utils.setVisibility(mSecFab2,View.GONE);
    }

    @Override
    public void onScroll(boolean scrolling) {
        if (scrolling) {
            if(mAdapter.isInActionMode()){
                Utils.setVisibility(mSecFab2, View.GONE);
                Utils.setVisibility(mSecFab1, View.GONE);
            }
            Utils.setVisibility(mMainFab, View.GONE);
        } else {
            Utils.setVisibility(mMainFab, View.VISIBLE);
            if(mAdapter.isInActionMode()){
                Utils.setVisibility(mSecFab1, View.VISIBLE);
                Utils.setVisibility(mSecFab2, View.VISIBLE);
            }
        }
    }

    @Override
    public PlaylistAdapter<PlaylistData> getAdapter() {
        return mAdapter;
    }

    @Override
    public void toggleSelectedElement(int position) {
        mAdapter.toggleSelectedElement(position);
    }

    @Override
    public ModeManager.Mode getMode() {
        return ModeManager.Mode.LIBRARY;
    }
}
