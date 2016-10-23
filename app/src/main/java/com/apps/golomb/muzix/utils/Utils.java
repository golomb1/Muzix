package com.apps.golomb.muzix.utils;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import com.apps.golomb.muzix.MainActivity;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.data.Playlist;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.mediaoperator.RepeatMode;
import com.apps.golomb.muzix.recyclerHelper.ListHeader;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.SelectableAdapter;

/**
 * Created by tomer on 06/09/2016.
 * Util class for static functions
 */
public class Utils {
    public static boolean DEBUG = true;

    public static String formatMillis(int millis) {
        return String.format(Locale.getDefault(), "%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public static PlaylistAdapter<MuzixSong> configureCurrentListAdapter
            (RecyclerView recyclerView, Context context, Object listeners, FastScroller scroller,
             FastScroller.OnScrollStateChangeListener onScrollChangeListener)
    {
        PlaylistAdapter<MuzixSong> adapter = new PlaylistAdapter<>(R.layout.list_item, listeners, false, null);

        // Headers
        adapter.setDisplayHeadersAtStartUp(false);
        adapter.enableStickyHeaders();

        // recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        // selection mode
        adapter.setMode(SelectableAdapter.MODE_IDLE);
        // drag
        adapter.setHandleDragEnabled(true);
        adapter.setLongPressDragEnabled(true);
        //Enable Swipe-To-Dismiss
        adapter.setSwipeEnabled(true);
        // scroll
        recyclerView.setVerticalScrollBarEnabled(true);

        int color = Utils.getScrollColor(context);
        adapter.setFastScroller(scroller, color, onScrollChangeListener);
        adapter.setAnimationOnScrolling(true);
        scroller.setVisibility(View.VISIBLE);
        adapter.setShowBubble(false);
        return adapter;
    }


    public static PlaylistAdapter<MuzixSong> configureListAdapter(List<MuzixSong> songs,
                                                                     RecyclerView recyclerView,
                                                                     Context context,
                                                                     Object listeners,
                                                                     FastScroller scroller,
                                                                     FastScroller.OnScrollStateChangeListener onScrollChangeListener,
                                                                     boolean swapAdapter) {
        PlaylistAdapter<MuzixSong> adapter =
                new PlaylistAdapter<>(new ArrayList<>(songs),R.layout.list_item,PlaylistData.getTempInstance(), listeners, false);

        // Headers
        adapter.setTopHeader(new ListHeader());
        adapter.setDisplayHeadersAtStartUp(false);
        //adapter.enableStickyHeaders();
        adapter.setRemoveOrphanHeaders(true);


        // recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (swapAdapter) {
            recyclerView.swapAdapter(adapter, false);
        } else {
            recyclerView.setAdapter(adapter);
        }
        // selection mode
        adapter.setMode(SelectableAdapter.MODE_IDLE);
        // drag
        adapter.setHandleDragEnabled(true);
        //Enable Swipe-To-Dismiss
        adapter.setSwipeEnabled(true);
        adapter.setLongPressDragEnabled(true);


        // fast scroll
        int color = Utils.getScrollColor(context);
        adapter.setFastScroller(scroller, color, onScrollChangeListener);
        adapter.setAnimationOnScrolling(true);
        scroller.setVisibility(View.VISIBLE);
        adapter.setShowBubble(false);
        return adapter;
    }


    public static PlaylistAdapter<MuzixSong> configureAllTrackAdapter(List<MuzixSong> songs,
                                                                      RecyclerView recyclerView,
                                                                      Context context,
                                                                      Object listeners,
                                                                      FastScroller scroller,
                                                                      FastScroller.OnScrollStateChangeListener onScrollChangeListener,
                                                                      boolean swapAdapter) {
        PlaylistAdapter<MuzixSong> adapter =
                new PlaylistAdapter<>(songs,R.layout.list_item,PlaylistData.getAllTrackInstance(), listeners, false);

        // Headers
        adapter.setTopHeader(new ListHeader());
        adapter.setDisplayHeadersAtStartUp(true);
        adapter.enableStickyHeaders();
        adapter.setRemoveOrphanHeaders(true);


        // recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (swapAdapter) {
            recyclerView.swapAdapter(adapter, false);
        } else {
            recyclerView.setAdapter(adapter);
        }
        // selection mode
        adapter.setMode(SelectableAdapter.MODE_IDLE);
        // drag
        adapter.setHandleDragEnabled(false);
        //Enable Swipe-To-Dismiss
        adapter.setSwipeEnabled(false);
        adapter.setLongPressDragEnabled(false);


        // fast scroll
        int color = Utils.getScrollColor(context);
        adapter.setFastScroller(scroller, color, onScrollChangeListener);
        adapter.setAnimationOnScrolling(true);
        scroller.setVisibility(View.VISIBLE);
        adapter.setShowBubble(true);
        return adapter;
    }

    public static PlaylistAdapter<PlaylistData> configureLibraryAdapter(List<PlaylistData> playlistDatas,
                                                                      RecyclerView recyclerView,
                                                                      Context context,
                                                                      Object listeners,
                                                                      FastScroller scroller,
                                                                      FastScroller.OnScrollStateChangeListener onScrollChangeListener,
                                                                      boolean swapAdapter) {
        PlaylistAdapter<PlaylistData> adapter =
                new PlaylistAdapter<>(playlistDatas,R.layout.playlist_card,null, listeners, false);

        // Headers
        adapter.setTopHeader(null);
        adapter.setDisplayHeadersAtStartUp(false);
        adapter.enableStickyHeaders();
        adapter.setRemoveOrphanHeaders(true);


        // recycler view
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
        if (swapAdapter) {
            recyclerView.swapAdapter(adapter, false);
        } else {
            recyclerView.setAdapter(adapter);
        }
        // selection mode
        adapter.setMode(SelectableAdapter.MODE_IDLE);
        // drag
        adapter.setHandleDragEnabled(false);
        //Enable Swipe-To-Dismiss
        adapter.setSwipeEnabled(false);
        adapter.setLongPressDragEnabled(false);


        // fast scroll
        int color = Utils.getScrollColor(context);
        adapter.setFastScroller(scroller, color, onScrollChangeListener);
        adapter.setAnimationOnScrolling(true);
        scroller.setVisibility(View.VISIBLE);
        adapter.setShowBubble(true);
        return adapter;
    }

