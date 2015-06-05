package com.indio.android.zapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Marco Gullo on 22/03/2015.
 */

public class DataProvider extends ContentProvider {

    private static final int SORTED = 10;           // sorted contact list
    private static final int BY_ID = 20;            // selected contact details
    private static final int BULKINSERT = 30;       // DUMP for bulkInsert

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DataDbHelper mOpenHelper;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DataContract.PATH + "/*", SORTED);
        matcher.addURI(authority, DataContract.PATH + "/*/#", BY_ID);
        matcher.addURI(authority, DataContract.PATH, BULKINSERT);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new DataDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor c;

        switch (sUriMatcher.match(uri)) {

            case SORTED:

                c = mOpenHelper.getReadableDatabase().query(
                        DataContract.DataEntry.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
//                Log.e("Post", "SORTED");
                break;

            case BY_ID:

                String id = uri.getPathSegments().get(2);
                c = mOpenHelper.getReadableDatabase().query(
                        DataContract.DataEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder + " OFFSET " + id
                );
//                Log.e("Post", "BY_ID");
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }


    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SORTED:
                return DataContract.DataEntry.CONTENT_TYPE;
            case BY_ID:
                return DataContract.DataEntry.CONTENT_TYPE;
            case BULKINSERT:
                return DataContract.DataEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // never called
//        Log.e("Post", "insert called");
        mOpenHelper.getWritableDatabase().insert(DataContract.DataEntry.TABLE_NAME, null, null);

        // ContentProvider notifies ContentResolver about changes:
        getContext().getContentResolver().notifyChange(uri, null);

        return null;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

//        Log.e("Post", "delete called really");
        mOpenHelper.getWritableDatabase().delete(DataContract.DataEntry.TABLE_NAME, null, null);

        // ContentProvider notifies ContentResolver about changes:
        getContext().getContentResolver().notifyChange(uri, null);

        return 0;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // never called
//        Log.e("Post", "update called");
        mOpenHelper.getWritableDatabase().update(DataContract.DataEntry.TABLE_NAME, null, null, null);

        // ContentProvider notifies ContentResolver about changes:
        getContext().getContentResolver().notifyChange(uri, null);

        return 0;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BULKINSERT:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DataContract.DataEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default: {
                return super.bulkInsert(uri, values);
            }
        }
    }
}
