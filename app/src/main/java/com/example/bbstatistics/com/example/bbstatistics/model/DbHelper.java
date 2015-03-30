package com.example.bbstatistics.com.example.bbstatistics.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bbstatistics.Consts;


public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bbstat.sqlite";
    private static final int DATABASE_VERSION = 1;
    // data
    Context mContext;
    private SQLiteDatabase mDb;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Open SQLite database
     */
    public void open() {
        mDb = super.getWritableDatabase();
    }
/*
    public SQLiteDatabase getDb() {
        return mDb;
    }
*/
    public Cursor getListOfTeams() {
        Cursor cursor = mDb.query(Teams.TEAM_TABLE, Teams.COLUMNS, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public void addTeam(String name) {
        Log.d(Consts.TAG, "Inserting new team '" + name + "'");
        ContentValues values = new ContentValues();
        values.put(Teams.COL_NAME, name);
        mDb.insert(Teams.TEAM_TABLE, null, values);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Consts.TAG, Teams.SQL_CREATE_TABLE);
        db.execSQL(Teams.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public final static class Teams {
        public static final String TEAM_TABLE = "team";
        public static final String COL_ID = "_id";
        public static final String COL_NAME = "team_name";
        public static final String[] COLUMNS = {COL_ID, COL_NAME};
        static final String SQL_CREATE_TABLE = "create table if not exists "
                + TEAM_TABLE + "(" + COL_ID + " integer primary key autoincrement,"
                + COL_NAME + " text not null unique);";

    }
}