    public static int getScrollColor(Context context) {
        // TODO - cache this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(R.color.colorAccent, null);
        } else {
            return context.getResources().getColor(R.color.colorAccent);
        }
    }

    public static List<MuzixSong> copyToDemoList(List<MuzixSong> muzixSongList) {
        List<MuzixSong> list = new ArrayList<>();
        for (char c = 'a'; c < 'z'; c++) {
            for (int i = 0; i < muzixSongList.size(); i++) {
                list.add(new MuzixSong(muzixSongList.get(i).getId(), c + " Song", "Artist " + 3 * i + 0, muzixSongList.get(i).getDuration(),muzixSongList.get(i).getAlbumId()));
                list.add(new MuzixSong(muzixSongList.get(i).getId(), c + " Song", "Artist " + 3 * i + 1, muzixSongList.get(i).getDuration(),muzixSongList.get(i).getAlbumId()));
                list.add(new MuzixSong(muzixSongList.get(i).getId(), c + " Song", "Artist " + 3 * i + 2, muzixSongList.get(i).getDuration(),muzixSongList.get(i).getAlbumId()));
            }
        }
        return list;
    }

    public static int getColor(Context context, int colorId, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(colorId, theme);
        }   //noinspection deprecation
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getColor(colorId);
        }
        return -1;
    }

    private static int getRandomBit() {
        return Math.random() > 0.5 ? 1 : 0;
    }

    public static List<Playlist> getAllPlaylists() {
        List<Playlist> playlist = new ArrayList<>();
        for (char c = 'a'; c < 'z'; c++) {
            for (int i = 0; i < 4 + getRandomBit(); i++) {
               // playlist.add(new Playlist(PLAYLIST, ((char) c) + "Playlist " + i, 0, " Me"));
            }
        }
        return playlist;
    }


    public static void setVisibility(final View view, final int visibility) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = view.getMeasuredWidth() / 2;
            int cy = view.getMeasuredHeight() / 2;

            // get the final radius for the clipping circle
            int radius = Math.max(view.getWidth(), view.getHeight()) / 2;

            if (StatedAnimatorListener.animatorListenerHashMap.contains(view)) {
                Log.d("TGolomb","setVisibility <- item already here");
                view.clearAnimation();
                StatedAnimatorListener.animatorListenerHashMap.remove(view);
                view.setVisibility(visibility);
                return;
            }

            if (visibility == View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, radius);
                anim.addListener(StatedAnimatorListener.getInstance(view, visibility));
                anim.start();
            } else {
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, radius, 0);
                anim.addListener(StatedAnimatorListener.getInstance(view, visibility));
                anim.start();
            }
        }
    }

    public static Drawable getDrawable(Context context, int resourceId, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resourceId, theme);
        } else {
            return context.getResources().getDrawable(resourceId);
        }
    }

    public static void changeBackgroundDrawable(final View view, int colorFromId, int colorToId, final int drawableId) {
        int colorFrom = getColor(view.getContext(), colorFromId, null);
        int colorTo = getColor(view.getContext(), colorToId, null);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setBackgroundResource(drawableId);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        colorAnimation.start();
    }

    public static boolean getMixedMode() {
        return false;
    }

    public static RepeatMode getRepeatMode() {
        return RepeatMode.REPEAT_LIST;
    }

    public static void Log(String s, Object object) {

    }

}

class StatedAnimatorListener implements Animator.AnimatorListener {
    static Set<View> animatorListenerHashMap = new HashSet<>();
    private static Queue<StatedAnimatorListener> animatorListenerPool;
    private View view;
    private int visibility;

    static StatedAnimatorListener getInstance(View view, int visibility) {
        if (animatorListenerPool == null) {
            animatorListenerPool = new ConcurrentLinkedQueue<>();
        }
        if (animatorListenerPool.size() == 0) {
            animatorListenerPool.add(new StatedAnimatorListener());
        }
        StatedAnimatorListener listener = animatorListenerPool.remove();
        listener.setUp(view, visibility);
        return listener;
    }

    public void setUp(View view, int visibility) {
        this.view = view;
        this.visibility = visibility;
    }

    /**
     * Add the view to the set of animated view, if it's not there
     */
    @Override
    public void onAnimationStart(Animator animation) {
        Log.d("TGolomb","animation start on view ");
        if (!animatorListenerHashMap.contains(view)) {
            animatorListenerHashMap.add(view);
        }
    }

    /**
     * remove the view to the set of animated view, if it's not there
     * if it was there then the animation wasn't interrupted, and we can change the visibility
     */
    @Override
    public void onAnimationEnd(Animator animation) {
        Log.d("TGolomb","animation end on view ");
        if (animatorListenerHashMap.contains(view)) {
            animatorListenerHashMap.remove(view);
            if (visibility != View.VISIBLE) {
                view.setVisibility(visibility);
            }
        }
        animatorListenerPool.add(this);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        Log.d("TGolomb","animation cancel on view ");
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
