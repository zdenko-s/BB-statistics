package com.example.bbstatistics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;


/**
 * An activity representing a list of Players. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PlayerDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PlayerListFragment} and the item details
 * (if present) is a {@link PlayerDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link PlayerListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class PlayerListActivity extends FragmentActivity
        implements PlayerListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private DbHelper mDbHelper;
    private PlayerListFragment mTeamsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Consts.TAG, "PlayerListActivity.onCreate()");
        setContentView(R.layout.activity_player_list);
        Log.d(Consts.TAG, "PlayerListActivity.onCreate()-setContentView");
        mDbHelper = new DbHelper(this);
        //mDbHelper.open();

        // Show the Up button in the action bar.
/*        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    */
        mTeamsFragment = ((PlayerListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.player_list));
        if (mTeamsFragment != null) {
            Log.d(Consts.TAG, "PlayerListActivity.onCreate(), teamsFragment present");
        } else {
            Log.d(Consts.TAG, "PlayerListActivity.onCreate(), teamsFragment is null");
        }

        if (findViewById(R.id.player_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((PlayerListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.player_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    protected void onStart() {
        Log.d(Consts.TAG, "PlayerListActivity.onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(Consts.TAG, "PlayerListActivity.onResume()");
        super.onResume();
        mDbHelper.open();
        if (mTeamsFragment != null) {
            Log.d(Consts.TAG, "PlayerListActivity.onResume(), teamsFragment present");
            mTeamsFragment.setDatasource(mDbHelper.getListOfTeams());
        } else {
            Log.d(Consts.TAG, "PlayerListActivity.onResume(), teamsFragment is null");
        }
    }

    @Override
    protected void onPause() {
        Log.d(Consts.TAG, "PlayerListActivity.onPause()");
        super.onPause();
        mDbHelper.close();
    }

    @Override
    protected void onStop() {
        Log.d(Consts.TAG, "PlayerListActivity.onStop()");
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
/*            navigateUpFromSameTask(this); */
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link PlayerListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        Log.d(Consts.TAG, "PlayerListActivity.onItemSelected(String id):" + id);
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(PlayerDetailFragment.ARG_ITEM_ID, id);
            PlayerDetailFragment fragment = new PlayerDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.player_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, PlayerDetailActivity.class);
            detailIntent.putExtra(PlayerDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
