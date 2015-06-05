package com.indio.android.zapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

/**
 * Created by Marco Gullo on 12/05/2015.
 */
public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tv2 = (TextView) findViewById(R.id.info_012);
        tv2.setText(R.string.info2);

        TextView tv3 = (TextView) findViewById(R.id.info_014);
        tv3.setText(R.string.info4);

        TextView tv4 = (TextView) findViewById(R.id.info_016);
        Bundle bundle = getIntent().getExtras();
        String formattedDate = bundle.getString("LAST_UPDATE");
        int contactsCount = bundle.getInt("COUNT");
        tv4.setText("last update on: " + formattedDate + "/" + contactsCount + " contacts");

    }
}
