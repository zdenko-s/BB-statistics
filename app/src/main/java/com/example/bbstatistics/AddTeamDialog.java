package com.example.bbstatistics;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;

public class AddTeamDialog extends Dialog {
    final private DbHelper mDbHelper;
    private Context mContext;

    public AddTeamDialog(Context context, DbHelper dbHelper) {
        super(context);
        mDbHelper = dbHelper;
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_team_dialog);
        Button btnDismiss = (Button) findViewById(R.id.btn_dismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // Load list of teams
        Cursor cursor = mDbHelper.getListOfTeams();
        // XML defined views which the data will be bound to. Specify ID of widgets
        int[] bindTo = new int[]{R.id.txtTeamId, R.id.txtTeamName};
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(mContext, R.layout.row_layout_team
                , cursor, DbHelper.Team.COLUMNS, bindTo, 0);
        ListView lvTeams = (ListView) findViewById(R.id.listViewTeams);
        lvTeams.setAdapter(dataAdapter);

        //Add "Add" button listener
        Button btnAdd = (Button) findViewById(R.id.btn_add_new_team);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get content of TextView
                TextView newTeam = (TextView) findViewById(R.id.editTextNewTeam);
                String teamName = newTeam.getText().toString();
                mDbHelper.addTeam(teamName);
                Toast.makeText(mContext, teamName + " added", Toast.LENGTH_SHORT);
                dismiss();
            }
        });
    }
}
