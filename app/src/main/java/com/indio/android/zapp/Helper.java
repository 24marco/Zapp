package com.indio.android.zapp;

import android.database.Cursor;

import com.indio.android.zapp.data.DataContract;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marco Gullo on 21/03/2015.
 */
public class Helper {

    /*
     * utility from milliseconds to date
     */
    public static String milliToDate(String milli) {
        long temp = Long.parseLong(milli);
        String dateString = null;

        if (temp != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - kk:mm:ss");
            dateString = formatter.format(new Date(temp));
        }
        return dateString;
    }



    /*
     * prepare detail data
     */
    public static String[] setStringData(Cursor myCursor) {

        String[] stringData = null;

        if (myCursor != null && myCursor.getCount() > 0) {
            stringData = new String[9];
            stringData[0] = myCursor.getString(myCursor.getColumnIndex(DataContract.DataEntry.COLUMN_DISPLAY_NAME_PRIMARY));
            stringData[1] = myCursor.getString(myCursor.getColumnIndex(DataContract.DataEntry.COLUMN_NUMBER1));
            stringData[2] = myCursor.getString(myCursor.getColumnIndex(DataContract.DataEntry.COLUMN_NUMBER2));
            stringData[3] = myCursor.getString(myCursor.getColumnIndex(DataContract.DataEntry.COLUMN_NUMBER3));
            stringData[4] = myCursor.getString(myCursor.getColumnIndex(DataContract.DataEntry.COLUMN_NUMBER4));
            stringData[5] = myCursor.getString(myCursor.getColumnIndex(DataContract.DataEntry.COLUMN_EMAIL_DATA));
            stringData[6] = myCursor.getString(myCursor.getColumnIndex(DataContract.DataEntry.COLUMN_POSTAL_DATA));
            stringData[7] = milliToDate(myCursor.getString(myCursor.getColumnIndex(DataContract.DataEntry.COLUMN_LAST_TIME_CONTACTED)));
            String temp = myCursor.getString(myCursor.getColumnIndex(DataContract.DataEntry.COLUMN_TIMES_CONTACTED));
            if (temp.equals("0")) {
                stringData[8] = null;
            } else {
                stringData[8] = temp;
            }
        }
        return stringData;
    }

}
