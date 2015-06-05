package com.indio.android.zapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Marco Gullo on 13/05/2015.
 */
public class DetailActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            DetailFragment myFragment = new DetailFragment();
            myFragment.setArguments(bundle);
            fm.beginTransaction().add(R.id.detail_container, myFragment).commit();
        }
    }

}
