package com.example.bbstatistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;
import com.example.bbstatistics.pojo.PlayerGamePojo;


public class Statistic extends Activity implements View.OnClickListener {

    private PlayerGamePojo[] mPlayers;
    private int mPlayerCount;

    private final static class PersistenceKeys {
        static final String PLAYERS_ON_COURT = "court";
        static final String PLAYERS_ON_BENCH = "bench";
    }
    private long mGameId;
    private long[] mPlayersOnCourt;//, mPlayersOnBench;
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
        if(mGameId == DbHelper.INVALID_ID) {
            return;
        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(Consts.TAG, "Statistic(Activity)#onResume()");
        mDbHelper.open();
        mStatisticView.setDbHelper(mDbHelper);
        // Load players of the game from db
        mPlayers = mDbHelper.loadPlayersOfGame(mGameId);
        if(mPlayers != null) {
            mPlayerCount = mPlayers.length;
            // Optimize. Allocate max size of arrays
            //mPlayersOnBench = new long[mPlayers.length];
            // Allow program to handle more players on court than actually present
            mPlayersOnCourt = new long[mPlayers.length];
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(Consts.TAG, "Statistic(Activity)#onRestoreInstanceState()");
        if(savedInstanceState != null) {
            //
            mPlayersOnCourt = savedInstanceState.getLongArray(PersistenceKeys.PLAYERS_ON_COURT);
            //mPlayersOnBench = savedInstanceState.getLongArray(PersistenceKeys.PLAYERS_ON_BENCH);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(Consts.TAG, "Statistic(Activity)#onSaveInstanceState()");
        // NOT called if the activity is closed by the user pressing the Back button or programmatically by calling finish().
        // called when the activity completes its active lifecycle, before it is killed
        //outState.putLongArray(PersistenceKeys.PLAYERS_ON_BENCH, mPlayersOnBench);
        outState.putLongArray(PersistenceKeys.PLAYERS_ON_COURT, mPlayersOnCourt);
    }

    @Override
    protected void onPause() {
        Log.v(Consts.TAG, "Statistic(Activity)#onPause()");
        super.onPause();
        mDbHelper.close();
    }

    // http://developer.android.com/training/basics/activity-lifecycle/pausing.html
    @Override
    protected void onStop() {
        super.onStop();
        // you should not use onPause() to store user changes (such as personal information entered into a form) to permanent storage
        Log.v(Consts.TAG, "Statistic(Activity)#onStop()");
        // TODO: Persist data to DB
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_statistic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Show dialog where used can choose players to substitute
     * @param view
     */
    public void substitutePlayers(View view) {
        //mDlg = new SubstitutePlayerDialog(this, R.style.CustomDialog, mPlayers, mPlayersOnCourt);
        mDlg = new SubstitutePlayerDialog(this, mPlayers, mPlayersOnCourt);
        mDlg.show();
        Log.v(Consts.TAG, "SubstituteDialog dismissed by" + (mSubstDialogDismissedByOk ? "OK" : "Cancel"));
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
}
