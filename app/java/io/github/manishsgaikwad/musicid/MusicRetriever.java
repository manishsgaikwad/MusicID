package io.github.manishsgaikwad.musicid;

/**
 * Created by manish on 02-12-2016.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Retrieves and organizes media to play. Before being used, you must call {@link #prepare()},
 * which will retrieve all of the music on the user's device (by performing a query on a content
 * resolver). After that, it's ready to retrieve a random song, with its title and URI, upon
 * request.
 */
public class MusicRetriever {
    final String TAG = "MusicRetriever";
    final String TAG1 = "CoverArtError";
    ContentResolver mContentResolver;
    Context mContext;
                // the items (songs) we have queried
    List<MusicRetriever.Item> mItems = new ArrayList<>();
    public MusicRetriever(ContentResolver cr, Context c) {
        mContentResolver = cr;
        mContext = c;
    }
    /**
     * Loads music data. This method may take long, so be sure to call it asynchronously without
     * blocking the main thread.
     */

    public void prepare() {
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.i(TAG, "Querying media...");
        Log.i(TAG, "URI: " + uri.toString());
        // Perform a query on the content resolver. The URI we're passing specifies that we
        // want to query for all audio media on external storage (e.g. SD card)
        Cursor cur = mContentResolver.query(uri, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));
        if (cur == null) {
            // Query  failed...
            Log.e(TAG, "Failed to retrieve music: cursor is null :-(");
            return;
        }
        if (!cur.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            Log.e(TAG, "Failed to move cursor to first row (no query results).");
            return;
        }
        Log.i(TAG, "Listing...");
        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        int column_index = cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        int albumIdColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        int dataPathColumn = cur.getColumnIndex(MediaStore.Audio.Media.DATA);


       // Log.i(TAG, "Title column index: " + String.valueOf(titleColumn));
        //Log.i(TAG, "ID column index: " + String.valueOf(titleColumn));
        Log.i("Path:",cur.getString(dataPathColumn));

        final  Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");


        // add each song to mItems
        do {
          //  Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn) + " Path " + cur.getString(dataPathColumn));


            Uri artworkUri = ContentUris.withAppendedId(sArtworkUri,cur.getLong(albumIdColumn));


                mItems.add(new Item(
                        cur.getLong(idColumn),
                        cur.getString(artistColumn),
                        cur.getString(titleColumn),
                        cur.getString(albumColumn),
                        cur.getLong(durationColumn),
                        artworkUri,
                        cur.getString(dataPathColumn),
                        cur.getLong(albumIdColumn)));


        } while (cur.moveToNext());
        Log.i(TAG, "Done querying media. MusicRetriever is ready.");
        cur.close();
    }
    public ContentResolver getContentResolver() {
        return mContentResolver;
    }


    /** Returns a random Item. If there are no items available, returns null. */
    public List<MusicRetriever.Item> getItems() {
        if (mItems.size() <= 0) return null;
        return mItems;
    }

    public static class Item implements Parcelable {
        long id;
        String artist;
        String title;
        String album;
        long duration;
        Uri albumartUri;
        String path;
        long albumID;

        public Item(long id, String artist, String title, String album, long duration,Uri albumartUri,String p, long aID) {
            this.id = id;
            this.artist = artist;
            this.title = title;
            this.album = album;
            this.duration = duration;
            this.albumartUri = albumartUri;
            this.path=p;
            this.albumID=aID;
        }



        public long getAlbumID(){
            return this.albumID;
        }

        public Uri getAlbumartUri() {
            return albumartUri;
        }

        public String getPath() {
            return path;
        }

        public long getId() {
            return id;
        }
        public String getArtist() {
            return artist;
        }
        public String getTitle() {
            return title;
        }
        public String getAlbum() {
            return album;
        }
        public long getDuration() {
            return duration;
        }
        public Uri getURI() {
            return ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
            dest.writeString(this.artist);
            dest.writeString(this.title);
            dest.writeString(this.album);
            dest.writeLong(this.duration);
            dest.writeParcelable(this.albumartUri, flags);
            dest.writeString(this.path);
            dest.writeLong(this.albumID);
        }

        protected Item(Parcel in) {
            this.id = in.readLong();
            this.artist = in.readString();
            this.title = in.readString();
            this.album = in.readString();
            this.duration = in.readLong();
            this.albumartUri = in.readParcelable(Uri.class.getClassLoader());
            this.path = in.readString();
            this.albumID = in.readLong();
        }

        public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel source) {
                return new Item(source);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
    }
}