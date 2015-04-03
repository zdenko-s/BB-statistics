package com.example.bbstatistics;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.bbstatistics.com.example.bbstatistics.model.BBPlayer;
import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(Consts.TAG, "StartupActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        // Init singleton
        BBPlayer.setContext(getApplicationContext());
        // Setup list, list's event listeners,
        mlvGames = (ListView) findViewById(R.id.listViewTeamsTest);
        mlvGames.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Create Adapter
        int[] bindTo = new int[]{android.R.id.text1, android.R.id.text2};
        mDataAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_2
                , null, DbHelper.Team.COLUMNS, bindTo, 0);
        mlvGames.setAdapter(mDataAdapter);
        mlvGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(Consts.TAG, "onItemClick(); position:" + position + ", id:" + id);
            }
        });
        mlvGames.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(Consts.TAG, "onItemLongClick(); position:" + position + ", id:" + id);
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
        //mDbHelper.open();
        // Test adapter
        /*
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_activated_2,
                mDbHelper.getListOfTeams(),
                DbHelper.Team.COLUMNS,
                new int[]{android.R.id.text1, android.R.id.text2}, 0);

        ListView listView = (ListView) findViewById(R.id.listViewTeamsTest);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        //*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(Consts.TAG, "StartupActivity.onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(Consts.TAG, "StartupActivity.onResume()");
        //*
        mDbHelper.open();
        // Display list of games in list
        Cursor teamsCursor = mDbHelper.getListOfTeams();
        //int[] bindTo = new int[]{android.R.id.text1, android.R.id.text2};
        //mDataAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_2
        //        , teamsCursor, DbHelper.Team.COLUMNS, bindTo, 0);
        Cursor oldCursor = mDataAdapter.swapCursor(teamsCursor);
        //mlvGames.setAdapter(mDataAdapter);
        //*/
    }

    @Override
    protected void onPause() {
        Log.v(Consts.TAG, "StartupActivity.onPause()");
        //TODO: Commit changes to DB
        mDbHelper.close();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(Consts.TAG, "StartupActivity.onStop()");
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
        Log.d(Consts.TAG, "addTeam clicked");
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
                String message = data.getStringExtra(Consts.ACTIVITY_RESULT_NEW_GAME_KEY);
                Log.d(Consts.TAG, "StartupActivity.onActivityResult (NewGame):" + message);
                // Refresh list
                //Cursor cursor = mDbHelper.getListOfTeams();
                //mDataAdapter.swapCursor(cursor);
            }
        }
    }
}
