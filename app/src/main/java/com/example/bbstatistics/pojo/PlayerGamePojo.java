package com.example.bbstatistics.pojo;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;

/**
 * Created by zdenko on 2015-04-05.
 */
public class PlayerGamePojo {
    public enum DbColumnName {
        shot1pt, attempt1pt, shot2pt, attempt2pt,shot3pt, attempt3pt, def_rebound, off_rebound
        , turn_over, steal, foul, foul_given, assist
    }
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
    /**
     * Find item which ID is searched for
     * @param array Where to search
     * @param id What to find
     * @return Index of item, or -1 if not found
     */
    public static PlayerGamePojo findById(PlayerGamePojo[] array, long id) {
        if(array == null)
            return null;
        for(int i = 0; i < array.length; i++) {
            if(array[i].getPlayerId() == id)
                return  array[i];
        }
        return null;
    }

    private long playerId;
    private int playerNumber;
    private String playerName;
    private boolean onCourt;
    // For faster access, fields are stored as array. This data is displayed in grid
    private byte[] rowData = new byte[DbColumnName.values().length];

    public PlayerGamePojo(long id, int number, String name) {
        playerId = id;
        playerNumber = number;
        playerName = name;
        onCourt = false;
    }
    //
    public final static String[] FIELD_NAMES = {DbHelper.Player.COL_ID, DbHelper.Player.COL_NUMBER, DbHelper.Player.COL_NAME};
    public static final String BIND_CURSOR_COLUMNS[] = {FIELD_NAMES[1], FIELD_NAMES[2]};

    /**
     * A
//     * @param fieldName
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
     * @param index
     * @param increment
     */
    public void addToField(int index, int increment) {
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
}
