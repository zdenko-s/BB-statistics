package com.example.bbstatistics.com.example.bbstatistics.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bbstatistics.Consts;
import com.example.bbstatistics.pojo.PlayerGamePojo;


public class DbHelper extends SQLiteOpenHelper {
    public static final long INVALID_ID = -1;
    public static final String DATABASE_NAME = "bbstat.db";
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
     *
     * @param name Name of team
     * @return _id of newly inserted team.
     */
    public long addTeam(String name) {
        Log.d(Consts.TAG, "Inserting new team '" + name + "'");
        ContentValues values = new ContentValues();
        values.put(Team.COL_NAME, name);
        return mDb.insert(Team.TEAM_TABLE, null, values);
    }

    /**
     * Get list of teams
     *
     * @return Cursor that contains all teams
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
     *
     * @param teamId    _id of team to which player will be added
     * @param playerNum Player number (Dress)
     * @param name      Name of the player
     * @return _id of player. Auto generated
     */
    public long addPlayer(int teamId, int playerNum, String name) {
        Log.d(Consts.TAG, "Inserting new player '" + name + "', #:" + playerNum + ", teamId:" + teamId);
        ContentValues values = new ContentValues();
        values.put(Player.COL_TEAM_ID, teamId);
        values.put(Player.COL_NUMBER, playerNum);
        values.put(Player.COL_NAME, name);
        return mDb.insert(Player.TABLE_NAME, null, values);
    }

    /**
     * Get all players of team
     *
     * @param teamId
     * @return
     */
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

