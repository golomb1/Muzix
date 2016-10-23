package com.apps.golomb.muzix;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.dataoperator.DataProvider;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerEvent;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerOperator;
import com.apps.golomb.muzix.mediaplayer.MediaPlayerProgressEvent;
import com.apps.golomb.muzix.recyclerHelper.DragItemEvent;
import com.apps.golomb.muzix.recyclerHelper.PanelStateEvent;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;
import com.apps.golomb.muzix.utils.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;


public class PlayerLayout extends RelativeLayout implements
        MediaController.MediaPlayerControl,
        View.OnClickListener,
        FlexibleAdapter.OnItemClickListener,
        SeekBar.OnSeekBarChangeListener,
        FlexibleAdapter.OnItemMoveListener,
        FlexibleAdapter.OnItemSwipeListener, FastScroller.OnScrollStateChangeListener {

    private MediaPlayerOperator operator;
    private DataProvider mDataProvider;

    private PlaylistAdapter<MuzixSong> mAdapter;
    private PopupMenu popupMenu;

    private int dragEventFromPosition;
    private int dragEventToPosition;


    /**********************************************************************************************/
    //                                      Control views
    /**********************************************************************************************/

    // This button is the top button, when collapsed is should be play/pause
    // and when expand it's need to be save button.
    private ImageButton headerBtn;

    // the current song title
    private TextView itemTitle;
    // the current song details
    private TextView itemDetails;

    // layout for animation to switch between itemImage an currentList.
    private RelativeLayout mListView;
    private ImageView itemImage;

    private SeekBar progressBar;
    private TextView timePassed;
    private TextView totalTime;

    private ImageButton playButton;

    private TextView playlistName;
    private TextView playlistDetails;
    private ImageView playlistBanner;
    private static AnimatedVectorDrawable playToPause;
    private static AnimatedVectorDrawable pauseToPlay;
    private static AnimatedVectorDrawable playToPause2;
    private static AnimatedVectorDrawable pauseToPlay2;

    /**********************************************************************************************/
    //                                      Constructors
    /**********************************************************************************************/

    public PlayerLayout(Context context) {
        super(context);
        init(context);
    }

    public PlayerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**********************************************************************************************/
    //                                          init
    /**********************************************************************************************/

    private void init(Context context) {

        // Initialize controls.
        LayoutInflater.from(context).inflate(R.layout.player, this, true);
        // disable a click collapse
        findViewById(R.id.player_control_layout).setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(playToPause == null){
                playToPause = (AnimatedVectorDrawable) context.getDrawable(R.drawable.play_to_pause);
            }
            if(pauseToPlay == null){
                pauseToPlay = (AnimatedVectorDrawable) context.getDrawable(R.drawable.pause_to_play);
            }
            if(playToPause2 == null){
                playToPause2 = (AnimatedVectorDrawable) context.getDrawable(R.drawable.play_to_pause);
            }
            if(pauseToPlay2 == null){
                pauseToPlay2 = (AnimatedVectorDrawable) context.getDrawable(R.drawable.pause_to_play);
            }
        }

        /*****************************/
        //      Initializes views
        /*****************************/

        // Top bar
        headerBtn = (ImageButton) findViewById(R.id.header_button);
        headerBtn.setOnClickListener(this);
        itemTitle = (TextView) findViewById(R.id.item_title);
        itemTitle.setSelected(true);
        itemDetails = (TextView) findViewById(R.id.item_artist);
        itemDetails.setSelected(true);

        ImageButton itemList = (ImageButton) findViewById(R.id.item_list);
        itemList.setOnClickListener(this);

        ImageButton moreBtn = (ImageButton) findViewById(R.id.item_more);
        initializePopupMenu(context, moreBtn);

        // Middle screen
        itemImage = (ImageView) findViewById(R.id.item_album);
        mListView = (RelativeLayout) findViewById(R.id.list_view);
        FastScroller fastScroller = (FastScroller) findViewById(R.id.fast_scroller);
        RecyclerView currentListView = (RecyclerView) findViewById(R.id.current_list);
        mAdapter = Utils.configureCurrentListAdapter(currentListView, getContext(), this,fastScroller,this);
        mAdapter.getItemTouchHelperCallback().setSwipeThreshold(1.0f);
        currentListView.setAdapter(mAdapter);

        playlistBanner = (ImageView) findViewById(R.id.banner);
        playlistName = (TextView) findViewById(R.id.playlist_name);
        playlistDetails = (TextView) findViewById(R.id.playlist_details);
        ImageButton saveBtn = (ImageButton) findViewById(R.id.save_list_btn);
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataProvider != null){
                    mDataProvider.createNewPlaylist(mAdapter.getElementList());
                }
            }
        });


        // bottom screen
        progressBar = (SeekBar) findViewById(R.id.progressBar);
        progressBar.setOnSeekBarChangeListener(this);
        timePassed = (TextView) findViewById(R.id.start_time);
        totalTime = (TextView) findViewById(R.id.end_time);

        // bottom screen controls
        playButton = (ImageButton) findViewById(R.id.play_btm);
        playButton.setOnClickListener(this);
        ImageButton prevButton = (ImageButton) findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(this);
        ImageButton nextButton = (ImageButton) findViewById(R.id.next_btn);
        nextButton.setOnClickListener(this);
        ImageButton likeButton = (ImageButton) findViewById(R.id.like_btn);
        likeButton.setOnClickListener(this);
        ImageButton dislikeButton = (ImageButton) findViewById(R.id.dislike_btn);
        dislikeButton.setOnClickListener(this);
    }


    private void initializePopupMenu(Context context, View moreBtn) {
        popupMenu = new PopupMenu(context, moreBtn);
        popupMenu.getMenuInflater().inflate(R.menu.player_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                // TODO
                return true;
            }
        });
        moreBtn.setOnClickListener(this);
    }

    public void updateView(MuzixSong song) {
        itemTitle.setText(song.getTitle());
        itemDetails.setText(song.getDetails());
        totalTime.setText(song.getDurationString());
        progressBar.setMax(song.getDuration());
        //TODO mItemImage.setDrawable
    }

    public void emptyView() {
        Log.d("TGolomb", "EmptyView");
        itemTitle.setText("");
        itemDetails.setText("");
        totalTime.setText("");
        progressBar.setMax(0);
    }

    private void updateView(PlaylistData playlistData) {
        playlistName.setText(playlistData.getName());
        playlistDetails.setText(playlistData.getDetails());
        if (playlistData.hasBanner()) {
            playlistBanner.setImageResource(playlistData.getBannerId());
        }
    }

    public void setMusicOperator(MediaPlayerOperator operator) {
        this.operator = operator;
        if (mAdapter != null) {
            mAdapter.setMediaOperator(operator);
        }
        this.onSongChanged(null);
    }


    public void setDataProvider(DataProvider mDataProvider) {
        this.mDataProvider = mDataProvider;
        if (mAdapter != null) {
            mAdapter.setDataOperator(mDataProvider);
        }
    }

    //**********************************************************************************************
    //                                     Media player Controller
    //**********************************************************************************************

    @SuppressWarnings({"WeakerAccess", "UnusedParameters"})
    public void setController(Activity activity, View songListView) {
        //set the controller up
        MusicController controller = new MusicController(activity);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(this);
        //controller.setAnchorView(songListView);
        controller.setEnabled(true);
    }


    //play next
    private void playNext() {
        if (operator != null) {
            operator.playNextSong();
        }
    }

    //play previous
    private void playPrev() {
        if (operator != null) {
            operator.playPrevSong();
        }
    }

    @Override
    public void start() {
        if (operator != null) {
            operator.startPlayer();
        }
    }

    @Override
    public void pause() {
        if (operator != null) {
            operator.pause();
        }
    }

    @Override
    public int getDuration() {
        if (operator != null) {
            return operator.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (operator != null) {
            return operator.getPosition();
        } else {
            return 0;
        }
    }

    @Override
    public void seekTo(int position) {
        if (operator != null) {
            operator.seekTo(position);
        }
    }

    @Override
    public boolean isPlaying() {
        return operator != null && operator.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        // we can leave this alone
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        // we can leave this alone
        return 0;
    }

    //**********************************************************************************************
    //                                     View events handling
    //**********************************************************************************************

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_button:
            case R.id.play_btm: {
                if (operator != null) {
                    operator.playPause();
                }
                break;
            }
            case R.id.next_btn: {
                if (operator != null) {
                    operator.playNextSong();
                }
                break;
            }
            case R.id.prev_btn: {
                if (operator != null) {
                    operator.playPrevSong();
                }
                break;
            }
            case R.id.like_btn: {
                // TODO like song
                break;
            }
            case R.id.dislike_btn: {
                // TODO dislike song
                break;
            }
            case R.id.item_list: {
                showCurrentList();
                break;
            }
            case R.id.more:
                popupMenu.show();
                break;
        }
    }


    private void showCurrentList() {
        if (operator != null) {
            if (mListView.getVisibility() != VISIBLE) {
                //Utils.setVisibility(mListView,VISIBLE);
                mListView.setVisibility(VISIBLE);
                TranslateAnimation animation = new TranslateAnimation(
                        Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE, 0,
                        Animation.RELATIVE_TO_SELF, 2,
                        Animation.RELATIVE_TO_SELF, 0);
                animation.setDuration(500);
                mListView.setAnimation(animation);
                mAdapter.setPlaylistData(operator.getPlaylistData());
                mAdapter.updateDataSet(operator.getPlaylist(), false);
            } else {
                TranslateAnimation animation = new TranslateAnimation(
                        Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 2);
                animation.setDuration(500);
                mListView.setAnimation(animation);
                mListView.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (Utils.DEBUG) {
                Utils.Log("onProgressChanged, seek to", progress);
            }
            if (operator != null) {
                if (operator.seekTo(progress)) {
                    timePassed.setText(Utils.formatMillis(progress));
                }
            }
        }
    }

    @Override
    public boolean shouldMoveItem(int fromPosition, int toPosition) {
        Log.d("TGolomb", "should move " + fromPosition + " -> " + toPosition);
        return toPosition >= 1 && toPosition < mAdapter.getItemCount();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // This method is being called on every move of items, should wait to finish of the operation, while saving the positions.
        if (dragEventFromPosition == -1) {
            dragEventFromPosition = fromPosition;
        }
        dragEventToPosition = toPosition;
        Log.d("TGolomb", "onItemMove( " + fromPosition + " , " + toPosition + " )");
    }

    //**********************************************************************************************
    //                                     Event Listener
    //**********************************************************************************************

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerStateChange(MediaPlayerEvent.PlayerStateEvent event) {
        if (operator != null) {
            mAdapter.onPlayerStateChanged(event);
            if (operator.isPlaying()) {
                setImage(playToPause, playButton, R.drawable.ic_pause);
                setImage(playToPause2, headerBtn, R.drawable.ic_pause);
            } else if (operator.isPaused()) {
                setImage(pauseToPlay, playButton, R.drawable.ic_play);
                setImage(pauseToPlay2, headerBtn, R.drawable.ic_play);
            } else if (operator.isStopped()) {
                playButton.setImageResource(R.drawable.ic_stop);
                headerBtn.setImageResource(R.drawable.ic_stop);
            }
        }

    }

    private void setImage(AnimatedVectorDrawable animator, ImageView imageView, int resourceId) {
        if(animator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(animator);
            animator.start();
        }
        else {
            imageView.setImageResource(resourceId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSongChanged(MediaPlayerEvent.ListPositionEvent event) {
        if (operator != null) {
            mAdapter.onSongChanged(event);
            MuzixSong song = operator.getCurrentSong();
            if (song != null) {
                updateView(song);
            }
            /*
            if (operator.isPlaying() && mSlidingPanelLayout != null) {
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }*/
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangingList(MediaPlayerEvent.ListChangeEvent event) {
        if (operator != null) {
            mAdapter.onListChanged(event);
            List<MuzixSong> songs = operator.getPlaylist();
            if (songs != null) {
                updateView(operator.getPlaylistData());
                mAdapter.setPlaylistData(operator.getPlaylistData());
                mAdapter.updateDataSet(songs, false);
                if (songs.size() == 0) {
                    emptyView();
                }
                Log.d("TGolomb","Scrolling");
                mAdapter.getRecyclerView().scrollToPosition(0);
            }
        }
    }

    @Subscribe()
    public void onListItemRemoved(final MediaPlayerEvent.ListItemRemoved event) {
        if (operator != null) {
            // the list is temp now
            updateView(operator.getPlaylistData());
            mAdapter.setPlaylistData(operator.getPlaylistData());
            mAdapter.removeItem(event.getPosition());
            mAdapter.onListItemRemoved(event);
            if(event.isLocal()) {
                Snackbar.make(this, R.string.notice_removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.action_undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // do nothing
                                if (operator != null) {
                                    operator.insert(event.getPosition(), event.getItem());
                                }
                            }
                        })
                        .show();
            }
        }
    }

    @Subscribe()
    public void onListItemInserted(final MediaPlayerEvent.ListItemInserted event) {
        if (operator != null) {
            // the list is temp now
            updateView(operator.getPlaylistData());
            mAdapter.setPlaylistData(operator.getPlaylistData());
            mAdapter.addItem(event.getPosition(),event.getItem());
            mAdapter.onListItemInserted(event);
        }
    }

    @Subscribe()
    public void onListItemsInserted(final MediaPlayerEvent.ListItemsInserted event) {
        if (operator != null) {
            // the list is temp now
            updateView(operator.getPlaylistData());
            mAdapter.setPlaylistData(operator.getPlaylistData());
            mAdapter.addItems(event.getPosition(),event.getItems());
            mAdapter.onListItemsInserted(event);
        }
    }

    @Subscribe()
    public void onListItemMoved(final MediaPlayerEvent.ListItemMoved event) {
        if (operator != null) {
            // the list is temp now
            updateView(operator.getPlaylistData());
            mAdapter.setPlaylistData(operator.getPlaylistData());
            mAdapter.updateDataSet(operator.getPlaylist());
            if(!event.isExternal()){
                Snackbar.make(this, R.string.notice_moved, Snackbar.LENGTH_LONG)
                        .setAction(R.string.action_undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (operator != null) {
                                    operator.externalModeItem(event.getTo(), event.getFrom());
                                }
                            }
                        })
                        .show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProgressUpdate(MediaPlayerProgressEvent event) {
        timePassed.setText(Utils.formatMillis(event.getPosition()));
        progressBar.setProgress(event.getPosition());
    }

    //**********************************************************************************************
    //                                     Empty methods
    //**********************************************************************************************

    public PlaylistAdapter<MuzixSong> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onActionStateChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.d("TGolomb", "onActionStateChanged( " + actionState + ")");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void enterExpandedMode() {
        // TODO - update the top controls
    }

    public void enterCollapsedMode() {
        // TODO - update the top controls
    }

    //**********************************************************************************************
    //                                  Swipe and move
    //**********************************************************************************************

    @Override
    public boolean onItemClick(int position) {
        if (operator != null) {
            operator.play(position);
            return true;
        }
        return false;
    }

    @Subscribe
    public void onDragEnd(DragItemEvent event) {
        if (event.getEvent().equals(DragItemEvent.Event.END)) {
            if (operator != null) {
                if (dragEventFromPosition != dragEventToPosition) {
                    operator.moveItem(dragEventFromPosition, dragEventToPosition);
                }
            }
        } else {
            dragEventFromPosition = -1;
            dragEventToPosition = -1;
        }
    }

    @Override
    public void onItemSwipe(int position, int direction) {
        if (operator != null) {
            operator.remove(mAdapter.getItemPosition(position));
        }
    }

    @Subscribe
    public void startDragItem(PanelStateEvent event) {
        if (event.getEvent().equals(PanelStateEvent.Event.START)) {
            mListView.setEnabled(true);
        } else {
            mListView.setEnabled(false);
        }
    }

    public void setSlidingPanelLayout(SlidingUpPanelLayout mSlidingPanelLayout) {
    }

    @Override
    public void onFastScrollerStateChange(boolean scrolling) {
        // nothing for now
    }
}

