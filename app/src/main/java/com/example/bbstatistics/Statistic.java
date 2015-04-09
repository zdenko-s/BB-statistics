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

import com.example.bbstatistics.model.DbHelper;
import com.example.bbstatistics.pojo.PlayerGamePojo;


public class Statistic extends Activity implements View.OnClickListener {
    static final String TAG = "Statistic";
    // In memory cache of player data. Sorted by dress number. First row may contain opponent team data.
    // (Depending on user preferences)
    private PlayerGamePojo[] mPlayers;

    private long mGameId;
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
//        if(mGameId == DbHelper.INVALID_ID) {
//            return;
//        }
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(Consts.TAG, "Statistic(Activity)#onRestoreInstanceState()");
        if (savedInstanceState != null) {
            Log.v(Consts.TAG, "Statistic(Activity)#onRestoreInstanceState(): saved state present");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(Consts.TAG, "Statistic(Activity)#onSaveInstanceState()");
        mStatisticView.logPlayersOnCourt();
        // NOT called if the activity is closed by the user pressing the Back button or programmatically by calling finish().
        // called when the activity completes its active lifecycle, before it is killed
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
     *
     * @param view Not used
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

}