    /**
     * Adds game
     *
     * @param teamId
     * @param opponentTeamId
     * @param dateOfGame
     * @param description
     * @return
     */
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
        Log.v(Consts.TAG, "DbHelper#getGames()");
        Cursor cursor = mDb.query(Game.VGAME_NAME, Game.VGAME_COLUMNS,
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
        Log.d(Consts.TAG, Player.SQL_CREATE_TABLE);
        Log.d(Consts.TAG, Game.SQL_CREATE_TABLE);
        Log.d(Consts.TAG, Game.SQL_CREATE_VIEW_GAMES);
        Log.d(Consts.TAG, PlayerGame.SQL_CREATE_TABLE);
        Log.d(Consts.TAG, PlayerGame.SQL_CREATE_VIEW_PG);

        db.execSQL(Team.SQL_CREATE_TABLE);
        db.execSQL(Player.SQL_CREATE_TABLE);
        db.execSQL(Game.SQL_CREATE_TABLE);
        db.execSQL(Game.SQL_CREATE_VIEW_GAMES);
        db.execSQL(PlayerGame.SQL_CREATE_TABLE);
        db.execSQL(PlayerGame.SQL_CREATE_VIEW_PG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add players of game to linking table Player -> Player_Game <- Game
    public void addPlayersToGame(long gameId, Long[] playersAtGame) {
        //TODO: Insert one by one players into DB
        // Each player is single entry in linking table
        for (int i = 0; i < playersAtGame.length; i++) {
            ContentValues values = new ContentValues();
            values.put(PlayerGame.COL_GAME_ID, gameId);
            values.put(PlayerGame.COL_PLAYER_ID, playersAtGame[i]);
            mDb.insert(PlayerGame.TABLE_NAME, null, values);
        }
    }

    /**
     * Load data from DB and cache it as POJO objects.
     *
     * @param gameId
     * @return
     */
    public PlayerGamePojo[] loadPlayersOfGame(long gameId) {
        // DB view is sorted by player number
        Cursor cursor = mDb.query(PlayerGame.V_PLAYER_GAME, PlayerGame.V_COLUMNS,
                PlayerGame.COL_GAME_ID + "=?",
                new String[]{"" + gameId},
                null, null, null);
        if (cursor != null) {
            // Size of array is equal to size of cursor
            PlayerGamePojo[] ret = new PlayerGamePojo[cursor.getCount()];
            int idx = 0;
            // Load data from cursor record by record
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PlayerGamePojo pojo = new PlayerGamePojo(
                        cursor.getLong(cursor.getColumnIndex(PlayerGame.COL_PLAYER_ID)),
                        cursor.getInt(cursor.getColumnIndex(Player.COL_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(Player.COL_NAME))
                );
                ret[idx++] = pojo;
                cursor.moveToNext();
            }
            return ret;
        } else
            return null;
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
                + COL_TEAM_ID + " integer not null references " + Team.TEAM_TABLE + "(" + Team.COL_ID + "),"
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

        public static final String VGAME_NAME = "v_game";
        public static final String VCOL_OPP_TEAM_NAME = "opp_team_name";
        public static final String[] VGAME_COLUMNS = {COL_ID, COL_DATE_TIME, Team.COL_NAME, VCOL_OPP_TEAM_NAME, COL_DESCRIPTION};
        //public static final String[] VGAME_COLUMNS_ = {COL_DATE_TIME, Team.COL_NAME, VCOL_OPP_TEAM_NAME, COL_DESCRIPTION};
/*
        CREATE VIEW v_game AS
        SELECT g._id,g.date_time,t.team_name,oppt.team_name AS opp_team_name,g.description FROM game g
        INNER JOIN team AS t ON g.team_id = t._id
        INNER JOIN team AS oppt ON g.opponent_tem_id = oppt._id
        ORDER BY date_time DESC;
*/
        static final String SQL_CREATE_VIEW_GAMES = "CREATE VIEW IF NOT EXISTS " + VGAME_NAME + " AS \nSELECT g." +
                COL_ID + ",g." + COL_DATE_TIME + ",t." + Team.COL_NAME + ", oppt." + Team.COL_NAME + " AS " +
                VCOL_OPP_TEAM_NAME + "," + "g." + COL_DESCRIPTION + "  FROM " + TABLE_NAME + " AS g \n" +
                "INNER JOIN " + Team.TEAM_TABLE + " AS t ON g." + COL_TEAM_ID + " = t." + Team.COL_ID + " \n" +
                "INNER JOIN " + Team.TEAM_TABLE + " AS oppt ON g." + COL_OPPONENT_TEAM_ID + " = oppt." +
                Team.COL_ID + "\n ORDER BY " + COL_DATE_TIME + " DESC;";

    }

    public final static class PlayerGame {
        public static final String TABLE_NAME = "player_game";
        public static final String V_PLAYER_GAME = "v_player_game";
        public static final String COL_ID = "_id";
        public static final String COL_GAME_ID = "game_id";
        public static final String COL_PLAYER_ID = "player_id";
        public static final String[] COLUMNS = {COL_ID, COL_GAME_ID, COL_PLAYER_ID};
        //CREATE TABLE IF NOT EXISTS player_game (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, game_id INTEGER NOT NULL REFERENCES game (_id)
        // , player_id INTEGER REFERENCES player (_id) NOT NULL, CONSTRAINT unq_game_id_player_id UNIQUE (game_id, player_id));
        static final String SQL_CREATE_TABLE = "create table if not exists "
                + TABLE_NAME + "(" + COL_ID + " integer primary key autoincrement not null,"
                + COL_GAME_ID + " integer not null references " + Game.TABLE_NAME + "(" + Game.COL_ID + "),"
                + COL_PLAYER_ID + " integer not null references " + Player.TABLE_NAME + "(" + Player.COL_ID + "),"
                + "CONSTRAINT unq_game_id_player_id UNIQUE (" + COL_GAME_ID + ", " + COL_PLAYER_ID + "));";
        // CREATE VIEW IF NOT EXISTS v_player_game AS SELECT pg._id,pg.game_id,pg.player_id,p.player_number,
        //p.player_name FROM player_game AS pg INNER JOIN player AS p ON pg.player_id = p._id
        //INNER JOIN team AS t ON p.team_id = t._id INNER JOIN game AS g ON pg.game_id = g._id
        // ORDER BY g.date_time DESC, p.player_number ASC;
        static final String SQL_CREATE_VIEW_PG = "CREATE VIEW IF NOT EXISTS " + V_PLAYER_GAME
                + " AS\n SELECT pg." + PlayerGame.COL_ID + ",pg." + PlayerGame.COL_GAME_ID + ",pg." + PlayerGame.COL_PLAYER_ID
                + ",p." + Player.COL_NUMBER + ",p." + Player.COL_NAME + " FROM " + PlayerGame.TABLE_NAME
                + " AS pg \n INNER JOIN " + Player.TABLE_NAME + " AS p ON pg." + PlayerGame.COL_PLAYER_ID
                + " = p." + Player.COL_ID + " \n INNER JOIN " + Team.TEAM_TABLE + " AS t ON p." + Player.COL_TEAM_ID
                + " = t." + Team.COL_ID + " \n INNER JOIN " + Game.TABLE_NAME + " AS g ON pg." + PlayerGame.COL_GAME_ID
                + " = g." + Game.COL_ID + " \n ORDER BY g." + Game.COL_DATE_TIME + " DESC, p." + Player.COL_NUMBER + " ASC;";
        public static final String[] V_COLUMNS = {COL_ID, COL_GAME_ID, COL_PLAYER_ID, Player.COL_NUMBER, Player.COL_NAME};
    }
}
