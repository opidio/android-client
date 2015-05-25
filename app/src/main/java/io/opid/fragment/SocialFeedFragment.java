package io.opid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import io.opid.OpidioApplication;
import io.opid.R;
import io.opid.network.adapter.PaginatedListAdapter;
import io.opid.network.adapter.SocialFeedAdapter;

public class SocialFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PaginatedListAdapter.AdapterStatusChanged, SocialFeedAdapter.ClickListener {
    private SocialFeedAdapter nextAdapter;
    private OnVideoSelectListener mListener;

    public static SocialFeedFragment newInstance() {
        SocialFeedFragment fragment = new SocialFeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SocialFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        ListView listView = (ListView) view.findViewById(R.id.main_list);
        View progressLayout = view.findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.VISIBLE);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // This fragment could be created before there's an access token, in which
        // case the fragment will be reloaded once there's one available.
        if (OpidioApplication.getInstance().getAccessToken() != null)
            listView.setAdapter(new SocialFeedAdapter(this, getActivity()));

        return view;
    }



    @Override
    public void onRefresh() {
        View view = getView();
        if (view != null) {
            // Preload a new adapter, will be used after the first items are loaded
            nextAdapter = new SocialFeedAdapter(this, getActivity());
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnVideoSelectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnVideoSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void videoClick(int channelId, int videoId, String url) {
        mListener.onVideoSelect(channelId, videoId, url);
    }

    public interface OnVideoSelectListener {
        void onVideoSelect(int id, int videoId, String channelUrl);
    }

}
