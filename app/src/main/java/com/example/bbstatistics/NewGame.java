package com.example.bbstatistics;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;

import java.util.Calendar;


public class NewGame extends ActionBarActivity implements View.OnClickListener {
//public class NewGame extends Activity {

    // Widget GUI
    private Button btnCalendar, btnTimePicker;
    private EditText txtDate, txtTime;
    private DbHelper mDbHelper;
    private Cursor mTeamsCursor, mPlayersCursor;
    private ListView mlvTeams, mlvPlayers;
    private Spinner mspnTeams;
    // Variable for storing current date and time
    private int mYear, mMonth, mDay, mHour, mMinute;
    private android.widget.SimpleCursorAdapter mPlayersDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        //
        btnCalendar = (Button) findViewById(R.id.btnCalendar);
        btnTimePicker = (Button) findViewById(R.id.btnTimePicker);

        txtDate = (EditText) findViewById(R.id.txtDate);
        txtTime = (EditText) findViewById(R.id.txtTime);

        btnCalendar.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        //
        mlvTeams = (ListView) findViewById(R.id.listViewTeams);
        mlvTeams.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // When team selected, fill players list with players from team
        mlvTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get _id of selected team
                Cursor c = ((SimpleCursorAdapter) mlvTeams.getAdapter()).getCursor();
                c.moveToPosition(position);
                int teamId = c.getInt(0);
                Cursor newPlayersCursor = mDbHelper.getPlayersOfTeam(teamId);
                mPlayersDataAdapter.swapCursor(newPlayersCursor);
            }
        });
        mspnTeams = (Spinner) findViewById(R.id.spinnerTeam);
        mlvPlayers = (ListView) findViewById(R.id.listViewPlayersOfTeam);
        //mlvPlayers.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        int[] bindTo = new int[]{android.R.id.text1, android.R.id.text2};
        mPlayersDataAdapter = new android.widget.SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_2
                , null, new String[]{DbHelper.Player.COL_NUMBER, DbHelper.Player.COL_NAME}, bindTo, 0);
        mlvPlayers.setAdapter(mPlayersDataAdapter);
        //
        mDbHelper = new DbHelper(this);
        // Get starting intent
        Intent intent = getIntent();

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
        String[] cursorColumns = new String[]{DbHelper.Team.COL_NAME};
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1
                , mTeamsCursor, cursorColumns, bindTo, 0);
        mlvTeams.setAdapter(dataAdapter);
        // Reuse cursor to populate spinner with teams
        SimpleCursorAdapter dataAdapterSpinner = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item
                , mTeamsCursor, cursorColumns, bindTo, 0);
        dataAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspnTeams.setAdapter(dataAdapterSpinner);
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

    @Override
    public void onClick(View v) {
        if (v == btnCalendar) {
            // Process to get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }
        if (v == btnTimePicker) {
            // Process to get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog tpd = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // Display Selected time in textbox
                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, true);
            tpd.show();
        }
    }
}
