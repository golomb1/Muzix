package com.apps.golomb.muzix.ExtendedRecycleView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by golomb on 13/07/2016.
 * This class should represent a recycle view with customs option build in.
 */
public class ExtendedRecycleView extends RecyclerView {

    private Context mContext;
    private ItemTouchHelper mTouchHelper;
    private ItemTouchHelper.Callback mCallback;

    // list of the views that we want to show when the list is not empty.
    private List<View> mNonEmptyViews = Collections.emptyList();
    // list of the views that we want to show when the list is empty.
    private List<View> mEmptyViews = Collections.emptyList();

    // this represent a data observer, it
    private AdapterDataObserver mObserver = new AdapterDataObserver() {

        @Override
        public void onChanged() {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            toggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            toggleViews();
        }
    };

    private void toggleViews() {
        if (getAdapter() != null && !mEmptyViews.isEmpty() && !mNonEmptyViews.isEmpty()) {
            if (getAdapter().getItemCount() == 0) {

                //show all the empty views
                showViews(mEmptyViews);
                //hide the RecyclerView
                setVisibility(View.GONE);

                //hide all the views which are meant to be hidden
                hideViews(mNonEmptyViews);
            } else {
                //hide all the empty views
                showViews(mNonEmptyViews);

                //show the RecyclerView
                setVisibility(View.VISIBLE);

                //hide all the views which are meant to be hidden
                hideViews(mEmptyViews);
            }
        }
    }

    private void showViews(List<View> viewList) {
        for (View view : viewList) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void hideViews(List<View> viewList) {
        for (View view : viewList) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
            if(adapter instanceof ExtendedRecycleAdapter){
                ((ExtendedRecycleAdapter)adapter).setTouchHelper(mTouchHelper,mCallback);
            }
        }
        mObserver.onChanged();
    }

    public void hideIfEmpty(View... views) {
        mNonEmptyViews = Arrays.asList(views);
    }

    public void showIfEmpty(View... emptyViews) {
        mEmptyViews = Arrays.asList(emptyViews);
    }

    public void enableDefaultDivider(Context context, int orientation){
        this.addItemDecoration(new DividerItemDecoration(context,orientation));
    }

    public void enableDefaultDivider(int orientation){
        this.enableDefaultDivider(mContext,orientation);
    }

    public void enableDefaultAnimation(){
        this.setItemAnimator(new DefaultItemAnimator());
    }

    public void enableDefaultTouchCallback(ItemTouchHelper.Callback callback){
        mTouchHelper = new ItemTouchHelper(callback);
        mCallback = callback;
        mTouchHelper.attachToRecyclerView(this);
        Adapter adapter = getAdapter();
        if(adapter != null && adapter instanceof ExtendedRecycleAdapter){
            ((ExtendedRecycleAdapter)adapter).setTouchHelper(mTouchHelper,mCallback);
        }
    }

    public void setDefaultLayoutManager(){
        setLayoutManager(new LinearLayoutManager(mContext));
    }



    public void initializeDefault(Context context,int orientation,ItemTouchHelperAdapter touchHelperAdapter,ExtendedRecycleAdapter adapter){
        mContext = context;
        //enableDefaultDivider(orientation);
        enableDefaultAnimation();
        enableDefaultTouchCallback(new ExtendedTouchCallback(touchHelperAdapter));
        setDefaultLayoutManager();
        setAdapter(adapter);
    }

    public void initializeDefault(Context context,int orientation,ExtendedRecycleAdapter adapter){
        initializeDefault(context,orientation,adapter,adapter);
    }

    public ExtendedRecycleView(Context context) {
        super(context);
    }

    public ExtendedRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
