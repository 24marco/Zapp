package com.indio.android.zapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.indio.android.zapp.data.DataContract;

import java.util.Calendar;
import java.util.Vector;

/**
 * Created by Marco Gullo on 23/03/2015.
 * query data and save in local database
 * update in Shared Preferences COUNT: contacts stored in database
 */
public class DataTask extends AsyncTask<Void, Void, Vector<ContentValues>> {

    private Context context;                                                // context
    private SharedPreferences.Editor editor;                                // Shared Preferences
    private boolean waitDialog;                                             // if true shows Toast message during async task
    private final ContentResolver contentResolver;                          // contentResolver
    private Vector<ContentValues> cVVector = new Vector<ContentValues>();               // vector that holds contentValues pair needed for bulkInsert


    public DataTask(Context context, SharedPreferences.Editor editor, boolean waitDialog) {
        this.context = context;
        this.contentResolver = this.context.getContentResolver();
        this.editor = editor;
        this.waitDialog = waitDialog;
    }


    /*
     * Query ContactsContract table, build contentValue pair to append to a vector
     * uses only contacts with phoneNumber
     */
    @Override
    protected Vector<ContentValues> doInBackground(Void... params) {

        int j = 0;

//        Log.e("Post", "Async - start");

        String[] s = {DataContract.DataEntry._ID,
                DataContract.DataEntry.COLUMN_CONTACT_ID,
                DataContract.DataEntry.COLUMN_DISPLAY_NAME_PRIMARY,
                DataContract.DataEntry.COLUMN_LAST_TIME_CONTACTED,
                DataContract.DataEntry.COLUMN_TIMES_CONTACTED,
                DataContract.DataEntry.COLUMN_EMAIL_DATA,
                DataContract.DataEntry.COLUMN_POSTAL_DATA,
                DataContract.DataEntry.COLUMN_NUMBER1,
                DataContract.DataEntry.COLUMN_NUMBER2,
                DataContract.DataEntry.COLUMN_NUMBER3,
                DataContract.DataEntry.COLUMN_NUMBER4};

        String[] contactSelection = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.LAST_TIME_CONTACTED,
                ContactsContract.Contacts.TIMES_CONTACTED};
        String[] phoneSelection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String[] emailSelection = {ContactsContract.CommonDataKinds.Email.DATA};
        String[] postalSelection = {ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS};

        int count = s.length;

        Cursor cursorContacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, contactSelection, ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?", new String[]{"1"}, null);

        if (cursorContacts != null && cursorContacts.getCount() > 0)
            while (cursorContacts.moveToNext()) {

                ContentValues dataValues = new ContentValues();
                String _id = cursorContacts.getLong(0) + "";                            // search for _ID in Contacts table
                dataValues.put(s[1], _id);                                              // NAME_RAW_CONTACT_ID
                dataValues.put(s[2], cursorContacts.getString(1));                      // DISPLAY_NAME
                dataValues.put(s[3], cursorContacts.getLong(2) + "");                   // LAST_TIME_CONTACTED
                dataValues.put(s[4], cursorContacts.getInt(3));                         // TIMES_CONTACTED

                Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, emailSelection, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{_id}, null);
                if (emailCursor != null && emailCursor.getCount() > 0) {
                    emailCursor.moveToFirst();
                    dataValues.put(s[5], emailCursor.getString(0));
                }

                Cursor postalCursor = contentResolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, postalSelection, ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?", new String[]{_id}, null);
                if (postalCursor != null && postalCursor.getCount() > 0) {
                    postalCursor.moveToFirst();
                    dataValues.put(s[6], postalCursor.getString(0));
                }

                Cursor phoneNumberCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, phoneSelection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{_id}, null);

                int i = 7;
                if (phoneNumberCursor != null && phoneNumberCursor.getCount() > 0)
                    while (phoneNumberCursor.moveToNext() && i < count) {
                        dataValues.put(s[i], phoneNumberCursor.getString(0));                         // NUMBER
                        i++;
                    }
                if (phoneNumberCursor != null && !phoneNumberCursor.isClosed())
                    phoneNumberCursor.close();
                if (emailCursor != null && !emailCursor.isClosed()) emailCursor.close();
                if (postalCursor != null && !postalCursor.isClosed()) postalCursor.close();

                dataValues.put(s[0], j);

                cVVector.add(dataValues);
                j++;
            }
        if (cursorContacts != null && !cursorContacts.isClosed()) cursorContacts.close();

        return cVVector;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (waitDialog) {
            Toast.makeText(context, "reading contacts ...", Toast.LENGTH_SHORT).show();
        }
    }


    /*
     * convert vector data to array, delete database and fill data in a brand new database
     */
    @Override
    protected void onPostExecute(Vector<ContentValues> contentValueses) {
        if (waitDialog) {
            Toast.makeText(context, "... done", Toast.LENGTH_SHORT).show();
        }

        if (contentValueses.size() > 0) {

            // from vector to array
            ContentValues[] cvArray = new ContentValues[contentValueses.size()];
            contentValueses.toArray(cvArray);

            // call delete to clean database table
            contentResolver.delete(DataContract.DataEntry.CONTENT_URI, null, null);
            // call bulkInsert to add data to the database
            int count = contentResolver.bulkInsert(DataContract.DataEntry.CONTENT_URI, cvArray);

            editor.putInt("COUNT", count).commit();             // save in Shared Preferences "number of contact stored in database"
            MainActivity.taskOn = false;

            Calendar c = Calendar.getInstance();                // save in Shared Preferences "lastUpdate" in ms
            long lastUpdate = (c.getTime()).getTime();
            editor.putLong("LAST_UPDATE", lastUpdate).commit();
        }
    }
}
