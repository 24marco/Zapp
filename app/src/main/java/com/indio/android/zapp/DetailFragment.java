package com.indio.android.zapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.indio.android.zapp.data.DataContract;

/**
 * Created by Marco Gullo on 20/03/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final String ZAPP_SHARE_HASHTAG = " #Zapp";                  // app HASHTAG
    private String message;                                                     // message to share
    private String[] field = new String[]{                                      // utility array
            "Name",
            "Phone 1",
            "Phone 2",
            "Phone 3",
            "Phone 4",
            "Email",
            "Address",
            "Last call",
            "Times"
    };
    private String[] stringData = null;                                         // will store detail data
    private RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6, rl7, rl8, rl9;         // point to DetailLayout fields
    private int position;                                                       // used to retrieve detail data from database
    private String sorting;                                                     // used to retrieve detail data from database
    private View parent;                                                        // layout view utility
    private Vibrator myVibrator;                                                // send button will vibrate
    private final int DETAIL_LOADER = 2;                                        // cursorLoader



    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        myVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        TextView sendTextView = (TextView) rootView.findViewById(R.id.send);                        // sharing "button" (it's a TextView indeed)
        sendTextView.setClickable(true);

        sendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVibrator.vibrate(30);                                                             // 30 ms vibration
                Intent sharingIntent = createShareDetailIntent();
                startActivity(Intent.createChooser(sharingIntent, "sending info: choose app"));
            }
        });

        parent = rootView;

        Bundle bundle = getArguments();
        position = bundle.getInt("POSITION");
        sorting = bundle.getString("SORTING");

        rl1 = (RelativeLayout) rootView.findViewById(R.id.rel1);                                    // point to layout key field
        rl2 = (RelativeLayout) rootView.findViewById(R.id.rel2);
        rl3 = (RelativeLayout) rootView.findViewById(R.id.rel3);
        rl4 = (RelativeLayout) rootView.findViewById(R.id.rel4);
        rl5 = (RelativeLayout) rootView.findViewById(R.id.rel5);
        rl6 = (RelativeLayout) rootView.findViewById(R.id.rel6);
        rl7 = (RelativeLayout) rootView.findViewById(R.id.rel7);
        rl8 = (RelativeLayout) rootView.findViewById(R.id.rel8);
        rl9 = (RelativeLayout) rootView.findViewById(R.id.rel9);

        /* Restore state */
        if (savedInstanceState != null) {
            boolean[] selected = savedInstanceState.getBooleanArray("selected");
            rl1.setSelected(selected[0]);
            rl2.setSelected(selected[1]);
            rl3.setSelected(selected[2]);
            rl4.setSelected(selected[3]);
            rl5.setSelected(selected[4]);
            rl6.setSelected(selected[5]);
            rl7.setSelected(selected[6]);
            rl8.setSelected(selected[7]);
            rl9.setSelected(selected[8]);
        } else {
            rl1.setSelected(true);
            rl2.setSelected(true);
            rl3.setSelected(false);
            rl4.setSelected(false);
            rl5.setSelected(false);
            rl6.setSelected(false);
            rl7.setSelected(false);
            rl8.setSelected(false);
            rl9.setSelected(false);
        }

        rl1.setOnClickListener(this);                                                               // layout field are clickable
        rl2.setOnClickListener(this);
        rl3.setOnClickListener(this);
        rl4.setOnClickListener(this);
        rl5.setOnClickListener(this);
        rl6.setOnClickListener(this);
        rl7.setOnClickListener(this);
        rl8.setOnClickListener(this);
        rl9.setOnClickListener(this);

