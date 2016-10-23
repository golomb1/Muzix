package com.apps.golomb.muzix;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.FlexiblePair;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.AllTrackResolver;
import com.apps.golomb.muzix.interfaces.LibraryResolver;
import com.apps.golomb.muzix.interfaces.ModeManager;
import com.apps.golomb.muzix.interfaces.ModeResolver;
import com.apps.golomb.muzix.interfaces.PlaylistResolver;
import com.apps.golomb.muzix.interfaces.dataoperator.DataEvent;
import com.apps.golomb.muzix.interfaces.dataoperator.DataOperator;
import com.apps.golomb.muzix.interfaces.dataoperator.DataProvider;
import com.apps.golomb.muzix.interfaces.mediaoperator.MediaPlayerOperator;
import com.apps.golomb.muzix.mediaplayer.MusicService;
import com.apps.golomb.muzix.recyclerHelper.DragItemEvent;
import com.apps.golomb.muzix.recyclerHelper.NestedScrollableViewHelper;
import com.apps.golomb.muzix.recyclerHelper.PanelStateEvent;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;
import com.apps.golomb.muzix.utils.MediaContext;
import com.apps.golomb.muzix.utils.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.helpers.ActionModeHelper;
import ru.shmakinv.android.material.widget.GpCollapsingToolbar;


/***
 * Overview:
 * 1. This class represent the main screen, in order to decrease this class I split it's panel to a different class called: PlayerLayout.
 * The PlayerLayout will be responsible to change the panel layout when a song is being played, as well as response to the views event (e.g. click)
 * 2. The class MusicService responsible for the service that play the music, that way even exiting the app the music can be still played.
 * The is a need when starting this activity to check if the service is up and if it is, then to connect to it, otherwise start running it.
 * The service need to be able to be operated with out the activity.
 * 3. If the service is already running then there is a need to sync the service with the app.
 */

