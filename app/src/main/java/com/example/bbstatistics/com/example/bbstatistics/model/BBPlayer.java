package com.example.bbstatistics.com.example.bbstatistics.model;

import android.content.Context;

import com.example.bbstatistics.R;

/**
 * Data model of Basketball player
 */
public class BBPlayer {
    private final Context mContext;
    private final String[] mColNames;

    /**
     * Creates instance of BBPlayer model
     *
     * @param applicationContext Activity which created BBPlayer
     */
    public BBPlayer(Context applicationContext) {
        mContext = applicationContext;
        mColNames = mContext.getResources().getStringArray(R.array.bb_column_names);
    }

    /**
     * Gets list of available columns.
     *
     * @return Array of strings of column names
     */
    public String[] getColumnNames() {
        return mColNames;
    }

    public int getColumnCount() {
        return mColNames.length;
    }
}
