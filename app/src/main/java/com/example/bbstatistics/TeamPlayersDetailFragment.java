package com.example.bbstatistics;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.bbstatistics.model.DbHelper;

/**
 * A fragment representing a single Team detail screen.
 * This fragment is either contained in a {@link PlayerListActivity}
 * in two-pane mode (on tablets) or a {@link PlayerDetailActivity}
 * on handsets.
 */
public class TeamPlayersDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    /**
     * The Team this fragment is presenting.
     */
    private String mTeam;
    private Cursor mCursor;
    private SimpleCursorAdapter mDataAdapter;
    private Callbacks mCallback;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TeamPlayersDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the team name specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mTeam = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstState) {
        View rootView = inflater.inflate(R.layout.add_player_fragment, container, false);
        // Show the Team name in a TextView.
        int[] bindTo = new int[]{android.R.id.text1, android.R.id.text2};
        mDataAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_2
                , mCursor, new String[]{DbHelper.Player.COL_NUMBER, DbHelper.Player.COL_NAME}, bindTo, 0);
        ListView lvPlayers = (ListView) rootView.findViewById(R.id.listViewPlayersOfTeam);
        lvPlayers.setAdapter(mDataAdapter);
        // Add "Add" button listener
        Button btnAdd = (Button) rootView.findViewById(R.id.btn_add_new_player);
        final EditText txPlayerNumber = (EditText) rootView.findViewById(R.id.editTextNewPlayerNumber);
        final EditText txPlayerName = (EditText) rootView.findViewById(R.id.editTextNewPlayerName);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get new user data
                CharSequence playerNum = txPlayerNumber.getText();
                CharSequence playerName = txPlayerName.getText();
                if (playerNum == null || playerName == null || playerNum.length() == 0 || playerName.length() == 0) {
                    Toast.makeText(getActivity(), "Enter both player data", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(Consts.TAG, "New player: #:" + playerNum + ", Name:" + playerName);
                Cursor cursor = mCallback.onItemAdded(playerNum.toString(), playerName.toString());
                txPlayerNumber.getText().clear();
                txPlayerName.getText().clear();
                // Hiding the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txPlayerNumber.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(txPlayerName.getWindowToken(), 0);

                // Refresh ListView
                // TODO: How to update cursor?
                mDataAdapter.swapCursor(cursor);
                //mDataAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), playerName + " added", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallback = (Callbacks) activity;
    }

    public void setDataSource(Cursor cursor) {
        mCursor = cursor;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been added.
         */
        public Cursor onItemAdded(String id, String name);
    }
}
