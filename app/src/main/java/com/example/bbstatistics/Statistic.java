package com.example.bbstatistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;


public class Statistic extends Activity {

    private long mGameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(Consts.TAG, "Statistic(Activity).onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        addListeners();
        getResources().getStringArray(R.array.bb_column_names);
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
        StatisticView statView = (StatisticView) findViewById(R.id.statisticView);
        buttonPlusMinus.setOnClickListener(statView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(Consts.TAG, "Statistic(Activity.onStart()");
    }

    @Override
    protected void onResume() {
        Log.v(Consts.TAG, "Statistic(Activity).onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.v(Consts.TAG, "Statistic(Activity).onPause()");
        super.onPause();
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

}