//        Toast.makeText(getActivity(),"Please select fields to share",Toast.LENGTH_SHORT).show();    // instruct user

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail_share, menu);
    }

    /*
     * on send button pressed an app will e launched
     */
    private Intent createShareDetailIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message + ZAPP_SHARE_HASHTAG);
        return shareIntent;
    }


    /*
     * create message including selected field
     */
    private void prepareMessage() {
        message = "";
        if (stringData != null) {
            if (rl1.isSelected()) message += field[0] + ": " + stringData[0] + "\n";
            if (rl2.isSelected()) message += field[1] + ": " + stringData[1] + "\n";
            if (rl3.isSelected()) message += field[2] + ": " + stringData[2] + "\n";
            if (rl4.isSelected()) message += field[3] + ": " + stringData[3] + "\n";
            if (rl5.isSelected()) message += field[4] + ": " + stringData[4] + "\n";
            if (rl6.isSelected()) message += field[5] + ": " + stringData[5] + "\n";
            if (rl7.isSelected()) message += field[6] + ": " + stringData[6] + "\n";
            if (rl8.isSelected()) message += field[7] + ": " + stringData[7] + "\n";
            if (rl9.isSelected()) message += field[8] + ": " + stringData[8] + "\n";
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader c = null;

        //retrieve item details form database
        String pos = String.valueOf(position);
        String sortOrder;

        if (sorting.equals("NAME")) {
            sortOrder = DataContract.DataEntry.COLUMN_DISPLAY_NAME_PRIMARY + " ASC";
        } else if (sorting.equals("LAST")) {
            sortOrder = DataContract.DataEntry.COLUMN_LAST_TIME_CONTACTED + " DESC";
        } else sortOrder = DataContract.DataEntry.COLUMN_TIMES_CONTACTED + " DESC";

        // sceglie il tipo di ordinamento effettua la query, setta l'header con il numero di elementi

        c = new CursorLoader(getActivity(),
                DataContract.DataEntry.CONTENT_URI.buildUpon().appendPath(sorting).appendPath(pos).build(),
                null,
                null,
                null,
                sortOrder + " LIMIT 1"                                              // orderBy
        );

        return c;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // create detail data
        if (data != null) {
            data.moveToFirst();
            stringData = Helper.setStringData(data);
        } else {
            stringData = null;
        }


        if (stringData != null) {
            // creating detailView not displaying nor include in the message empty field

            TextView tv1 = (TextView) parent.findViewById(R.id.tv1);    // Name
            tv1.setText(stringData[0]);

            TextView tv2 = (TextView) parent.findViewById(R.id.tv2);    // Phone number 1
            tv2.setText(stringData[1]);

            TextView tv3 = (TextView) parent.findViewById(R.id.tv3);    // Phone number 2
            if (stringData[2] == null) {
                rl3.setVisibility(View.GONE);
                rl3.setSelected(false);
            } else
                tv3.setText(stringData[2]);

            TextView tv4 = (TextView) parent.findViewById(R.id.tv4);    // Phone number 3
            if (stringData[3] == null) {
                rl4.setVisibility(View.GONE);
                rl4.setSelected(false);
            } else
                tv4.setText(stringData[3]);

            TextView tv5 = (TextView) parent.findViewById(R.id.tv5);    // Phone number 4
            if (stringData[4] == null) {
                rl5.setVisibility(View.GONE);
                rl5.setSelected(false);
            } else
                tv5.setText(stringData[4]);

            TextView tv6 = (TextView) parent.findViewById(R.id.tv6);    // Email
            if (stringData[5] == null) {
                rl6.setVisibility(View.GONE);
                rl6.setSelected(false);
            } else
                tv6.setText(stringData[5]);

            TextView tv7 = (TextView) parent.findViewById(R.id.tv7);    // Address
            if (stringData[6] == null) {
                rl7.setVisibility(View.GONE);
                rl7.setSelected(false);
            } else
                tv7.setText(stringData[6]);

            TextView tv8 = (TextView) parent.findViewById(R.id.tv8);    // Last call
            if (stringData[7] == null) {
                rl8.setVisibility(View.GONE);
                rl8.setSelected(false);
            } else
                tv8.setText(stringData[7]);

            TextView tv9 = (TextView) parent.findViewById(R.id.tv9);    // Times
            if (stringData[8] == null) {
                rl9.setVisibility(View.GONE);
                rl9.setSelected(false);
            } else
                tv9.setText(stringData[8]);

            prepareMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /*
     * select or deselect the view and update message to share
     */
    @Override
    public void onClick(View v) {
        if (v.isSelected()) {
            v.setSelected(false);
        } else {
            v.setSelected(true);
        }
        prepareMessage();
    }


    /*
     * save fragment status on orientation changes
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean[] selected = {rl1.isSelected(),
                rl2.isSelected(),
                rl3.isSelected(),
                rl4.isSelected(),
                rl5.isSelected(),
                rl6.isSelected(),
                rl7.isSelected(),
                rl8.isSelected(),
                rl9.isSelected()};
        outState.putBooleanArray("selected", selected);
    }

}
