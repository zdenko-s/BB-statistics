package com.example.bbstatistics;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.bbstatistics.model.BBPlayer;
import com.example.bbstatistics.model.DbHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

class GestureListener extends GestureDetector.SimpleOnGestureListener {

    public boolean onDown(MotionEvent e) {
        return true;
    }

    public boolean onDoubleTap(MotionEvent e) {
        Log.d(Consts.TAG, "Double_Tap");
        return true;
    }
}

public class StartupActivity extends Activity {

    SimpleCursorAdapter mDataAdapter;
    private DbHelper mDbHelper;
    private ListView mlvGames;
    private boolean mActivityResultOk = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.v(Consts.TAG, "StartupActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        // Init singleton
        BBPlayer.setContext(getApplicationContext());
        // Setup list, list's event listeners,
        mlvGames = (ListView) findViewById(R.id.lvAllGames);
        //mlvGames.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //mlvGames.setBackgroundResource(R.drawable.listview_selector);
        // Create Adapter
        /*
        int[] bindTo = new int[]{android.R.id.text1, android.R.id.text2};
        mDataAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_2
                , null, new String[] {DbHelper.Game.COL_DATE_TIME, DbHelper.Game.COL_DESCRIPTION}, bindTo, 0);
        */
        int[] bindTo = new int[]{R.id.txtGameId, R.id.txtDateTime, R.id.txtTeamName, R.id.txtOpponentTeamName, R.id.txtDescription};
        mDataAdapter = new SimpleCursorAdapter(this, R.layout.row_layout_game
                , null, DbHelper.Game.VGAME_COLUMNS, bindTo, 0);

        mlvGames.setAdapter(mDataAdapter);
        mlvGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(Consts.TAG, "onItemClick(); position:" + position + ", id:" + id);
                view.setSelected(true);
            }
        });
        mlvGames.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(Consts.TAG, "onItemLongClick(); position:" + position + ", id:" + id);
                startGame(id);
                return true;
            }
        });
        /*
        GestureDetector gestureDectector = new GestureDetector(this, new GestureListener());
        mlvGames.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                ListView list = (ListView) v;
                if(list != null) {
                    int position = list.pointToPosition((int) e.getX(), (int) e.getY());
                    Log.d(Consts.TAG, "Touched row:" + position);
                }
                return false;
            }
        });
        */
        //
        mDbHelper = new DbHelper(this);
        Log.v(Consts.TAG, DbHelper.GameStatistic.SQL_CREATE_TABLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.v(Consts.TAG, "StartupActivity.onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.v(Consts.TAG, "S-StartupActivity.onResume()");
        //*
        mDbHelper.open();
        // Display list of games in list
        Cursor gamesCursor = mDbHelper.getGames();
        Cursor oldCursor = mDataAdapter.swapCursor(gamesCursor);
        if(oldCursor != null && !oldCursor.isClosed())
            oldCursor.close();
//        Log.v(Consts.TAG, "E-StartupActivity.onResume()");
    }

    @Override
    protected void onPause() {
//        Log.v(Consts.TAG, "StartupActivity.onPause()");
        //TODO: Commit changes to DB
        mDbHelper.close();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.v(Consts.TAG, "StartupActivity.onStop()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_startup, menu);
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

    public void addTeam(View view) {
        // Show dialog to add team
//        Log.d(Consts.TAG, "addTeam clicked");
        /*
        final Dialog addTeamDlg = new Dialog(this);
        addTeamDlg.setContentView(R.layout.add_team_dialog);
        Button dismissBtn = (Button) addTeamDlg.findViewById(R.id.btn_dismiss);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTeamDlg.dismiss();
            }
        });
        Log.d(Consts.TAG, "addTeam clicked. Show dialog");
        addTeamDlg.show();
        */
        AddTeamDialog dlg = new AddTeamDialog(this, mDbHelper);
        dlg.show();
    }

    /**
     * Start new activity to insert new player
     *
     * @param view
     */
    public void addPlayer(View view) {
        Intent intent = new Intent(this, PlayerListActivity.class);
        startActivity(intent);
    }

    /**
     * Add game to team.
     *
     * @param view
     */
    public void addGame(View view) {
        Intent intent = new Intent(this, NewGame.class);
        startActivityForResult(intent, Consts.ACTIVITY_REQUEST_NEW_GAME);
        /*
        Cursor cursor = mDbHelper.getListOfTeams();
        new AlertDialog.Builder(this)
                .setTitle("Select team:")
                .setCursor(cursor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(StartupActivity.this, "Selected " + which, Toast.LENGTH_SHORT).show();
                        Log.d(Consts.TAG, "Selected team:" + which);
                    }
                }, DbHelper.Team.COL_NAME)
                .create()
                .show();
                */
    }

    public void editGame(long gameId) {
        Intent intent = new Intent(this, NewGame.class);
        intent.putExtra(Consts.ACTIVITY_REQUEST_DATA_GAMEID_KEY, gameId);
        startActivityForResult(intent, Consts.ACTIVITY_REQUEST_EDIT_GAME);
    }

    public void startGame(long gameId) {
        Intent intent = new Intent(this, Statistic.class);
        intent.putExtra(Consts.ACTIVITY_REQUEST_DATA_GAMEID_KEY, gameId);
        startActivity(intent);
        //startActivityForResult(intent, Consts.ACTIVITY_REQUEST_START_GAME);
    }

    /**
     * Return point from child activity. NOTE: This is called before onResume()
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Consts.TAG, "StartupActivity.onActivityResult(), requestCode:" + requestCode + ", resultCode:" + resultCode);
        if (resultCode == RESULT_OK) {
            // check if the request code is same as what is passed
            if (requestCode == Consts.ACTIVITY_REQUEST_NEW_GAME) {
                long gameId = data.getLongExtra(Consts.ACTIVITY_RESULT_NEW_GAME_KEY, -1);
                Log.d(Consts.TAG, "StartupActivity.onActivityResult (NewGame _id):" + gameId);
                mActivityResultOk = true;
                // Refresh list
                //Cursor cursor = mDbHelper.getListOfTeams();
                //mDataAdapter.swapCursor(cursor);
            }
        }
    }

    /**
     * Export SQLite database to SD card
     * @param view
     */
    public void exportDb(View view) {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/"+ "com.example.bbstatistics" + "/databases/" + DbHelper.DATABASE_NAME;
        String backupDBPath = DbHelper.DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
            Log.v(Consts.TAG, "Db exported to:" + backupDB.getAbsolutePath());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
