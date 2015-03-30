package com.example.bbstatistics;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bbstatistics.com.example.bbstatistics.model.DbHelper;
import com.example.bbstatistics.dummy.DummyContent;

/**
 * A list fragment representing a list of Players. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link PlayerDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PlayerListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private Cursor mTeamsCursor;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayerListFragment() {
        Log.d(Consts.TAG, "PlayerListFragment.PlayerListFragment()");
    }

    public void setDatasource(Cursor listOfTeams) {
        Log.d(Consts.TAG, "PlayerListFragment.setDatasource()");
        mTeamsCursor = listOfTeams;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(Consts.TAG, "PlayerListFragment.onAttach()");
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Consts.TAG, "PlayerListFragment.onCreate()");

        // TODO: replace with a real list adapter.
//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, android.R.id.text1, DummyContent.ITEMS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(Consts.TAG, "PlayerListFragment.onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(Consts.TAG, "PlayerListFragment.onViewCreated()");

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(Consts.TAG, "PlayerListFragment.onActivityCreated()");
    }

    @Override
    public void onStart() {
        Log.d(Consts.TAG, "PlayerListFragment.onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(Consts.TAG, "PlayerListFragment.onResume()");
        super.onResume();
        //setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
        //        android.R.layout.simple_list_item_activated_1, android.R.id.text1, DummyContent.ITEMS));
        ArrayAdapter<DummyContent.DummyItem> dataAdapter1 = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_activated_1, android.R.id.text1, DummyContent.ITEMS);
        int[] bindTo = new int[]{R.id.txtTeamId, R.id.txtTeamName};
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(getActivity(), R.layout.row_layout_team
                , mTeamsCursor, DbHelper.Teams.COLUMNS, bindTo, 0);
        int[] bindTo2 = new int[]{android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter dataAdapter2 = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_2
                , mTeamsCursor, DbHelper.Teams.COLUMNS, bindTo2, 0);

        setListAdapter(dataAdapter2);
    }

    @Override
    public void onPause() {
        Log.d(Consts.TAG, "PlayerListFragment.onPause()");
        super.onPause();
    }

    @Override
    public void onDetach() {
        Log.d(Consts.TAG, "PlayerListFragment.onDetach()");
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Log.d(Consts.TAG, "PlayerListFragment.onListItemClick()");
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
//        mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }
}
