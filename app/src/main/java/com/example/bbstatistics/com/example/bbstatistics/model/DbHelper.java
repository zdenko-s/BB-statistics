package com.example.bbstatistics.com.example.bbstatistics.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bbstatistics.Consts;
import com.example.bbstatistics.NewGame;

import java.util.Date;


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
        Log.d(Consts.TAG, "DbHelper#open()");
        mDb = super.getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        Log.d(Consts.TAG, "DbHelper#close()");
        super.close();
    }
    /*
    public SQLiteDatabase getDb() {
        return mDb;
    }
*/

    /**
     * Add team to DB
     * @param name Name of team
     * @return _id of newly inserted team.
     */
    public long addTeam(String name) {
        Log.d(Consts.TAG, "Inserting new team '" + name + "'");
        ContentValues values = new ContentValues();
        values.put(Team.COL_NAME, name);
        long id = mDb.insert(Team.TEAM_TABLE, null, values);
        return id;
    }
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
    /**
     * Add player to team
     * @param teamId _id of team to which player will be added
     * @param playerNum Player number (Dress)
     * @param name Name of the player
     * @return _id of player. Auto generated
     */
    public long addPlayer(int teamId, int playerNum, String name) {
        Log.d(Consts.TAG, "Inserting new player '" + name + "', #:" + playerNum + ", teamId:" + teamId);
        ContentValues values = new ContentValues();
        values.put(Player.COL_TEAM_ID, teamId);
        values.put(Player.COL_NUMBER, playerNum);
        values.put(Player.COL_NAME, name);
        long id = mDb.insert(Player.TABLE_NAME, null, values);
        return id;
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


    public long addGame(int teamId, int opponentTeamId, String dateOfGame, String description) {
        ContentValues values = new ContentValues(Game.COLUMNS.length - 1);
        values.put(Game.COL_TEAM_ID, teamId);
        values.put(Game.COL_OPPONENT_TEAM_ID, opponentTeamId);
        values.put(Game.COL_DATE_TIME, dateOfGame);
        values.put(Game.COL_DESCRIPTION, description);
        long _id = mDb.insert(Game.TABLE_NAME, null, values);
        return _id;
    }

    public Cursor getGames() {
        Cursor cursor = mDb.query(Game.TABLE_NAME, Game.COLUMNS,
                null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
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
        Log.d(Consts.TAG, Game.SQL_CREATE_TABLE);
        db.execSQL(Game.SQL_CREATE_TABLE);
        Log.d(Consts.TAG, PlayerGame.SQL_CREATE_TABLE);
        db.execSQL(PlayerGame.SQL_CREATE_TABLE);
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
        // CREATE TABLE team (_id INTEGER PRIMARY KEY AUTOINCREMENT, team_name TEXT NOT NULL UNIQUE);
        static final String SQL_CREATE_TABLE = "create table if not exists "
                + TEAM_TABLE + "(" + COL_ID + " integer primary key autoincrement not null,"
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
        //CREATE TABLE player (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, team_id INTEGER NOT NULL,
        // player_number INTEGER NOT NULL, player_name VARCHAR (20) NOT NULL);
        static final String SQL_CREATE_TABLE = "create table if not exists "
                + TABLE_NAME + "(" + COL_ID + " integer primary key autoincrement not null,"
                + COL_TEAM_ID + " integer not null,"
                + COL_NUMBER + " integer not null,"
                + COL_NAME + " text not null);";
    }

    public final static class Game {
        public static final String TABLE_NAME = "game";
        public static final String COL_ID = "_id";
        public static final String COL_TEAM_ID = "team_id";
        public static final String COL_OPPONENT_TEAM_ID = "opponent_tem_id";
        public static final String COL_DATE_TIME = "date_time";
        public static final String COL_DESCRIPTION = "description";
        public static final String[] COLUMNS = {COL_ID, COL_TEAM_ID, COL_OPPONENT_TEAM_ID, COL_DATE_TIME, COL_DESCRIPTION};
        // CREATE TABLE game (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, team_id INTEGER NOT NULL,
        // opponent_tem_id INTEGER NOT NULL, date_time DATETIME NOT NULL, description TEXT);
        static final String SQL_CREATE_TABLE = "create table if not exists "
                + TABLE_NAME + "(" + COL_ID + " integer primary key autoincrement not null,"
                + COL_TEAM_ID + " integer not null,"
                + COL_OPPONENT_TEAM_ID + " integer not null,"
                + COL_DATE_TIME + " datetime not null,"
                + COL_DESCRIPTION + " text not null);";

        public void add(int teamId, int opponentTeamId, String dateTime) {

        }
    }

    public final static class PlayerGame {
        public static final String TABLE_NAME = "player_game";
        public static final String COL_ID = "_id";
        public static final String COL_GAME_ID = "team_id";
        public static final String COL_PLAYER_ID = "player_number";
        public static final String[] COLUMNS = {COL_ID, COL_GAME_ID, COL_PLAYER_ID};
        //CREATE TABLE IF NOT EXISTS player_game (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, game_id INTEGER NOT NULL REFERENCES game (_id)
        // , player_id INTEGER REFERENCES player (_id) NOT NULL, CONSTRAINT unq_game_id_player_id UNIQUE (game_id, player_id));
        static final String SQL_CREATE_TABLE = "create table if not exists "
                + TABLE_NAME + "(" + COL_ID + " integer primary key autoincrement not null,"
                + COL_GAME_ID + " integer not null references " + Game.TABLE_NAME + "(" + Game.COL_ID + "),"
                + COL_PLAYER_ID + " integer not null references " + Player.TABLE_NAME + "(" + Player.COL_ID + "),"
                + "CONSTRAINT unq_game_id_player_id UNIQUE (" + COL_GAME_ID + ", " + COL_PLAYER_ID + "));";
    }
}
