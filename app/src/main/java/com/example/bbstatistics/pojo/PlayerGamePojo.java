package com.example.bbstatistics.pojo;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;

/**
 * Created by zdenko on 2015-04-05.
 */
public class PlayerGamePojo {
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

    public PlayerGamePojo(long id, int number, String name) {
        playerId = id;
        playerNumber = number;
        playerName = name;
        onCourt = false;
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

    public boolean isOnCourt() {
        return onCourt;
    }

    public void setOnCourt(boolean onCourt) {
        this.onCourt = onCourt;
    }
}
