package com.example.bbstatistics.pojo;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;

/**
 * Created by zdenko on 2015-04-05.
 */
public class PlayerGamePojo {
    private long playerId;
    private int playerNumber;
    private String playerName;

    public PlayerGamePojo(long id, int number, String name) {
        playerId = id;
        playerNumber = number;
        playerName = name;
    }
    //
    public final static String[] FIELD_NAMES = {DbHelper.Player.COL_ID, DbHelper.Player.COL_NUMBER, DbHelper.Player.COL_NAME};
    public static final String BIND_CURSOR_COLUMNS[] = {FIELD_NAMES[1], FIELD_NAMES[2]};


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
}
