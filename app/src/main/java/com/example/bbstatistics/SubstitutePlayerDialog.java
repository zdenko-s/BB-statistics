package com.example.bbstatistics;

import android.app.Dialog;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;
import com.example.bbstatistics.pojo.PlayerGamePojo;

import java.util.ArrayList;

/**
 * Created by Zdenko on 2015-04-05.
 */
public class SubstitutePlayerDialog extends Dialog {
    private final Statistic parentActivity;
    private final PlayerGamePojo[] mPlayersPojoRoRef;
    //private final ArrayList<Long> mPlayersOnCourt;

    private static final int INITIAL_CURSOR_SIZE = 5;
    // When dialog dismissed, ListView selected items collected from here
    private Cursor mOnCourtCursor;
    private Cursor mOnBenchCursor;
    private ListView mLvCourt;
    private ListView mLvBench;

    /**
     *
     * @param statisticActivity ParentActivity used to register callbacks
     * @param players In memory cache of data. Parent view is responsible for persistence
     */
    public SubstitutePlayerDialog(Statistic statisticActivity, PlayerGamePojo[] players) {
        super(statisticActivity);
        parentActivity = statisticActivity;
        mPlayersPojoRoRef = players;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_substitute);
        // Button click handler is parent activity. Dialog data released after Dialog#dismiss().
        // If dialog dismisses itself, caller does not have values of Dialog' state
        Button btnOk = (Button) findViewById(R.id.btnOK);
        btnOk.setOnClickListener(parentActivity);
        Button btnDismiss = (Button) findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(parentActivity);
        // Setup ListViews
        mLvCourt = (ListView) findViewById(R.id.lvPlayersOnCourt);
        mLvCourt.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mLvBench = (ListView) findViewById(R.id.lvPlayersOnBench);
        mLvBench.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // Create two cursors, one for each ListView
        mOnCourtCursor = getPlayersCursor(true);
        mOnBenchCursor = getPlayersCursor(false);
        // Create list adapters
        int[] bindTo = new int[]{android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter onCourtDataAdapter = new SimpleCursorAdapter(parentActivity,
                android.R.layout.simple_list_item_activated_2, mOnCourtCursor, PlayerGamePojo.BIND_CURSOR_COLUMNS, bindTo, 0);
        mLvCourt.setAdapter(onCourtDataAdapter);
        SimpleCursorAdapter onBenchDataAdapter = new SimpleCursorAdapter(parentActivity,
                android.R.layout.simple_list_item_activated_2, mOnBenchCursor, PlayerGamePojo.BIND_CURSOR_COLUMNS, bindTo, 0);
        mLvBench.setAdapter(onBenchDataAdapter);
    }

    /**
     * Creates MatrixCursor used by ListAdapter. First PoJo item may be skipped if it is opponent
     * @param onCourt which players to add to cursor. Court or bench. If true, court players will be added
     * @return Cursor filled with data of players which are on court/bench
     */
    Cursor getPlayersCursor(boolean onCourt) {
        MatrixCursor cursor = new MatrixCursor(PlayerGamePojo.FIELD_NAMES, INITIAL_CURSOR_SIZE);
        for (int i = Settings.OpponentRowNum; i < mPlayersPojoRoRef.length; i++) {
            PlayerGamePojo p = mPlayersPojoRoRef[i];
            String logLine = "getPlayersCursor. Player: _id:" + p.getPlayerId() + ",#:" + p.getPlayerNumber() + ", name:" + p.getPlayerName();
            if(onCourt) {
                // Is player _id present in mPlayersOnCourt vector?
                if(p.isOnCourt()) {
                    cursor.newRow().add(p.getPlayerId()).add(p.getPlayerNumber()).add(p.getPlayerName());
                    //Log.v(Consts.TAG, logLine + " onCourt");
                }
            }
            else {
                // Is player _id is not present in mPlayersOnCourt vector?
                if(!p.isOnCourt()) {
                    cursor.newRow().add(p.getPlayerId()).add(p.getPlayerNumber()).add(p.getPlayerName());
                    //Log.v(Consts.TAG, logLine + " onBench");
                }
            }
        }
        return cursor;
    }

    /**
     * Based on ListView selection substitutes player. Selected player in ListView mLvCourt is put on bench,
     * selected player in ListView mLvBench is put on court,
     */
    public void substitute() {
        // Get selected players from ListView 'BENCH'. They will go to court.
        SparseBooleanArray toCourt = mLvBench.getCheckedItemPositions();
        for(int i = 0; i < toCourt.size(); i++) {
            // If list item is selected, then unselected, it will be in SparseArray with correct selected value: FALSE
            if(toCourt.get(toCourt.keyAt(i), false)) {
                mOnBenchCursor.moveToPosition(toCourt.keyAt(i));
                long id = mOnBenchCursor.getLong(mOnBenchCursor.getColumnIndex(DbHelper.PlayerGame.COL_ID));
                PlayerGamePojo p = PlayerGamePojo.findById(mPlayersPojoRoRef, id);
                if(p != null) {
                    p.setOnCourt(true);
                    Log.v(Consts.TAG, "To court goes:" + p.getPlayerNumber());
                }
                else
                    Log.e(Consts.TAG, "_id not found! " + id);
            }
        }
        // Get selected players from ListView 'COURT'. They will go to bench.
        SparseBooleanArray toBench = mLvCourt.getCheckedItemPositions();
        for(int i = 0; i < toBench.size(); i++) {
            if(toBench.get(toBench.keyAt(i), false)) {
                mOnCourtCursor.moveToPosition(toBench.keyAt(i));
                final long goesToBenchId = mOnCourtCursor.getLong(mOnCourtCursor.getColumnIndex(DbHelper.Player.COL_ID));
                PlayerGamePojo p = PlayerGamePojo.findById(mPlayersPojoRoRef, goesToBenchId);
                if(p != null) {
                    p.setOnCourt(false);
                    Log.v(Consts.TAG, "To bench goes:" + p.getPlayerNumber());
                }
                else
                    Log.e(Consts.TAG, "goesToBenchId not found! " + goesToBenchId);
            }
        }

    }

    /**
     * Helper method which checks does array contains element
     * @param arr Array to check
     * @param val Value to search for
     * @return true is value is present
     */
    private boolean contains(long[] arr, long val) {
        if(arr == null || arr.length == 0)
            return false;
        int length = arr.length;
        for(int i = 0; i < length; i++) {
            if(arr[i] == val)
                return true;
        }
        return false;
    }
}
