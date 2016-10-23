package com.apps.golomb.muzix.utils;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import com.apps.golomb.muzix.R;
import com.apps.golomb.muzix.entities.FlexiblePair;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by tomer on 15/10/2016.
 * This class is a util class to contains a global media operations.
 */

public class MediaUtils {
    private final static Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    public static void loadSongArt(Context context, ImageView view, final long albumId){
        Uri uri = ContentUris.withAppendedId(sArtworkUri,
                albumId);
        Glide.with(context).load(uri).listener(new RequestListener<Uri, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        })
                .placeholder(R.drawable.default_album)
                .error(R.drawable.default_album)
                .crossFade().centerCrop().into(view);
    }

    public static ArrayList<FlexiblePair<Integer,String>> getAllPlaylistsName() {
        ArrayList<FlexiblePair<Integer,String>> list = new ArrayList<>();
        int index = 0;
        for(int c='a'; c <= 'z' ; c++){
            list.add(new FlexiblePair<>(index, (char)c + " playlist 1"));
            list.add(new FlexiblePair<>(index + 1, (char)c + " playlist 2"));
            list.add(new FlexiblePair<>(index + 2, (char)c + " playlist 3"));
            list.add(new FlexiblePair<>(index + 3, (char)c + " playlist 4"));
            list.add(new FlexiblePair<>(index + 4, (char)c + " playlist 5"));
            index += 5;
        }
        return list;
    }
}
