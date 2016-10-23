package com.apps.golomb.muzix.interfaces;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import com.apps.golomb.muzix.MainActivity;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.data.Playlist;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.dataoperator.DataProvider;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerOperator;
import com.apps.golomb.muzix.mediaplayer.MusicService;
import com.apps.golomb.muzix.recyclerHelper.ListHeader;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;
import com.apps.golomb.muzix.utils.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import java.util.List;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.helpers.ActionModeHelper;
import eu.davidea.flexibleadapter.items.IFlexible;

import static android.view.View.GONE;

/**
 * Created by tomer on 19/10/2016.
 * Contains all the logic of all tracks mode.
 */

public class AllTrackResolver implements ModeResolver{

    private final SlidingUpPanelLayout mSlidingUpPanelLayout;
    private final MediaPlayerOperator mOperator;
    private final FloatingActionButton mMainFab;
    private final FloatingActionButton mSecFab1;
    private final FloatingActionButton mSecFab2;
    private PlaylistAdapter<MuzixSong> mAdapter;

    private static AnimatedVectorDrawable playToPlaylistAVD;
    private static AnimatedVectorDrawable playlistToPlayAVD;


    public AllTrackResolver(@NonNull MainActivity activity,
                            @NonNull PlaylistAdapter<MuzixSong> adapter,
                            @NonNull MusicService service,
                            @NonNull MediaPlayerOperator operator,
                            @NonNull final DataProvider dataProvider,
                            SlidingUpPanelLayout slidingUpPanelLayout,
                            FloatingActionButton mainFab,
                            FloatingActionButton secFab1,
                            FloatingActionButton secFab2){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(playToPlaylistAVD == null) {
                playToPlaylistAVD = (AnimatedVectorDrawable) activity.getDrawable(R.drawable.play_to_playlist);
            }
            if(playlistToPlayAVD == null) {
                playlistToPlayAVD = (AnimatedVectorDrawable) activity.getDrawable(R.drawable.playlist_to_play);
            }
        }

        mAdapter = adapter;
        mOperator = operator;
        mSlidingUpPanelLayout = slidingUpPanelLayout;

        mAdapter.setMediaOperator(operator);
        mAdapter.setDataOperator(dataProvider);
        service.addObserver(mAdapter);

        mainFab.setVisibility(View.VISIBLE);
        secFab1.setVisibility(GONE);
        secFab2.setVisibility(GONE);

        mMainFab = mainFab;
        mMainFab.setImageResource(R.drawable.ic_play_arrow_white);
        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // floating button was pressed.
                if(mAdapter.isInActionMode()) {
                    mOperator.play(PlaylistData.getTempInstance(), mAdapter.getSelectedItems(), 0);
                }
                else {
                    mOperator.play(PlaylistData.getAllTrackInstance(), mAdapter.getElementList(), 0);
                }
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
        mSecFab1 = secFab1;
        mSecFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOperator.playAsNext(mAdapter.getSelectedItems());
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
        mSecFab2 = secFab2;
        mSecFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOperator.append(mAdapter.getSelectedItems());
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
    }

    @Override
    public void destroy(MusicService service) {
        service.removeObserver(mAdapter);
    }

    @Override
    public int getPlaylistId() {
        return Playlist.ALL_TRACKS;
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
    @SuppressWarnings("ConstantConditions")
    public boolean handleClick(int position, ActionModeHelper actionModeHelper) {
        //Action on elements are allowed if Mode is IDLE, otherwise selection has priority
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
    public void enterActionMode() {
        mAdapter.enterActionMode();
        if (playToPlaylistAVD != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMainFab.setImageDrawable(playToPlaylistAVD);
            playToPlaylistAVD.start();
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
        if (playlistToPlayAVD != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMainFab.setImageDrawable(playlistToPlayAVD);
            playlistToPlayAVD.start();
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
    public ModeManager.Mode getMode() {
        return ModeManager.Mode.ALL_TRACKS;
    }

}
