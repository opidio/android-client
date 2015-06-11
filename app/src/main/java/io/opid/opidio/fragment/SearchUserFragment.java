package io.opid.opidio.fragment;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import io.opid.opidio.R;
import io.opid.opidio.network.adapter.PaginatedListAdapter;
import io.opid.opidio.network.adapter.UserSearchResultAdapter;

public class SearchUserFragment extends OpidioFragment implements View.OnClickListener, TextView.OnEditorActionListener, SwipeRefreshLayout.OnRefreshListener, PaginatedListAdapter.AdapterStatusChanged {

    private Button submitButton;
    private EditText searchField;
    private ListAdapter nextAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView searchResults;

    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_user, container, false);

        submitButton = (Button) view.findViewById(R.id.search_button);
        searchField = (EditText) view.findViewById(R.id.edit_name);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        searchResults = (ListView) view.findViewById(R.id.main_list);

        swipeRefreshLayout.setOnRefreshListener(this);
        submitButton.setOnClickListener(this);
        searchField.setOnEditorActionListener(this);

        return view;
    }


    @Override
    public void onRefresh() {
        View view = getView();
        if (view != null) {
            search();
        }
    }


    @Override
    public void statusChanged(boolean loading) {
        View view = getView();
        if (!loading && view != null) {
            SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
            swipeRefreshLayout.setRefreshing(false);

            View progressLayout = view.findViewById(R.id.progress_layout);
            progressLayout.setVisibility(View.GONE);

            if (nextAdapter != null) {
                ListView listView = (ListView) view.findViewById(R.id.main_list);
                listView.setAdapter(nextAdapter);
                nextAdapter = null;

            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        search();
    }

    private void search() {
        String name = searchField.getText().toString();
        searchResults.setAdapter(new UserSearchResultAdapter(this, getActivity(), name));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            search();
        }
        return false;
    }

    @Override
    public


    String getName() {
        return getString(R.string.search_users);
    }

}
