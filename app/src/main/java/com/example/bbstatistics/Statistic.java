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


public class Statistic extends Activity {

    private long mGameId;
    private DbHelper mDbHelper;
    private StatisticView mStatisticView;

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
        mGameId = intent.getLongExtra(Consts.ACTIVITY_REQUEST_DATA_GAMEID_KEY, Consts.INVALID_ID);
        if(mGameId == Consts.INVALID_ID) {
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
        Log.v(Consts.TAG, "Statistic(Activity.onStart()");
    }

    @Override
    protected void onResume() {
        Log.v(Consts.TAG, "Statistic(Activity).onResume()");
        mDbHelper.open();
        mStatisticView.setDbHelper(mDbHelper);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.v(Consts.TAG, "Statistic(Activity).onPause()");
        super.onPause();
        mDbHelper.close();
    }

    @Override
    protected void onStop() {
        Log.v(Consts.TAG, "Statistic(Activity).onStop()");
        super.onStop();
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
        SubstitutePlayerDialog dlg = new SubstitutePlayerDialog(this);
        dlg.show();
        Log.v(Consts.TAG, "SubstituteDialog dismissed by" + (dlg.isIsOkPressed() ? "OK" : "Cancel") );
    }
}
