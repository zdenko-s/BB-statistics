package com.example.bbstatistics.com.example.bbstatistics.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bbstatistics.Consts;


public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bbstat.db";
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

    /**
     * Get list of teams
     *
     * @return
     */
    public Cursor getListOfTeams() {
        Log.d(Consts.TAG, "DbHelper#getListOfTeams()");
        Cursor cursor = mDb.query(Team.TEAM_TABLE, Team.COLUMNS, null, null, null, null, Team.COL_NAME);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getPlayersOfTeam(int teamId) {
        Cursor cursor = mDb.query(Player.TABLE_NAME, Player.COLUMNS,
                Player.COL_TEAM_ID + "=?",
                new String[]{"" + teamId},
                null, null,
                Player.COL_NUMBER);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public void addTeam(String name) {
        Log.d(Consts.TAG, "Inserting new team '" + name + "'");
        ContentValues values = new ContentValues();
        values.put(Team.COL_NAME, name);
        mDb.insert(Team.TEAM_TABLE, null, values);
    }

    public void addPlayer(int teamId, int playerNum, String name) {
        Log.d(Consts.TAG, "Inserting new player '" + name + "', #:" + playerNum + ", teamId:" + teamId);
        ContentValues values = new ContentValues();
        values.put(Player.COL_TEAM_ID, teamId);
        values.put(Player.COL_NUMBER, playerNum);
        values.put(Player.COL_NAME, name);
        mDb.insert(Player.TABLE_NAME, null, values);
    }

    /**
     * Create tables if do not exist
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Consts.TAG, Team.SQL_CREATE_TABLE);
        db.execSQL(Team.SQL_CREATE_TABLE);
        Log.d(Consts.TAG, Player.SQL_CREATE_TABLE);
        db.execSQL(Player.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Metadata related to DB table team
     */
    public final static class Team {
        public static final String TEAM_TABLE = "team";
        public static final String COL_ID = "_id";
        public static final String COL_NAME = "team_name";
        public static final String[] COLUMNS = {COL_ID, COL_NAME};
        static final String SQL_CREATE_TABLE = "create table if not exists "
                + TEAM_TABLE + "(" + COL_ID + " integer primary key autoincrement,"
                + COL_NAME + " text not null unique);";

    }

    /**
     * Metadata related to db table player
     */
    public final static class Player {
        public static final String TABLE_NAME = "player";
        public static final String COL_ID = "_id";
        public static final String COL_TEAM_ID = "team_id";
        public static final String COL_NUMBER = "player_number";
        public static final String COL_NAME = "player_name";
        public static final String[] COLUMNS = {COL_ID, COL_TEAM_ID, COL_NUMBER, COL_NAME};
        //public static final String[] ORDER_BY_NUMBER = {COL_NUMBER};
        //CREATE TABLE player (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, team_id INTEGER NOT NULL, player_name VARCHAR (20) NOT NULL);
        static final String SQL_CREATE_TABLE = "create table if not exists "
                + TABLE_NAME + "(" + COL_ID + " integer primary key autoincrement,"
                + COL_TEAM_ID + " integer not null,"
                + COL_NUMBER + " integer not null,"
                + COL_NAME + " text not null);";
    }
}
