package com.example.bbstatistics;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;

import com.example.bbstatistics.com.example.bbstatistics.model.BBPlayer;


public class Statistic extends Activity {
    private BBPlayer bbPlayerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        bbPlayerModel = new BBPlayer(getApplicationContext());
        setContentView(R.layout.activity_statistic);
        addListeners();
        getResources().getStringArray(R.array.bb_column_names);
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

    public BBPlayer getModel() {
        return bbPlayerModel;
    }
}
