package com.example.bbstatistics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;


public class StartupActivity extends Activity {

    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(Consts.TAG, "StartupActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        // Add listeners
        mDbHelper = new DbHelper(this);
        //mDbHelper.open();
        // Test adapter
        /*
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.row_layout_team,
                mDbHelper.getListOfTeams(),
                DbHelper.Teams.COLUMNS,
                new int[]{R.id.txtTeamId, R.id.txtTeamName}, 0);

        ListView listView = (ListView) findViewById(R.id.listViewTeamsTest);
        //listView.setAdapter(adapter);
        */
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
        Log.v(Consts.TAG, "StartupActivity.onResume()");
        mDbHelper.open();
        super.onResume();
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
     * Add game to team. Show dialog to choose team. When team selected, add game.
     *
     * @param view
     */
    public void addGame(View view) {
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
    }
}
