package com.example.bbstatistics.pojo;

import com.example.bbstatistics.model.DbHelper;

/**
 * Created by Zdenko on 2015-04-05.
 */
public class PlayerGamePojo {
    //
    public final static String[] FIELD_NAMES = {DbHelper.Player.COL_ID, DbHelper.Player.COL_NUMBER, DbHelper.Player.COL_NAME};
    public static final String BIND_CURSOR_COLUMNS[] = {FIELD_NAMES[1], FIELD_NAMES[2]};
    private boolean mDirty = false;
    private boolean mPlaying = false;
    private long playerId;  // DB unique ID
    private int playerNumber;   // Number on shirt
    private int mPlayingTime;   // seconds
    private String playerName;
    /*    public enum FieldName {
            POINT1(0),ATTEMPT1(1),POINT2(2),ATTEMPT2(3),POINT3(4),ATTEMPT3(5),DEF_REBOUND(6),OFF_REBOUND(7)
            ,TURN_OVER(8), STEAL(9), FOUL(10),FOUL_GIVEN(11);
            private int colIndex;
            private FieldName(int val) {
                colIndex = val;
            }
            int getColIndex() {
                return colIndex;
            }
        }*/
    private boolean onCourt;
    // For faster access, fields are stored as array. This data is displayed in grid
    private byte[] rowData = new byte[DbColumnName.values().length];

    public PlayerGamePojo(long id, int number, String name) {
        playerId = id;
        playerNumber = number;
        playerName = name;
        onCourt = false;
    }

    /**
     * Find item which ID is searched for
     *
     * @param array Where to search
     * @param id    What to find
     * @return Index of item, or -1 if not found
     */
    public static PlayerGamePojo findById(PlayerGamePojo[] array, long id) {
        if (array == null)
            return null;
        for (int i = 0; i < array.length; i++) {
            if (array[i].getPlayerId() == id)
                return array[i];
        }
        return null;
    }

    public boolean isDirty() {
        return mDirty;
    }

    public void clearDirty() {
        this.mDirty = false;
    }

    /**
     * Creates XML and appends it to buffer.
     *
     * @param sb Buffer to append created XML.
     */
    public void addXml(StringBuilder sb) {

    }

    /**
     * Append CVS formatted line of text to buffer.
     *
     * @param sb Buffer to append data.
     */
    public void addCvs(StringBuilder sb) {
        sb.append(playerId).append(',');
        sb.append(playerNumber).append(',');
        sb.append(playerName);
        for (PlayerGamePojo.DbColumnName dbColumn : PlayerGamePojo.DbColumnName.values()) {
            sb.append(',').append(getFieldValue(dbColumn));
        }
        sb.append("\n");
    }

    /**
     * A
     * //     * @param fieldName
     *
     * @return
     */
//    public byte getFieldValue(FieldName fieldName) {
//        return rowData[fieldName.getColIndex()];
//    }
//    public void setFieldValue(FieldName fieldName, byte value) {
//        rowData[fieldName.getColIndex()] = value;
//    }
    public byte getFieldValue(DbColumnName colName) {
        return rowData[colName.ordinal()];
    }

    public void setFieldValue(DbColumnName colName, byte value) {
        rowData[colName.ordinal()] = value;
    }

    public byte getFieldValue(int index) {
        return rowData[index];
    }

    public void setFieldValue(int index, byte value) {
        rowData[index] = value;
    }

    /**
     * Shortcut method to increment field value. No need to call getter than setter
     *
     * @param index
     * @param increment
     */
    public void addToField(int index, int increment) {
        mDirty = true;
        rowData[index] += increment;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isOnCourt() {
        return onCourt;
    }

    public void setOnCourt(boolean onCourt) {
        this.onCourt = onCourt;
    }

    /**
     * Gets does player plays, Playing time is increasing when game clock is not stopped.
     * Some users prefer to display all players on screen and mark it as 'playing' by touching name cell
     *
     * @return true if playing
     */
    public boolean isPlaying() {
        return mPlaying;
    }

    public void setPlaying(boolean mPlaying) {
        this.mPlaying = mPlaying;
    }

    public int getPlayingTime() {
        return mPlayingTime;
    }

    public void setPlayingTime(int mPlayingTime) {
        this.mPlayingTime = mPlayingTime;
    }

    public enum DbColumnName {
        shot1pt, attempt1pt, shot2pt, attempt2pt, shot3pt, attempt3pt, def_rebound, off_rebound, turn_over, steal, foul, foul_given, assist, loose_ball
    }
}
