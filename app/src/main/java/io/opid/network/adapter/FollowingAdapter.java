package io.opid.network.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import io.opid.OpidioApplication;
import io.opid.R;
import io.opid.model.Follower;
import io.opid.model.Followers;

public class FollowingAdapter extends PaginatedListAdapter<Follower, Followers> {

    public FollowingAdapter(Fragment fragment, Activity activity) {
        super(Followers.class, fragment, activity, R.layout.row_following);

        loadMoreData();
    }

    @Override
    public View createView(int position, View view, final Follower user) {
        TextView nameLabel = (TextView) view.findViewById(R.id.text_name);

        nameLabel.setText(user.getName());
        return view;
    }

    @Override
    protected String getURL() {
        return "/api/following/";
    }

    @Override
    protected void handleResponse(Followers response) {
        addItems(response.getFollowers());
        setCurrentPage(response.getPage());
        setMaxPages(response.getTotal_pages());
    }

    @Override
    public Map<String, String> getHeaders(Map<String, String> headers) {
        headers.put("access_token", OpidioApplication.getInstance().getAccessToken());
        return headers;
    }

    @Override
    protected int loadNext() {
        return 7;
    }


}
