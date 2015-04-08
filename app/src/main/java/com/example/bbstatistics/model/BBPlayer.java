package com.example.bbstatistics.model;

import android.content.Context;

import com.example.bbstatistics.R;

/**
 * Data model of Basketball player
 */
public class BBPlayer {
    private static Context mAppContext;
    private static String[] mColNames;

    /**
     * Gets list of available columns.
     *
     * @return Array of strings of column names
     */
    public static String[] getColumnNames() {
        return mColNames;
    }

    public static int getColumnCount() {
        return mColNames.length;
    }

    public static void setContext(Context applicationContext) {
        mAppContext = applicationContext;
        mColNames = mAppContext.getResources().getStringArray(R.array.bb_column_names);
    }
}
