package com.apps.golomb.muzix.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.apps.golomb.muzix.data.MuzixSong;
import com.apps.golomb.muzix.entities.PlaylistData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomer on 19/10/2016.
 * This class is responsible to query the device SQL-lite database for songs data.
 */

public class MediaContext {




    public static List<MuzixSong> getAllSongs(ContentResolver resolver) {
        List<MuzixSong> muzixSongList = new ArrayList<>();
        //retrieve song info
        Uri allSongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        // the data to retrieve from the music files.
        String[] dataType = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
        };


        Cursor musicCursor = resolver.query(allSongUri, dataType, selection, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int artistIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                int thisDuration = musicCursor.getInt(durationColumn);
                long thisArtistId = musicCursor.getLong(artistIdColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisAlbumId = musicCursor.getLong(albumIdColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                muzixSongList.add(new MuzixSong(thisId,thisTitle,thisDuration,thisArtistId,thisArtist,thisAlbumId,thisAlbum));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return muzixSongList;
    }

}
