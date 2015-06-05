package com.indio.android.zapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indio.android.zapp.data.DataContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Marco Gullo on 10/05/2015.
 */
public class DataAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private String mode;
    private String formattedDate;

    public DataAdapter(Context context, Cursor c, int flags, String mode) {
        super(context, c, flags);
        this.mode = mode;
        inflater = (LayoutInflater) context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);

        Calendar calendar = Calendar.getInstance();
        long lastUpdate = (calendar.getTime()).getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        formattedDate = df.format(lastUpdate);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View myView = inflater.inflate(R.layout.main_list_item, parent, false);
        ViewHolder myViewHolder = new ViewHolder(myView);
        myView.setTag(myViewHolder);
        return myView;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String text4 = "";

        ViewHolder myViewHolder = (ViewHolder) view.getTag();

        TextView myTextView1 = myViewHolder.text1;
        TextView myTextView2 = myViewHolder.text2;
        TextView myTextView3 = myViewHolder.text3;
        TextView myTextView4 = myViewHolder.text4;

        String text1 = (cursor.getPosition() + 1 + "");
        String text2 = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_DISPLAY_NAME_PRIMARY));
        String text3 = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NUMBER1));
        if (mode.equals("LAST")) {
            text4 = Helper.milliToDate(cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_LAST_TIME_CONTACTED)));
            if (text4 != null) {
                String temp = text4.substring(0, 10);   // friendly date just if "date equals current day"
                if (temp.equals(formattedDate)) {
                    text4 = mContext.getString(R.string.today) + " - " + text4.substring(13);
                }
            }
        } else if (mode.equals("MOST")) {
            text4 = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_TIMES_CONTACTED));
        }


        myTextView1.setText(text1);
        myTextView2.setText(text2);
        myTextView3.setText(text3);
        myTextView4.setText(text4);

        myTextView2.setTypeface(null, Typeface.BOLD);
    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView text1;
        public final TextView text2;
        public final TextView text3;
        public final TextView text4;

        public ViewHolder(View view) {
            text1 = (TextView) view.findViewById(R.id.text1);
            text2 = (TextView) view.findViewById(R.id.text2);
            text3 = (TextView) view.findViewById(R.id.text3);
            text4 = (TextView) view.findViewById(R.id.text4);
        }
    }

}