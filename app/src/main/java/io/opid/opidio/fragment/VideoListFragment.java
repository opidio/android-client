package io.opid.opidio.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import io.opid.opidio.R;
import io.opid.opidio.network.adapter.PaginatedListAdapter;
import io.opid.opidio.network.adapter.VideoListAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link io.opid.opidio.fragment.VideoListFragment.OnVideoSelectListener}
 * interface.
 */
public class VideoListFragment extends OpidioFragment implements SwipeRefreshLayout.OnRefreshListener, PaginatedListAdapter.AdapterStatusChanged, VideoListAdapter.ClickListener {
    private VideoListAdapter nextAdapter;
    private OnVideoSelectListener mListener;

//    private OnFragmentInteractionListener mListener;

    public VideoListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        ListView listView = (ListView) view.findViewById(R.id.main_list);
        listView.setAdapter(new VideoListAdapter(this, getActivity()));
        View progressLayout = view.findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.VISIBLE);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onRefresh() {
        View view = getView();
        if (view != null) {
            // Preload a new adapter, will be used after the first items are loaded
            nextAdapter = new VideoListAdapter(this, getActivity());
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
    public void videoClick(int channelId, int videoId, String videoUrl) {
        mListener.onVideoSelect(channelId, videoId, videoUrl);
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
    public String getName() {
        return getString(R.string.all_videos);
    }

    public interface OnVideoSelectListener {
        public void onVideoSelect(int id, int videoId, String channelUrl);
    }

}
