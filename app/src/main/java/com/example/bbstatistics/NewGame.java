package com.example.bbstatistics;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import android.widget.Toast;

import com.example.bbstatistics.model.DbHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class NewGame extends ActionBarActivity implements View.OnClickListener {
//public class NewGame extends Activity {

    private long mGameId;  // If editing existing game, this is _id of DB record
    // Widget GUI
    private Button btnCalendar, btnTimePicker;
    private EditText txtDate, txtTime;
    private DbHelper mDbHelper;
    //private Cursor mPlayersCursor;
    private ListView mlvTeams;
    private Spinner mspnTeams;
    private SimpleCursorAdapter mPlayersDataAdapter;
    private ListView mlvPlayers;

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
        mlvPlayers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        int[] bindTo = new int[]{android.R.id.text1, android.R.id.text2};
        mPlayersDataAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_2
                , null, new String[]{DbHelper.Player.COL_NUMBER, DbHelper.Player.COL_NAME}, bindTo, 0);
        mlvPlayers.setAdapter(mPlayersDataAdapter);
        //
        mDbHelper = new DbHelper(this);
        // Get starting intent
        Intent intent = getIntent();
        // If gameId present, edit game
        mGameId = intent.getLongExtra(Consts.ACTIVITY_REQUEST_DATA_GAMEID_KEY, DbHelper.INVALID_ID);
        if(mGameId == DbHelper.INVALID_ID) {
            return;
        }
        // _id of existing game is passed, load it and fill data
        // TODO: Load existing game
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Consts.TAG, "NewGame#onResume()");
        mDbHelper.open();
        // Load data from DB and populate Views
        Cursor teamsCursor = mDbHelper.getListOfTeams();
        int[] bindTo = new int[]{android.R.id.text1};
        String[] cursorColumns = new String[]{DbHelper.Team.COL_NAME};
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1
                , teamsCursor, cursorColumns, bindTo, 0);
        mlvTeams.setAdapter(dataAdapter);
        // Reuse cursor to populate spinner with teams
        SimpleCursorAdapter dataAdapterSpinner = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item
                , teamsCursor, cursorColumns, bindTo, 0);
        dataAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspnTeams.setAdapter(dataAdapterSpinner);
    }

    @Override
    protected void onPause() {
        Log.d(Consts.TAG, "NewGame#onPause()");
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

    /**
     * Add game to DB.
     * @param view
     */
    public void addGame(View view) {
        StringBuffer dbgStrBuf = new StringBuffer(128);
        dbgStrBuf.append("Adding new game. ");
        // Fetch date/time from UI
        String dateTimeString = getDateTimeDbString();
        if (dateTimeString == null) {
            Toast.makeText(this, "Missing date/time", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(Consts.TAG, "Date/time in DB format:" + dateTimeString);
        // Get teamID from selection
        final int checkedItemPos = mlvTeams.getCheckedItemPosition();
        if (checkedItemPos == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Team not selected", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor c = ((SimpleCursorAdapter) mlvTeams.getAdapter()).getCursor();
        c.moveToPosition(checkedItemPos);
        int teamId = c.getInt(0);
        dbgStrBuf.append("Selected teamId:").append(teamId).append(", Team:").append(c.getString(1));
        Log.d(Consts.TAG, "Selected teamId:" + teamId);
        // Get opponent _id
        final int selectedOpponentTeamPos = mspnTeams.getSelectedItemPosition();
        if (selectedOpponentTeamPos == Spinner.INVALID_POSITION) {
            Toast.makeText(this, "Opponent team not selected", Toast.LENGTH_SHORT).show();
            return;
        }
        c = ((SimpleCursorAdapter) mlvTeams.getAdapter()).getCursor();
        c.moveToPosition(selectedOpponentTeamPos);
        int opponentTeamId = c.getInt(0);
        dbgStrBuf.append(", opponentId:").append(opponentTeamId).append(",name:").append(c.getString(1));
        Log.d(Consts.TAG, "Selected opponent teamId:" + opponentTeamId);
        // Description - optional
        EditText editDesc = (EditText) findViewById(R.id.description);
        String desc = editDesc.getText().toString();
        dbgStrBuf.append(", desc:").append(desc);
        // Persist to DB
        long gameId = mDbHelper.addGame(teamId, opponentTeamId, getDateTimeDbString(), desc);
        Log.v(Consts.TAG, dbgStrBuf.toString());
        dbgStrBuf.append(". ret _id:").append(gameId);
        // We have Game._id, insert players
        SparseBooleanArray checked = mlvPlayers.getCheckedItemPositions();
        if(checked.size() != 0) {
            SimpleCursorAdapter adapter = (SimpleCursorAdapter)mlvPlayers.getAdapter();
            c = adapter.getCursor();
            Long[] playersAtGame = new Long[checked.size()];
            for(int i = 0; i < checked.size(); i++) {
                c.moveToPosition(checked.keyAt(i));
                long playerId = c.getLong(c.getColumnIndex(DbHelper.Player.COL_ID));
                playersAtGame[i] = playerId;
            }
            mDbHelper.addPlayersToGame(gameId, playersAtGame);
        }
        // Return result
        Intent intent = new Intent();
        intent.putExtra(Consts.ACTIVITY_RESULT_NEW_GAME_KEY, gameId);
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

    /**
     * Get date/time from UI and convert it to DB format
     * @return String presentation of entered Date and time
     */
    private String getDateTimeDbString() {
        Calendar c = Calendar.getInstance();
        // Date
        String dateString = txtDate.getText().toString();
        DateFormat dateFormatter = DateFormat.getDateInstance();
        if (dateString.length() > 0) {
            try {
                Date formDate = dateFormatter.parse(dateString);
                c.setTime(formDate);
            } catch (ParseException e) {
                Log.d(Consts.TAG, "Parsing date failed:" + dateString);
                Toast.makeText(this, R.string.err_invalid_date, Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            Toast.makeText(this, R.string.err_missing_date, Toast.LENGTH_SHORT).show();
            return null;
        }
        SimpleDateFormat dbDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dbDateFormatter.format(c.getTime());
        // Time
        String timeString = txtTime.getText().toString();
        DateFormat timeFormatter = DateFormat.getTimeInstance();
        if (timeString.length() > 0) {
            Log.d(Consts.TAG, "Parsing time:" + timeString);
            try {
                Date formTime = timeFormatter.parse(timeString);
                c.setTime(formTime);
            } catch (ParseException e) {
                Toast.makeText(this, R.string.err_invalid_time, Toast.LENGTH_SHORT).show();
                Log.d(Consts.TAG, "Parsing time failed:" + timeString);
            }
        } else {
            Toast.makeText(this, R.string.err_missing_time, Toast.LENGTH_SHORT).show();
            return null;
        }
        SimpleDateFormat dbTimeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = dbTimeFormatter.format(c.getTime());
        return formattedDate + " " + formattedTime;
    }

    /**
     * Show Date or Time picker
     * @param v Clicked View
     */
    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        if (v == btnCalendar) {
            // Variable for storing date
            final int mYear, mMonth, mDay;
            final DateFormat dateFormatter = DateFormat.getDateInstance();
            // Parse date present in EditText date
            String dateString = txtDate.getText().toString();
            if (dateString.length() > 0) {
                try {
                    Date formDate = dateFormatter.parse(dateString);
                    c.setTime(formDate);
                } catch (ParseException e) {
                    Log.d(Consts.TAG, "Parsing date failed:" + dateString);
                }
            }
            // Get fields needed for DatePickerDialog constructor
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            c.set(year, monthOfYear, dayOfMonth);
                            String formattedDate = dateFormatter.format(c.getTime());
                            txtDate.setText(formattedDate);
                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }
        if (v == btnTimePicker) {
            // Variable for storing time
            final int hour, minute;
            final DateFormat timeFormatter = DateFormat.getTimeInstance();
            String timeString = txtTime.getText().toString();
            if (timeString.length() > 0) {
                Log.d(Consts.TAG, "Parsing time:" + timeString);
                try {
                    Date formTime = timeFormatter.parse(timeString);
                    c.setTime(formTime);
                } catch (ParseException e) {
                    Log.d(Consts.TAG, "Parsing time failed:" + timeString);
                }
            }
            // Process to get Time
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog tpd = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                            // Display Selected time in textbox
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minuteOfHour);
                            String formattedTime = timeFormatter.format(c.getTime());
                            txtTime.setText(formattedTime);
                        }
                    }, hour, minute, true);
            tpd.show();
        }
    }
}
