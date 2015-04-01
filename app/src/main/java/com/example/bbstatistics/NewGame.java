package com.example.bbstatistics;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;

import java.util.Calendar;


public class NewGame extends ActionBarActivity {
//public class NewGame extends Activity {

    private DatePicker mdpDateOfGame;
    private TimePicker mtpTimeOfGame;
    private DbHelper mDbHelper;
    private Cursor mTeamsCursor;
    private ListView mlvTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        //
        mlvTeams = (ListView) findViewById(R.id.listViewTeams);
        mlvTeams.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDbHelper = new DbHelper(this);
        // Get starting intent
        Intent intent = getIntent();
        // Set Date
        mdpDateOfGame = (DatePicker) findViewById(R.id.dpDate);
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mdpDateOfGame.init(year, month, day, null);
        //dpDateOfGame.getYear();
        // Time
        mtpTimeOfGame = (TimePicker) findViewById(R.id.tpTimeOfGame);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        mtpTimeOfGame.setIs24HourView(true);
        mtpTimeOfGame.setCurrentHour(hour);
        mtpTimeOfGame.setCurrentMinute(min);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDbHelper.open();
        // Load data from DB and populate Views
        mTeamsCursor = mDbHelper.getListOfTeams();
        int[] bindTo = new int[]{android.R.id.text1};
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1
                , mTeamsCursor, new String[]{DbHelper.Team.COL_NAME}, bindTo, 0);
        mlvTeams.setAdapter(dataAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void addGame(View view) {
        Intent intent = new Intent();
        intent.putExtra(Consts.ACTIVITY_RESULT_NEW_GAME_KEY, "Test");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_game, menu);
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
