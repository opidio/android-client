package io.opid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.opid.network.adapter.PaginatedListAdapter;
import io.opid.R;
import io.opid.network.adapter.VideoListAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class VideoListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PaginatedListAdapter.AdapterStatusChanged {
    private VideoListAdapter nextAdapter;

//    private OnFragmentInteractionListener mListener;

    public static VideoListFragment newInstance() {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
                nextAdapter = null;

            }
        }
    }

    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/
    /*
    //TODO: Use this
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }*/

}