package com.apps.golomb.muzix.recyclerHelper;

import android.support.v7.widget.GridLayoutManager;

/**
 * Created by tomer on 11/10/2016.
 * To identify span items and get their span.
 */

interface SpanFlexible{
    int getSpan(GridLayoutManager manager);
}
