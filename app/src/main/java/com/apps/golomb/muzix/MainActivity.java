package com.apps.golomb.muzix;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.data.Playlist;
import com.apps.golomb.muzix.mediaplayer.MusicService;
import com.apps.golomb.muzix.wigets.MuzixListDataExtractor;
import com.apps.golomb.muzix.wigets.MuzixListViewHolderGenerator;
import com.apps.golomb.muzix.wigets.MuzixViewHolderGenerator;
import com.libs.golomb.extendedrecyclerview.DataExtractor.SectionListDataExtractor;
import com.libs.golomb.extendedrecyclerview.DataExtractor.SimpleListDataExtractor;
import com.libs.golomb.extendedrecyclerview.ExtendedRecycleAdapter;
import com.libs.golomb.extendedrecyclerview.ExtendedRecycleView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.apps.golomb.muzix.data.MuzixEntity.PLAYLIST;


/***
 * Overview:
 *      1. This class represent the main screen, in order to decrease this class I split it's panel to a different class called: PlayerLayout.
 *         The PlayerLayout will be responsible to change the panel layout when a song is being played, as well as response to the views event (e.g. click)
 *      2. The class MusicService responsible for the service that play the music, that way even exiting the app the music can be still played.
 *          The is a need when starting this activity to check if the service is up and if it is, then to connect to it, otherwise start running it.
 *          The service need to be able to be operated with out the activity.
 *      3. If the service is already running then there is a need to sync the service with the app.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SlidingUpPanelLayout.PanelSlideListener,
        ExtendedRecycleAdapter.OnClickListener<MuzixSong>{


    private static final int READ_EXTERNAL_STORAGE_ALL_TRACKS = 0;
    private DrawerLayout mDrawer;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private ExtendedRecycleAdapter<MuzixSong> mSongsAdapter;
    private ExtendedRecycleAdapter<Playlist> mPlaylistAdapter;

    private ImageButton mPlaySlide;
    private PlayerLayout mPlayer;

    // the service
    private MusicService musicSrv;

    private Intent playIntent;

    // flag to indicate that the activity is connected to the service
    private boolean musicBound=false;
    private ExtendedRecycleView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* sliding panel */
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingUpPanelLayout.addPanelSlideListener(this);

        // Load the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating button loading
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // floating button was pressed.
                // TODO
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //drawer layout set up
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // The Content

        mRecyclerView = (ExtendedRecycleView) findViewById(R.id.recycler_view);
        mSongsAdapter = new ExtendedRecycleAdapter<>(new SimpleListDataExtractor<>(new ArrayList<MuzixSong>(), false, true), new MuzixViewHolderGenerator(this), this);
        mPlaylistAdapter = new ExtendedRecycleAdapter<>(new SimpleListDataExtractor<>(new ArrayList<Playlist>(), false, true),
                new MuzixListViewHolderGenerator(this),
                new ExtendedRecycleAdapter.OnClickListener<Playlist>() {
                    @Override
                    public void onClick(View view, Playlist item) {
                        // TODO - switch to list view
                    }
                });
        final GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if ( (mPlaylistAdapter.hasHeader() && mPlaylistAdapter.isHeader(position)) ||
                        mPlaylistAdapter.hasFooter() && mPlaylistAdapter.isFooter(position) )
                    return manager.getSpanCount();
                return 1;
            }
        });
        mRecyclerView.initializeDefault(this, LinearLayoutManager.VERTICAL, mSongsAdapter);

        mPlayer = new PlayerLayout(this);
        mPlayer.setController(this,mRecyclerView);

        mPlaySlide = (ImageButton) findViewById(R.id.sliding_top_control);

        // the content view of the navigation drawer layout(the menu).
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            musicBound = true;
            mPlayer.setService(musicSrv);
            mPlayer.setMusicBound(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
            mPlayer.setMusicBound(false);
        }
    };


    @Override
    protected void onStart() {
        // TODO
        super.onStart();
        // start the service
        if(playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent,musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    // when pressing back, when the drawer is open we will want to close it.
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        else if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else{
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * TODO
     * Called when the user clicked on one of the toolbar options
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

        return super.onOptionsItemSelected(item);
    }

    /**
     * TODO
     * Called when the user clicked on one of the drawer options
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

        } else if (id == R.id.nav_artist) {

        } else if (id == R.id.nav_playlist) {
            libraryMode();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        // lets close the drawer when something was clicked.
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * TODO -- what to do in case of null
     * TODO    The way that the list is being sort.
     * when a playlist is selected, prepare akk the relevant classes on the current list.
     * @param muzixSongList - the list that was selected.
     */
    public void playlistSelected(List<MuzixSong> muzixSongList){
        if (muzixSongList != null) {
            Collections.sort(muzixSongList);
            mRecyclerView.setDefaultLayoutManager();
            mSongsAdapter.update(new SectionListDataExtractor<>(muzixSongList, false, true));
            mRecyclerView.getAdapter().notifyItemRangeRemoved(0,mRecyclerView.getAdapter().getItemCount());
            mRecyclerView.swapAdapter(mSongsAdapter,true);
            mSongsAdapter.notifyDataSetChanged();
            if(musicSrv != null && musicBound) {
                musicSrv.setList(muzixSongList);
            }
        }
    }


    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlist = new ArrayList<>();
        for(int i = 0; i < 100; i ++){
            playlist.add(new Playlist(PLAYLIST,"Playlist "+ i, 0, " Me"));
        }
        return playlist;
    }

    public void libraryMode(){
        // TODO
        List<Playlist> list = getAllPlaylists();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mPlaylistAdapter.update(new SimpleListDataExtractor<>(list,false,true));
        mRecyclerView.getAdapter().notifyItemRangeRemoved(0,mRecyclerView.getAdapter().getItemCount());
        mRecyclerView.swapAdapter(mPlaylistAdapter,true);
        mPlaylistAdapter.notifyDataSetChanged();
    }

    /**
     * TODO - play music file, the music file can be either in the current list being viewed, or the list being heard.
     * @param item - the music file
     */
    private void playMusic(MuzixSong item) {
        //TODO
        mPlayer.setUp(item.getFirst());
        musicSrv.setSong(musicSrv.getList().indexOf(item));
        musicSrv.playSong();
    }


    /**
     * set playlist as all the music files in the device
     */
    public void setAllTracks() {
        playlistSelected(getAllTracks());
    }

    /***
     * handling the result of permission request.
     * @param requestCode - the code for the request,
     * @param permissions - the permission being asked.
     * @param grantResults - the result of the request.
     * TODO handle request denial.
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
            }
        }
    }


    /**
     * Check if permission was given for a specific request, and ask if not.
     * @param permissionRequest - the requested permission.
     * @param requestCode - the id of the permission being requested.
     * @return true if the permission was already given, false otherwise.
     * If permission wasn't given yet, you cannot use it and there is a need to wait for getting permission.
     */
    private boolean getPermission(String permissionRequest, int requestCode){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permissionRequest);
            if(permissionCheck != PackageManager.PERMISSION_GRANTED){
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
     * @return all the music files in the device.
     */
    public List<MuzixSong> getAllTracks() {
        List<MuzixSong> muzixSongList = new ArrayList<>();
        // check for permission to read external storage, if not then ask for permission.
        if(!getPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_ALL_TRACKS)){
            return null;
        }
        //retrieve song info
        Uri allSongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        // the data to retrieve from the music files.
        String[] dataType = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION};
        Cursor musicCursor = getContentResolver().query(allSongUri, dataType, selection, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                int thisDuration = musicCursor.getInt(durationColumn);
                muzixSongList.add(new MuzixSong(thisId, thisTitle, thisArtist, thisDuration));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return muzixSongList;
    }

    /***
     * How to react when the panel start sliding.
     * Right now nothing.
     * @param panel - view
     * @param slideOffset - offset
     */
    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    /***
     * Click was made on a list item, therefor we need to expand the panel and start playing the new song.
     * @param view - that was clicked
     * @param item - the item associated with this view in the list.
     */
    @Override
    public void onClick(View view,MuzixSong item) {
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        playMusic(item);
    }


    /**
     * No Destroy we need to do few things:
     *  1. maintaining the service to keep running
     *  2. free all resource
     *  3. keep all of the app variable that need to keep maintaining in the service.
     */
    @Override
    protected void onDestroy() {
        // TODO
        mPlayer.destroy();
        unbindService(musicConnection);
        stopService(playIntent);
        musicSrv=null;
        mPlayer.setService(null);
        super.onDestroy();
    }


    /**
     * This methods is taking care on how to change the panel when it's changes its state.
     * @param panel - the panel view
     * @param previousState - the previous state of the panel
     * @param newState - it's new state.
     */
    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
            mPlaySlide.setVisibility(View.INVISIBLE);
        }
        else if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
            mPlaySlide.setVisibility(View.VISIBLE);
        }
    }


}
