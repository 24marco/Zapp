package com.indio.android.zapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Marco Gullo on 22/03/2015.
 */
public class DataContract {

    public static final String CONTENT_AUTHORITY = "com.indio.android.zapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH = "data";

    public static final class DataEntry implements BaseColumns {

        public static final String TABLE_NAME = "data";

        // Column with the DISPLAY_NAME_PRIMARY in data
        public static final String _ID = "_id";

        // Column with the DISPLAY_NAME_PRIMARY in data
        public static final String COLUMN_CONTACT_ID = "CONTACT_ID";

        // Column with the DISPLAY_NAME_PRIMARY in data
        public static final String COLUMN_DISPLAY_NAME_PRIMARY = "DISPLAY_NAME_PRIMARY";

        // Column with the LAST_TIME_CONTACTED by phone
        public static final String COLUMN_LAST_TIME_CONTACTED = "LAST_TIME_CONTACTED";

        // Column with the TIMES_CONTACTED by phone
        public static final String COLUMN_TIMES_CONTACTED = "TIMES_CONTACTED";

        // Column with the phone NUMBER1
        public static final String COLUMN_NUMBER1 = "NUMBER1";

        // Column with the phone NUMBER2
        public static final String COLUMN_NUMBER2 = "NUMBER2";

        // Column with the phone NUMBER3
        public static final String COLUMN_NUMBER3 = "NUMBER3";

        // Column with the phone NUMBER4
        public static final String COLUMN_NUMBER4 = "NUMBER4";

        // Column with the EMAIL_DATA
        public static final String COLUMN_EMAIL_DATA = "EMAIL";

        // Column with the POSTAL_DATA
        public static final String COLUMN_POSTAL_DATA = "POSTAL";



        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;



        public static Uri buildDataUri(String data) {
            return CONTENT_URI.buildUpon().appendPath(data).build();
        }
    }
}
