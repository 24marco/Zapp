package com.indio.android.zapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.indio.android.zapp.data.DataContract.DataEntry;

/*
 * Created by Marco Gullo on 22/03/2015.
 * Manages a local database for contact statistic data.
 */

public class DataDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "contact.db";


    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // create data table
        final String SQL_CREATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS " + DataEntry.TABLE_NAME + " (" +
                DataEntry._ID + " INTEGER, " +
                DataEntry.COLUMN_CONTACT_ID + " TEXT, " +
                DataEntry.COLUMN_DISPLAY_NAME_PRIMARY + " TEXT, " +
                DataEntry.COLUMN_LAST_TIME_CONTACTED + " TEXT, " +
                DataEntry.COLUMN_TIMES_CONTACTED + " REAL, " +
                DataEntry.COLUMN_EMAIL_DATA + " TEXT, " +
                DataEntry.COLUMN_POSTAL_DATA + " TEXT, " +
                DataEntry.COLUMN_NUMBER1 + " TEXT, " +
                DataEntry.COLUMN_NUMBER2 + " TEXT, " +
                DataEntry.COLUMN_NUMBER3 + " TEXT, " +
                DataEntry.COLUMN_NUMBER4 + " TEXT" +
                ");";

//        Log.e("Post", SQL_CREATE_DATA_TABLE);
        db.execSQL(SQL_CREATE_DATA_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME);
        onCreate(db);
    }
}
