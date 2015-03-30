package com.example.bbstatistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;


public class StartupActivity extends Activity {

    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        // Add listeners
        mDbHelper = new DbHelper(this);
        mDbHelper.open();
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
    protected void onPause() {
        Log.v(Consts.TAG, "StartupActivity.onPause()");
        //TODO: Commit changes to DB
        mDbHelper.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v(Consts.TAG, "StartupActivity.onResume()");
        mDbHelper.open();
        super.onResume();
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

    public void addPlayer(View view) {
        Intent intent = new Intent(this, Statistic.class);
        startActivity(intent);
    }
}
