package com.example.bbstatistics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.bbstatistics.model.DbHelper;
import com.example.bbstatistics.pojo.PlayerGamePojo;


public class Statistic extends Activity implements View.OnClickListener {
    static final String TAG = "Statistic";
    // In memory cache of player data. Sorted by dress number. First row may contain opponent team data.
    // (Depending on user preferences)
    private PlayerGamePojo[] mPlayers;
    private long mGameId;
    private int mPeriod = 1;
    private DbHelper mDbHelper;
    private StatisticView mStatisticView;
    private SubstitutePlayerDialog mDlg;
    private boolean mSubstDialogDismissedByOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(Consts.TAG, "Statistic(Activity).onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        mStatisticView = (StatisticView) findViewById(R.id.statisticView);
        addListeners();
        getResources().getStringArray(R.array.bb_column_names);
        mDbHelper = new DbHelper(this);
        // Get starting intent
        Intent intent = getIntent();
        // Show game statistic
        mGameId = intent.getLongExtra(Consts.ACTIVITY_REQUEST_DATA_GAMEID_KEY, DbHelper.INVALID_ID);
    }

    private void addListeners() {
        // Add +/- button listener
        Button buttonPlusMinus = (Button) findViewById(R.id.btn_plus_minus);
        buttonPlusMinus.setOnClickListener(mStatisticView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(Consts.TAG, "Statistic(Activity#onStart()");
        mStatisticView.logPlayersOnCourt();
    }

    // Called when energy saving turns off display. State is preserved and no need to reload data.
    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
        mDbHelper.open();
        if (mPlayers == null || mPlayers.length == 0) {
            // Load players of the game from db
            mPlayers = mDbHelper.loadPlayersOfGame(mGameId);
            if (mPlayers != null) {
                // Pass Players of game to StatisticView (child view)
                mStatisticView.setSharedPlayersData(mPlayers);
            }
        } else {
            Log.v(TAG, "onResume() not reloading data.");
        }
    }

    /**
     * WHen back button pressed, prompt user to save data
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.save_changes_title))
                .setMessage(getString(R.string.save_changes_question))
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Statistic.super.onBackPressed();
                    }
                })
                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (save())
                            Statistic.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(Consts.TAG, "Statistic(Activity)#onRestoreInstanceState()");
        if (savedInstanceState != null) {
            Log.v(Consts.TAG, "Statistic(Activity)#onRestoreInstanceState(): saved state present");
            //mPlayersOnCourt = savedInstanceState.getLongArray(PersistenceKeys.PLAYERS_ON_COURT);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(Consts.TAG, "Statistic(Activity)#onSaveInstanceState()");
        mStatisticView.logPlayersOnCourt();
        // NOT called if the activity is closed by the user pressing the Back button or programmatically by calling finish().
        // called when the activity completes its active lifecycle, before it is killed
        //outState.putLongArray(PersistenceKeys.PLAYERS_ON_COURT, mPlayersOnCourt);
    }

    @Override
    protected void onPause() {
        Log.v(Consts.TAG, "Statistic(Activity)#onPause()");
        mStatisticView.logPlayersOnCourt();
        super.onPause();
        mDbHelper.close();
    }

    // http://developer.android.com/training/basics/activity-lifecycle/pausing.html
    @Override
    protected void onStop() {
        super.onStop();
        // you should not use onPause() to store user changes (such as personal information entered into a form) to permanent storage
        Log.v(Consts.TAG, "Statistic(Activity)#onStop()");
        mStatisticView.logPlayersOnCourt();
        // TODO: Persist data to DB
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_statistic_settings:
                Toast.makeText(this, "TODO: Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_statistic_copy:
                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //String xmlData = getGameStatisticXml();
                String cvsData = getGameStatisticCvs();
                ClipData clip = ClipData.newPlainText("BBStatistic of game:" + mGameId, cvsData);
                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates CSV presentation of every player data, including header
     *
     * @return CSV formatted string.
     */
    private String getGameStatisticCvs() {
        StringBuilder sb = new StringBuilder();
        // Add header row
        sb.append(DbHelper.Player.COL_ID).append(',');
        sb.append(DbHelper.Player.COL_NUMBER).append(',');
        sb.append(DbHelper.Player.COL_NAME);
        // Add data fields
        for (PlayerGamePojo.DbColumnName dbColumn : PlayerGamePojo.DbColumnName.values()) {
            sb.append(',').append(dbColumn.name());
        }
        // Add data
        for (PlayerGamePojo player : mPlayers) {
            player.addCvs(sb);
        }
        return sb.toString();
    }

    /**
     * Creates XML presentation of player's static data
     *
     * @return XML string presentation of data.
     */
    private String getGameStatisticXml() {
        StringBuilder sb = new StringBuilder();
        for (PlayerGamePojo player : mPlayers) {
            player.addXml(sb);
        }
        return null;
    }

    /**
     * Show dialog where used can choose players to substitute
     *
     * @param view
     */
    public void substitutePlayers(View view) {
        // If number of players marked as "on court" changes, recalculate height of row
        //int playersMarkedOnCourt =
        mDlg = new SubstitutePlayerDialog(this, mPlayers);
        mDlg.show();
        Log.v(Consts.TAG, "SubstituteDialog dismissed by " + (mSubstDialogDismissedByOk ? "OK" : "Cancel"));
        if (mSubstDialogDismissedByOk) {
            // Update statistic grid view with players on court
            mStatisticView.redraw();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                mSubstDialogDismissedByOk = true;
                mDlg.substitute();
                mDlg.dismiss();
                break;
            case R.id.btnDismiss:
                mSubstDialogDismissedByOk = false;
                mDlg.dismiss();
                break;
        }
    }

    /**
     * Save PlayerGamePojo to db
     *
     * @return
     */
    private boolean save() {
        mDbHelper.savePlayerStatistic(mGameId, mPeriod, mPlayers);
        return true;
    }

    private final static class PersistenceKeys {
        static final String PLAYERS_ON_COURT = "court";
        static final String PLAYERS_ON_BENCH = "bench";
    }
}
