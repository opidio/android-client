package io.opid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import io.opid.R;
import io.opid.network.adapter.FollowingAdapter;
import io.opid.network.adapter.MyFollowersAdapter;
import io.opid.network.adapter.PaginatedListAdapter;

public class FollowingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PaginatedListAdapter.AdapterStatusChanged, FollowingAdapter.UserUnfollowedEvent {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView followerList;
    private ListAdapter nextAdapter;

    public static FollowingFragment newInstance() {
        return new FollowingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_followers, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        followerList = (ListView) view.findViewById(R.id.main_list);
        followerList.setAdapter(new FollowingAdapter(this, getActivity()));

        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onRefresh() {
        View view = getView();
        if (view != null) {
            nextAdapter = new FollowingAdapter(this, getActivity());

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
    public void userUnfollowed() {
        onRefresh();
    }
}
