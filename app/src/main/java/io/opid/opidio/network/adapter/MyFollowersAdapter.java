package io.opid.opidio.network.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import io.opid.opidio.OpidioApplication;
import io.opid.opidio.R;
import io.opid.opidio.model.Follower;
import io.opid.opidio.model.Followers;

import java.util.Map;

public class MyFollowersAdapter extends PaginatedListAdapter<Follower, Followers> {

    public MyFollowersAdapter(Fragment fragment, Activity activity) {
        super(Followers.class, fragment, activity, R.layout.row_follower);

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
        return "/api/my-followers/";
    }

    @Override
    protected void handleResponse(Followers response) {
        addItems(response.getFollowers());
        setCurrentPage(response.getPage());
        setMaxPages(response.getTotal_pages());
    }

    @Override
    public Map<String, String> getHeaders(Map<String, String> headers) {
        headers.put("Access-Token", OpidioApplication.getInstance().getAccessToken());
        return headers;
    }

    @Override
    protected int loadNext() {
        return 7;
    }


}