public class MainActivity extends AppCompatActivity
        implements ActionMode.Callback,
        FlexibleAdapter.OnItemClickListener,
        FlexibleAdapter.OnItemLongClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        SlidingUpPanelLayout.PanelSlideListener,
        FastScroller.OnScrollStateChangeListener,
        SearchView.OnQueryTextListener,
        ModeManager {


    private static final int READ_EXTERNAL_STORAGE_ALL_TRACKS = 0;

    //**********************************************************************************************
    //                                  Fields
    //**********************************************************************************************

    //**************************      Views

    private DrawerLayout mDrawer;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    // player control layout
    private PlayerLayout mPlayer;
    /**
     * main screen recycler view
     **/
    private RecyclerView mFirstRecyclerView;
    /**
     * the main screen fab button
     **/
    private FloatingActionButton mMainFab;
    private FloatingActionButton mFab1;
    private FloatingActionButton mFab2;

    private FastScroller mFastScroller;

    private View mainView;
    //**************************      other fields

    private MediaPlayerOperator mOperator = new MediaPlayerOperator();

    // handle the long click and the selection mode
    private ActionModeHelper mActionModeHelper;

    // the service that responsible to play music
    private MusicService musicSrv;

    private Intent playIntent;
    // flag to indicate that the activity is connected to the service
    private boolean musicBound = false;

    // adapter for the list view.
    private ModeResolver resolver;

    private boolean panelEventTriggered;
    private View mHeaderView;
    private Toolbar mToolbar;
    private FloatingActionButton mHeaderFab;
    private ActionBarDrawerToggle toggle;
    private ModeManager.Mode oldMode;
    private View.OnClickListener drawerClickListener;

    private DataProvider dataProvider;
    private SearchView mSearchView;

    //**********************************************************************************************
    //                                  Activity Operation
    //**********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataProvider = new DataOperator(this,getAllTracks());
        dataProvider.register(this);

        panelEventTriggered = false;
        mainView = findViewById(R.id.main_view);

        /* sliding panel */
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingUpPanelLayout.addPanelSlideListener(this);

        // Load the toolbar
        GpCollapsingToolbar gpCollapsingToolbar = (GpCollapsingToolbar) findViewById(R.id.gp_toolbar);
        gpCollapsingToolbar.setTitleEnabled(false);
        gpCollapsingToolbar.setTitle("Muzix");
        mHeaderView = findViewById(R.id.play_list_header);
        mHeaderView.setVisibility(View.GONE);

        mHeaderFab = (FloatingActionButton) findViewById(R.id.header_fab);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //

        // Floating button loading
        mMainFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab1 = (FloatingActionButton) findViewById(R.id.fab2);
        mFab2 = (FloatingActionButton) findViewById(R.id.fab3);

        //drawer layout set up
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        drawerClickListener = toggle.getToolbarNavigationClickListener();

        // The Content
        mFastScroller = (FastScroller) findViewById(R.id.fast_scroller);

        /* mFirstRecyclerView */
        mFirstRecyclerView = (RecyclerView) findViewById(R.id.playlist_recycler_view);

        setAllTracks();

        mPlayer = (PlayerLayout) findViewById(R.id.player);
        mPlayer.setController(this, null);
        mPlayer.setSlidingPanelLayout(mSlidingUpPanelLayout);
        mPlayer.setDataProvider(dataProvider);
        if (mOperator != null && mOperator.boundToService() && this.musicBound) {
            mPlayer.setMusicOperator(mOperator);
        }

        // the content view of the navigation drawer layout(the menu).
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            musicBound = true;
            musicSrv.createProgressObserver();
            mOperator.setService(musicSrv);
            mPlayer.setMusicOperator(mOperator);
            musicSrv.addObserver(mPlayer);
            if (Utils.DEBUG) {
                Log.d("TGolomb", "PlayLayout was register as observer in the service.");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicSrv.removeObserver(mPlayer);
            Log.d("TGolomb", "PlayLayout was unregister as observer in the service.");
            mPlayer.setMusicOperator(null);
            mOperator.setService(null);
            musicSrv.destroyProgressObserver();
            musicBound = false;
            musicSrv = null;
        }
    };

    /**
     * The view is already initialized, register everything to listen to events.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // register to general events : like drag operation
        EventBus.getDefault().register(this);
        EventBus.getDefault().register(mPlayer);
        // start/bind to the music service
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, 0);
        }
    }


    /**
     * No Destroy we need to do few things:
     * 1. maintaining the service to keep running
     * 2. free all resource
     * 3. keep all of the app variable that need to keep maintaining in the service.
     */
    @Override
    protected void onDestroy() {
        dataProvider.unregister(this);
        unbindService(musicConnection);
        // register to general events : like drag operation
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(mPlayer);
        setResolver(null);
        super.onDestroy();
    }

    // when pressing back, when the drawer is open we will want to close it.
    @Override
    public void onBackPressed() {
        mActionModeHelper.destroyActionModeIfCan();
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (oldMode != null) {
            restoreMode();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        initSearchView(menu);
        return true;
    }


    /**
     * TODO - handle presses on the menu
     * Called when the user clicked on one of the toolbar options
     *
     * @param item - the item that was pressed.
     * @return if was handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            if (oldMode != null) {
                restoreMode();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * TODO - handle all possible modes
     * Called when the user clicked on one of the drawer options
     *
     * @param item - the item that was pressed.
     * @return if was handled.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_track) {
            setAllTracks();
            // Handle the camera action
        } else if (id == R.id.nav_albums) {
            setAlbumsMode();
        } else if (id == R.id.nav_artist) {
            setArtistMode();
        } else if (id == R.id.nav_playlist) {
            setLibraryMode();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        // lets close the drawer when something was clicked.
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //**********************************************************************************************
    //                                  Listeners
    //**********************************************************************************************

    /***
     * How to react when the panel start sliding.
     * Right now nothing.
     *
     * @param panel       - view
     * @param slideOffset - offset
     */
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (slideOffset > 0.5f && mActionModeHelper != null) {
            mActionModeHelper.destroyActionModeIfCan();
        }
        if (!panelEventTriggered && slideOffset > 0.01f && slideOffset < 0.99f) {
            EventBus.getDefault().post(PanelStateEvent.getInstance(PanelStateEvent.Event.START));
            mainView.setEnabled(false);
            panelEventTriggered = true;
        } else if (panelEventTriggered && slideOffset < 0.01f && slideOffset > 0.99f) {
            EventBus.getDefault().post(PanelStateEvent.getInstance(PanelStateEvent.Event.END));
            mainView.setEnabled(true);
            panelEventTriggered = false;
        }
        if (slideOffset < 0.5f && mSlidingUpPanelLayout!=null) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (slideOffset > 0.5f && mSlidingUpPanelLayout!=null) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    /**
     * This methods is taking care on how to change the panel when it's changes its state.
     *
     * @param panel         - the panel view
     * @param previousState - the previous state of the panel
     * @param newState      - it's new state.
     */
    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                    SlidingUpPanelLayout.PanelState newState) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mPlayer.enterExpandedMode();
            if(mActionModeHelper != null) {
                mActionModeHelper.destroyActionModeIfCan();
            }
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            mPlayer.enterCollapsedMode();
        }
    }


    @Override
    public void onFastScrollerStateChange(boolean scrolling) {
        resolver.onScroll(scrolling);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        resolver.enterActionMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Utils.getColor(this, R.color.colorAccent, this.getTheme()));
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        resolver.exitActionMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Utils.getColor(this, R.color.colorPrimaryDark, this.getTheme()));
        }
    }


    //**********************************************************************************************
    //                                  Click Events
    //**********************************************************************************************


    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onItemClick(int position) {
        final int f = position;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resolver.handleClick(f, mActionModeHelper);
            }
        });
        return true;
    }

    @Override
    public void onItemLongClick(int position) {
        resolver.toggleSelectedElement(position);
        mActionModeHelper.onLongClick(this, position);
    }

    private ActionModeHelper initializeActionModeHelper(FlexibleAdapter adapter, int mode) {
        return new ActionModeHelper(adapter, R.menu.main, MainActivity.this) {
            @Override
            public void updateContextTitle(int count) {
                if (mActionMode != null) {//You can use the internal ActionMode instance
                    mActionMode.setTitle(count == 1 ?
                            getString(R.string.action_selected_one, Integer.toString(count)) :
                            getString(R.string.action_selected_many, Integer.toString(count)));
                            initSearchView(mActionMode.getMenu());
                }
            }
        }.withDefaultMode(mode);
    }

    //**********************************************************************************************
    //                                  Modes of operations
    //**********************************************************************************************

    /**
     * set playlist as all the music files in the device
     */
    public void setAllTracks() {
        if (musicSrv != null && musicBound) {
            List<MuzixSong> muzixSongList = Utils.copyToDemoList(getAllTracks());
            PlaylistAdapter<MuzixSong> adapter = Utils.configureAllTrackAdapter(muzixSongList, mFirstRecyclerView, this, this, mFastScroller, this, false);
            setResolver(new AllTrackResolver(this, adapter, musicSrv, mOperator,dataProvider, mSlidingUpPanelLayout, mMainFab, mFab1, mFab2));
            mActionModeHelper = initializeActionModeHelper(resolver.getAdapter(), SelectableAdapter.MODE_MULTI);
            mHeaderView.setVisibility(View.GONE);
            mToolbar.setBackgroundResource(R.color.colorPrimary);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                toggle.setDrawerIndicatorEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                mToolbar.setOnClickListener(drawerClickListener);
                toggle.syncState();
            }
        }
    }

    private void setAlbumsMode() {
        setLibraryMode(dataProvider.getAllAlbums());
    }

    private void setArtistMode() {
        setLibraryMode(dataProvider.getAllArtists());
    }


    private void setLibraryMode() {
        setLibraryMode(dataProvider.getAllPlaylists());
    }

    private void setLibraryMode(List<PlaylistData> playlistDataList) {
        if (musicSrv != null && musicBound) {
            PlaylistAdapter<PlaylistData> adapter = Utils.configureLibraryAdapter(playlistDataList, mFirstRecyclerView, this, this, mFastScroller, this, false);
            setResolver(new LibraryResolver(this, this, adapter, mOperator,dataProvider, mSlidingUpPanelLayout, mMainFab, mFab1, mFab2));
            mActionModeHelper = initializeActionModeHelper(resolver.getAdapter(), SelectableAdapter.MODE_MULTI);
            mHeaderView.setVisibility(View.GONE);
            mToolbar.setBackgroundResource(R.color.colorPrimary);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                toggle.setDrawerIndicatorEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                mToolbar.setOnClickListener(drawerClickListener);
                toggle.syncState();
            }
        }
    }


    private void selectedListMode(PlaylistData playlistData) {
        if (musicSrv != null && musicBound) {
            List<MuzixSong> muzixSongList = playlistData.getList();
            PlaylistAdapter<MuzixSong> adapter = Utils.configureListAdapter(muzixSongList, mFirstRecyclerView, this, this, mFastScroller, this, false);
            setResolver(new PlaylistResolver(this.dataProvider,adapter, mOperator, musicSrv, mHeaderFab, mMainFab, mFab1, mFab2, mSlidingUpPanelLayout, playlistData));
            mActionModeHelper = initializeActionModeHelper(resolver.getAdapter(), SelectableAdapter.MODE_MULTI);
            mHeaderView.setVisibility(View.VISIBLE);
            mToolbar.setBackgroundResource(R.color.transparent);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                toggle.setDrawerIndicatorEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (oldMode != null) {
                            restoreMode();
                        }
                    }
                });
            }
        }
    }

    public void setResolver(ModeResolver resolver) {
        if(this.resolver != null){
            this.resolver.destroy(musicSrv);
        }
        this.resolver = resolver;
    }

    @Override
    public void switchMode(PlaylistData data) {
        oldMode = resolver.getMode();
        selectedListMode(data);
    }

    @Override
    public void restoreMode() {
        if (oldMode.equals(Mode.ALBUMS)) {

        } else if (oldMode.equals(Mode.ARTIST)) {

        } else if (oldMode.equals(Mode.LIBRARY)) {
            setLibraryMode();
        }
        oldMode = null;
    }



    /***
     * handling the result of permission request.
     *
     * @param requestCode  - the code for the request,
     * @param permissions  - the permission being asked.
     * @param grantResults - the result of the request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_ALL_TRACKS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setAllTracks();
                }
                else{
                    // TODO handle request denial
                }
            }
        }
    }


    /**
     * Check if permission was given for a specific request, and ask if not.
     * @param permissionRequest - the requested permission.
     * @param requestCode       - the id of the permission being requested.
     * @return true if the permission was already given, false otherwise.
     * If permission wasn't given yet, you cannot use it and there is a need to wait for getting permission.
     */
    private boolean getPermission(String permissionRequest, int requestCode) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permissionRequest);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permissionRequest},
                        requestCode);
                return false;
            }
        }
        return true;
    }

    /***
     * This method returns all the music files in the device.
     * The user is being ask for permission to access his files.
     *
     * @return all the music files in the device.
     */
    public List<MuzixSong> getAllTracks() {
        // check for permission to read external storage, if not then ask for permission.
        if (!getPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_ALL_TRACKS)) {
            return null;
        }
        return MediaContext.getAllSongs(getContentResolver());
    }


    @Subscribe
    public void startDragItem(DragItemEvent event) {
        if (event.getEvent().equals(DragItemEvent.Event.START)) {
            mSlidingUpPanelLayout.setTouchEnabled(false);
        } else {
            mSlidingUpPanelLayout.setTouchEnabled(true);
        }
    }



    //**********************************************************************************************
    //                      data events
    //**********************************************************************************************

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemRemoved(final DataEvent.ListItemRemoved event) {
        int eventId = event.getPlaylistId();
        if(resolver.getMode().equals(Mode.LIST) && resolver.getPlaylistId() == eventId) {
            resolver.getAdapter().removeElementItem(event.getPosition());
        }
        if(mOperator != null && mOperator.getListId() == eventId){
            mOperator.removeFromList(event.getPosition());
        }
        Snackbar.make(this.mainView, R.string.notice_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataProvider != null) {
                            dataProvider.insert(event.getPlaylistId(),event.getPosition(), event.getItem());
                        }
                    }
                })
                .show();
    }

    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemInserted(final DataEvent.ListItemInserted event) {
        int eventId = event.getPlaylistId();
        if(resolver.getMode().equals(Mode.LIST) && resolver.getPlaylistId() == eventId) {
            resolver.getAdapter().addElementItem(event.getPosition(),event.getItem());
        }
        if(mOperator != null && mOperator.getListId() == eventId){
            mOperator.insert(event.getPosition(),event.getItem(),false);
        }
    }

    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemsInserted(final DataEvent.ListItemsInserted event) {
        int eventId = event.getPlaylistId();
        if(resolver.getMode().equals(Mode.LIST) && resolver.getPlaylistId() == eventId) {
            resolver.getAdapter().addElementItems(event.getPosition(),event.getItems());
        }
        if(mOperator != null && mOperator.getListId() == eventId){
            mOperator.insert(event.getPosition(),event.getItems(),false);
        }
    }

    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemMoved(final DataEvent.ListItemMoved event) {
        final int eventId = event.getListId();
        if(resolver.getMode().equals(Mode.LIST) && resolver.getPlaylistId() == eventId) {
            resolver.getAdapter().moveElementItem(event.getFrom(),event.getTo());
        }
        if(mOperator != null && mOperator.getListId() == eventId){
            mOperator.externalModeItem(event.getFrom(),event.getTo(),false);
        }
        if(!event.isUndo()) {
            Snackbar.make(this.mainView, R.string.notice_moved, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dataProvider != null) {
                                dataProvider.moveItem(eventId,event.getTo(), event.getFrom(),true);
                            }
                        }
                    })
                    .show();
        }
    }

    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaylistCreated(final DataEvent.PlaylistAdded event) {
        if(resolver.getMode().equals(Mode.LIBRARY)) {
            resolver.getAdapter().addElementItem(event.getPosition(),event.getData());
        }
    }

    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaylistCreated(final DataEvent.PlaylistRemoved event) {
        if(resolver.getMode().equals(Mode.LIBRARY)) {
            resolver.getAdapter().removeElementItem(event.getPosition());
        }
        Snackbar.make(this.mainView, R.string.notice_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataProvider != null) {
                            dataProvider.addPlaylist(event.getPosition(), event.getData(), event.getList());
                        }
                    }
                })
                .show();
    }

    //**********************************************************************************************
    //                      delegate methods
    //**********************************************************************************************

    public void addToPlaylist(FlexiblePair iFlexible, List<MuzixSong> payload) {
        this.dataProvider.addToPlaylist(iFlexible,payload);
    }

    public void createNewPlaylist(String name, String details, List<MuzixSong> payload) {
        this.dataProvider.createNewPlaylist(name,details,payload);
    }

    //**********************************************************************************************
    //                      search logic
    //**********************************************************************************************


    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("TGolomb","onQueryTextChange "+ newText);
        resolver.getAdapter().setSearchText(newText);
        resolver.getAdapter().filterItems(resolver.getAdapter().getElementList(),300);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.v("TGolomb", "onQueryTextSubmit called!");
        return onQueryTextChange(query);
    }

    private void initSearchView(final Menu menu) {
        //Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            MenuItemCompat.setOnActionExpandListener(
                    searchItem, new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            // TODO hide views
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            // TODO show views
                            return true;
                        }
                    });
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            mSearchView.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
            mSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);
            mSearchView.setQueryHint(getString(R.string.search));
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(this);
        }
    }
}
