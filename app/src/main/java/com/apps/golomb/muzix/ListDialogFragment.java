package com.apps.golomb.muzix;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.FlexiblePair;
import com.apps.golomb.muzix.entities.PlaylistData;
import com.apps.golomb.muzix.interfaces.CreatePlaylistDialog;
import com.apps.golomb.muzix.recyclerHelper.PlaylistAdapter;
import com.apps.golomb.muzix.recyclerHelper.SimpleListHeader;
import com.apps.golomb.muzix.utils.Utils;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Created by tomer on 21/10/2016.
 * This is a simple List Dialog that composed with a recycler view and a toolbar.
 */

public class ListDialogFragment
        extends DialogFragment
        implements FlexibleAdapter.OnItemClickListener, FastScroller.OnScrollStateChangeListener, CreatePlaylistDialog.DialogResultReceiver {

    public static final String LIST_ARG_KEY = "LIST_ARG_KEY";
    public static final String LIST_PAYLOAD = "LIST_PAYLOAD";
    private List<MuzixSong> payload;
    private PlaylistAdapter<PlaylistData> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //************************************************
        //              arguments
        //************************************************

        Bundle args = getArguments();
        ArrayList<FlexiblePair<Integer,String>> list = (ArrayList<FlexiblePair<Integer,String>>) args.getSerializable(LIST_ARG_KEY);
        payload = (List<MuzixSong>) args.getSerializable(LIST_PAYLOAD);

        //************************************************
        //              initiate view
        //************************************************

        View rootView = inflater.inflate(R.layout.list_dialog, container);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Select a playlist");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        FastScroller scroller = (FastScroller) rootView.findViewById(R.id.fast_scroller_dialog);

        getDialog().setTitle("DialogFragment Tutorial");

        //************************************************
        //              set adapter
        //************************************************

        adapter =
                new PlaylistAdapter<>(list,R.layout.list_dialog,null, this, false);
        adapter.setTopHeader(new SimpleListHeader());
        adapter.setStickySectionHeadersHolder((ViewGroup) rootView.findViewById(R.id.sticky_header_container));
        adapter.enableStickyHeaders();
        adapter.setDisplayHeadersAtStartUp(true);
        adapter.setRemoveOrphanHeaders(true);

        adapter.setMode(SelectableAdapter.MODE_IDLE);
        recyclerView.setAdapter(adapter);

        // drag
        adapter.setHandleDragEnabled(false);
        //Enable Swipe-To-Dismiss
        adapter.setSwipeEnabled(false);
        adapter.setLongPressDragEnabled(false);
        // fast scroll
        int color = Utils.getScrollColor(getContext());
        adapter.setFastScroller(scroller, color, this);
        adapter.setAnimationOnScrolling(true);
        adapter.toggleFastScroller();

        return rootView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onItemClick(int position) {
        if(position == 0){
            new CreatePlaylistDialog(this).show(getContext());
        }
        else{
            IFlexible iFlexible = adapter.getItem(position);
            if(iFlexible instanceof FlexiblePair){
                ((MainActivity)getActivity()).addToPlaylist((FlexiblePair) iFlexible,payload);
                this.dismiss();
            }
        }
        return false;
    }

    @Override
    public void onFastScrollerStateChange(boolean scrolling) {

    }

    @Override
    public void receiveDialogResult(String name, String details, Object payload) {
        ((MainActivity)getActivity()).createNewPlaylist(name,details,this.payload);
        this.dismiss();
    }
}
