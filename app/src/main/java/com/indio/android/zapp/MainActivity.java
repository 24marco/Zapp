package com.indio.android.zapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.indio.android.zapp.data.DataContract;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Marco Gullo on 16/03/2015.
 */

public class MainActivity extends ActionBarActivity implements MainFragment.displayDetailListener {

    private String sorting;                                 // sorting mode: "NAME", "LAST", "MOST"
    private long lastUpdate;                                // last update, time in ms
    private int contactsCount;                              // number of contacts inseted in database
    private boolean twopane = false;                        // ONE_PANE or TWO_PANE layout
    private SharedPreferences pref;                         // SharedPreferences
    private SharedPreferences.Editor editor;                // editor for SharedPreferences
    private int mainFrag_container, detailFrag_container;   // R.id view
    private FragmentManager fm;                             // fragment manager
    protected static boolean taskOn;                        // true if an AsyncTask is already running


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            taskOn = false;
        } else {
            taskOn = savedInstanceState.getBoolean("TASK");
        }

        /*
         *  initialization: check PANE mode
         */
        if (findViewById(R.id.container1) != null) {
            twopane = true;
            mainFrag_container = R.id.container1;
            detailFrag_container = R.id.container2;
        } else {
            twopane = false;
            mainFrag_container = detailFrag_container = R.id.main_container;
        }

        /*
         * Shared Preferences: manages status
         * SORTING: string on disaplyed data sorting
         * LAST_UPDATE: long on last database update time in ms
         * COUNT: int on contacts stored in the database
         * SELECTION: int on item position in main list
         * FIRST_VISIBLE_POSITION: int on main list display
         * PADDING_TOP: int on main list display
         */
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        sorting = pref.getString("SORTING", null);
        lastUpdate = pref.getLong("LAST_UPDATE", 0);
        contactsCount = pref.getInt("COUNT", 0);

        if (twopane) {
            if (savedInstanceState == null) {
                if (sorting != null) { // not first run, inflateFragments & query local database
                    Bundle bundle = new Bundle();
                    bundle.putString("SORTING", sorting);
                    MainFragment myFragment = new MainFragment();
                    myFragment.setArguments(bundle);
                    fm.beginTransaction().replace(mainFrag_container, myFragment).commit();

                    int position = pref.getInt("SELECTION", 0);
                    displayDetail(position);

                    if (updateNeeded()) {
                        taskOn = true;
                        DataTask myDataTask = new DataTask(this, editor, false);
                        myDataTask.execute();
                    }
                } else {
                    // first of all the times: first time, bulk insert & onDataTaskComplete inflateFragment & query local database
                    initDataTask(true);
                }
            }
        } else { // ONE_PANE
            if (savedInstanceState == null) {
                if (sorting != null) { // not first run, inflateFragments & query local database
                    Bundle bundle = new Bundle();
                    bundle.putString("SORTING", sorting);
                    MainFragment myFragment = new MainFragment();
                    myFragment.setArguments(bundle);
                    fm.beginTransaction().replace(mainFrag_container, myFragment).commit();
//                    myFragment.setRetainInstance(true);
                    if (updateNeeded()) {
                        taskOn = true;
                        DataTask myDataTask = new DataTask(this, editor, false);
                        myDataTask.execute();
                    }
                } else {
                    // first of all the times: first time, bulk insert & onDataTaskComplete inflateFragment & query local database
                    initDataTask(true);
                }
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("TASK", taskOn);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_refresh:                                                       // async task, bulk insert & onDataTaskComplete inflateFragment & query local database

                if (!taskOn) {
                    initDataTask(true);
                    taskOn = true;
                }
                break;

            case R.id.action_about:                                                         // call InfoActivity passing lastUpdate as extra

                lastUpdate = pref.getLong("LAST_UPDATE", 0);
                contactsCount = pref.getInt("COUNT", 0);

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy-kk:mm:ss", Locale.ITALIAN);
                String formattedDate = df.format(lastUpdate);

                Bundle bundle = new Bundle();
                bundle.putString("LAST_UPDATE", formattedDate);
                bundle.putInt("COUNT", contactsCount);

                Intent intent = new Intent(this, AboutActivity.class).putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.action_sort_by_size:                                                  // Sorting icon with menu

                View action_sort = findViewById(R.id.action_sort_by_size);
                PopupMenu popup = new PopupMenu(MainActivity.this, action_sort);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_actions, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        Bundle bundle = new Bundle();
                        MainFragment myFragment = new MainFragment();
                        boolean resort = true;
                        switch (item.getItemId()) {

                            case R.id.action_sort_name:
                                sorting = "NAME";
                                bundle.putString("SORTING", sorting);
                                bundle.putBoolean("RESORT", resort);
                                myFragment.setArguments(bundle);
                                fm.beginTransaction()
                                        .replace(mainFrag_container, myFragment)
                                        .commit();
                                editor.putString("SORTING", sorting).commit();      // Saving LAST_TIME_CONTACTED

                                if (twopane) {
                                    displayDetail(0);                               // in two pane mode display detail panel too, position 0
                                }

                                break;

                            case R.id.action_sort_last:
                                sorting = "LAST";
                                bundle.putString("SORTING", sorting);
                                bundle.putBoolean("RESORT", resort);
                                myFragment.setArguments(bundle);
                                fm.beginTransaction()
                                        .replace(mainFrag_container, myFragment)
                                        .commit();
                                editor.putString("SORTING", sorting).commit();      // Saving LAST_TIME_CONTACTED

                                if (twopane) {
                                    displayDetail(0);                               // in two pane mode display detail panel too, position 0
                                }

                                break;

                            case R.id.action_sort_most:
                                sorting = "MOST";
                                bundle.putString("SORTING", sorting);
                                bundle.putBoolean("RESORT", resort);
                                myFragment.setArguments(bundle);
                                fm.beginTransaction()
                                        .replace(mainFrag_container, myFragment)
                                        .commit();
                                editor.putString("SORTING", sorting).commit();      // Saving LAST_TIME_CONTACTED

                                if (twopane) {
                                    displayDetail(0);                               // in two pane mode display detail panel too, position 0
                                }

                                break;
                        }
                        return true;
                    }
                });

                popup.show();                                                       //showing popup menu

        }
        return super.onOptionsItemSelected(item);
    }


    /*
     * "initDataTask" is called in the first run after first installation or on "reload"
     * waitDialog if true shows Tpast messages on init and finish
     */
    public void initDataTask(boolean waitDialog) {

        DataTask myDataTask = new DataTask(this, editor, waitDialog);   // "myDataTask" retrieve contact data form local content provider and bulkinsert in a new database
        myDataTask.execute();                                           // "myDataTask" when finished calls "onDataTaskComplete()"
        sorting = "LAST";                                               // save in Shared Preferences "sorting", initialized to "LAST", (this is the default sorting mode)
        editor.putString("SORTING", sorting).commit();

        Bundle bundle = new Bundle();
        bundle.putString("SORTING", sorting);
        MainFragment myFragment = new MainFragment();
        myFragment.setArguments(bundle);
        fm.beginTransaction().replace(mainFrag_container, myFragment).commit();

        if (twopane) {
            displayDetail(0);                                           // in two pane mode display detail panel too, position 0 is default
        }
    }


    /* Callback from MainFragment
     * Display detail info on item clicked based on "position" parameter.
     * Because it is called by "onItemClickListener" and by cursorLoader's onLoadFinished
     * we distinguish using "fromLoadFinished" parameter
     */

    @Override
    public void displayDetail(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        bundle.putString("SORTING", sorting);
        if (!twopane) { // call DetailActivity
            Intent intent = new Intent(this, DetailActivity.class).putExtras(bundle);
            startActivity(intent);
        } else { // call replace DetailFragment TWO_PANE
            DetailFragment myFragment = new DetailFragment();
            myFragment.setArguments(bundle);
            fm.beginTransaction().replace(detailFrag_container, myFragment).commit();
        }
    }


    /* read last call inserted in the database end the last call in the Contract.Contatcs table
     * in order to decide if an update is needed
     */

    public boolean updateNeeded() {

        long aa = 0;
        long bb = 0;

        // last call in ContactsContract.Contacts table
        Cursor cursorContacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts.LAST_TIME_CONTACTED},
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ? ",
                new String[]{"1"},
                DataContract.DataEntry.COLUMN_LAST_TIME_CONTACTED + " DESC LIMIT 1");

        if (cursorContacts != null && cursorContacts.getCount() > 0) {
            cursorContacts.moveToFirst();
            aa = Long.parseLong(cursorContacts.getString(0));
            if (!cursorContacts.isClosed()) cursorContacts.close();
        }

        // last call in database table
        Cursor cursorDatabase = getContentResolver().query(DataContract.DataEntry.CONTENT_URI.buildUpon().appendPath("LAST").appendPath("0").build(),
                new String[]{DataContract.DataEntry.COLUMN_LAST_TIME_CONTACTED},
                null,
                null,
                DataContract.DataEntry.COLUMN_LAST_TIME_CONTACTED + " DESC LIMIT 1");

        if (cursorDatabase != null && cursorDatabase.getCount() > 0) {
            cursorDatabase.moveToFirst();
            bb = Long.parseLong(cursorDatabase.getString(0));
            if (!cursorDatabase.isClosed()) cursorDatabase.close();
        }

        boolean updateNeeded = ((aa - bb) > 0);
        return updateNeeded;
    }


}