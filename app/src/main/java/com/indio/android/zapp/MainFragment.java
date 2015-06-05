package com.indio.android.zapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.indio.android.zapp.data.DataContract;

/**
 * Created by Marco Gullo on 08/05/2015.
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private int mPosition;                                              // position selected by user in the main list
    private Context mContext;                                           // context
    private DataAdapter myAdapter;                                      // list adapter
    private ListView myListView;                                        // list widget
    private final String[] projection = {                               // data displayed in the main list
            DataContract.DataEntry.COLUMN_DISPLAY_NAME_PRIMARY,
            DataContract.DataEntry.COLUMN_LAST_TIME_CONTACTED,
            DataContract.DataEntry.COLUMN_TIMES_CONTACTED,
            DataContract.DataEntry.COLUMN_NUMBER1,
            DataContract.DataEntry.COLUMN_NUMBER2
    };
    private String sorting;                                             // sort choosed for display main list
    private final int MAIN_LOADER = 1;                                  // main list cursorloader
    private SharedPreferences pref;                                     // SharedPreferences
    private boolean resort;                                             // boolean flag: true if user change displayed data sorting
    private int top, index;                                             // main list diaply references


    interface displayDetailListener {                                   // interface to MainActivity
        void displayDetail(int position);
    }


    public MainFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getActivity();
        sorting = getArguments().getString("SORTING");
        resort = getArguments().getBoolean("RESORT", false);

        if (resort) {                                                               // if true user want to re-display data choosing sort order by menu
            index = 0;
            top = 0;
            mPosition = 0;
        } else {                                                                    // if false restore last displayed status
            index = pref.getInt("FIRST_VISIBLE_POSITION", 0);
            top = pref.getInt("PADDING_TOP", 0);
            mPosition = pref.getInt("SELECTION", 0);
        }
        myAdapter = new DataAdapter(mContext, null, 0, sorting);                    // prepare an empty adapter
        myListView = (ListView) rootView.findViewById(R.id.display_name_list_view);
        myListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        myListView.setAdapter(myAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> rootView, View view, int position, long id) {
                ((displayDetailListener) mContext).displayDetail(position);         // callback to MainActivity
                mPosition = position;
            }
        });

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MAIN_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    /*
     * Shared Preferences: manages displayed status
     */

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = pref.edit();
        int index = myListView.getFirstVisiblePosition();
        View v = myListView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - myListView.getPaddingTop());

        editor.putInt("FIRST_VISIBLE_POSITION", index).commit();
        editor.putInt("PADDING_TOP", top).commit();
        editor.putInt("SELECTION", mPosition).commit();
    }


    /*
     * depending on "sorting" query the database
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl;

        if (sorting.equals("NAME")) {
            cl = new CursorLoader(mContext,
                    DataContract.DataEntry.buildDataUri("NAME"),
                    projection,
                    null,
                    null,
                    DataContract.DataEntry.COLUMN_DISPLAY_NAME_PRIMARY + " ASC"
            );

        } else if (sorting.equals("LAST")) {
            cl = new CursorLoader(mContext,
                    DataContract.DataEntry.buildDataUri("LAST"),
                    projection,
                    null,
                    null,
                    DataContract.DataEntry.COLUMN_LAST_TIME_CONTACTED + " DESC"
            );

        } else {
            cl = new CursorLoader(mContext,
                    DataContract.DataEntry.buildDataUri("MOST"),
                    projection,
                    null,
                    null,
                    DataContract.DataEntry.COLUMN_TIMES_CONTACTED + " DESC"
            );
        }

        return cl;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (myAdapter != null && data != null) {
            myAdapter.swapCursor(data);

            /*
             * // managing item position visibility on orientation change && TWO PANE mode
             * if (mPosition > myListView.getLastVisiblePosition() && getActivity().findViewById(R.id.container1) != null) {
             * myListView.smoothScrollToPosition(mPosition);
             * }
             */

            /*
             * restore display list status
             */
            myListView.setItemChecked(mPosition, true);
            myListView.setSelectionFromTop(index, top);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (myAdapter != null) myAdapter.swapCursor(null);
    }

}
