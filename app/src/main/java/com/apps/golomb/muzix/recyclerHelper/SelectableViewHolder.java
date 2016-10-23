package com.apps.golomb.muzix.recyclerHelper;

import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.customviews.FlipAutoResizeTextView;
import com.apps.golomb.muzix.utils.Utils;
import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by tomer on 19/10/2016.
 * This class was made in order to join the logic of selection and active mode in a single class.
 */

public class SelectableViewHolder extends ExtendedViewHolder {


    private final RelativeLayout mSelectorPanel;
    private final FlipAutoResizeTextView mSelectorText;

    public SelectableViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        mSelectorPanel = (RelativeLayout) itemView.findViewById(R.id.icon_view);
        mSelectorText = (FlipAutoResizeTextView) itemView.findViewById(R.id.selection_text);
        mSelectorPanel.setVisibility(View.INVISIBLE);
    }

    @SuppressWarnings("unused")
    public SelectableViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
        super(view, adapter, stickyHeader);
        mSelectorPanel = (RelativeLayout) itemView.findViewById(R.id.icon_view);
        mSelectorText = (FlipAutoResizeTextView) itemView.findViewById(R.id.selection_text);
        mSelectorPanel.setVisibility(View.INVISIBLE);
    }


    @Override
    public void enterPlayedMode() {
        super.enterPlayedMode();
        //getFrontView().setBackgroundResource(R.drawable.selector_active_item_light);
        getFrontView().setBackgroundResource(R.drawable.item_transition_background);
        TransitionDrawable transition = (TransitionDrawable)  getFrontView().getBackground();
        transition.startTransition(500);
    }

    @Override
    public void exitPlayedMode() {
        super.exitPlayedMode();
        //getFrontView().setBackgroundResource(R.drawable.selector_item_light);
        getFrontView().setBackgroundResource(R.drawable.item_rev_transition_background);
        TransitionDrawable transition = (TransitionDrawable)  getFrontView().getBackground();
        transition.reverseTransition(500);
    }

    @Override
    public void unbindInPlayedMode() {
        super.unbindInPlayedMode();
        getFrontView().setBackgroundResource(R.drawable.selector_item_light);
    }

    @Override
    public void bindInPlayedMode() {
        super.bindInPlayedMode();
        getFrontView().setBackgroundResource(R.drawable.selector_active_item_light);
    }

    @Override
    public void enterActiveMode(int position) {
        super.enterActiveMode(position);
        getFrontView().setSelected(true);
        Log.d("TGolomb", "enterActiveMode");
        Utils.setVisibility(mSelectorPanel, View.VISIBLE);
        updateActiveMode(position);
    }

    @Override
    public void updateActiveMode(int value) {
        mSelectorText.setText(String.valueOf(value + 1));
    }

    @Override
    public void exitActiveMode() {
        super.exitActiveMode();
        Log.d("TGolomb", "enterActiveMode");
        getFrontView().setSelected(false);
        Utils.setVisibility(mSelectorPanel, View.INVISIBLE);
    }

    @Override
    public void bindInActiveMode(int position) {
        super.bindInActiveMode(position);
        getFrontView().setActivated(true);
        mSelectorPanel.setVisibility(View.VISIBLE);
        mSelectorText.setText(String.valueOf(position + 1), false);
    }

    @Override
    public void unbindInActiveMode() {
        super.unbindInActiveMode();
        getFrontView().setActivated(false);
        mSelectorPanel.setVisibility(View.INVISIBLE);
    }

}