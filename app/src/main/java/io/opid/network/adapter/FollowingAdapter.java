package io.opid.network.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import io.opid.Config;
import io.opid.OpidioApplication;
import io.opid.R;
import io.opid.model.Follower;
import io.opid.model.Followers;
import io.opid.model.Success;
import io.opid.model.User;
import io.opid.network.misc.JacksonRequest;

public class FollowingAdapter extends PaginatedListAdapter<Follower, Followers> {

    public FollowingAdapter(Fragment fragment, Activity activity) {
        super(Followers.class, fragment, activity, R.layout.row_following);

        loadMoreData();
    }

    @Override
    public View createView(int position, View view, final Follower user) {
        TextView nameLabel = (TextView) view.findViewById(R.id.text_name);
        Button followButton = (Button) view.findViewById(R.id.add_button);

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unfollowUser(user);
            }
        });
        nameLabel.setText(user.getName());
        return view;
    }

    @Override
    protected String getURL() {
        return "/api/following/";
    }

    private void unfollowUser(final Follower user) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", OpidioApplication.getInstance().getAccessToken());
        params.put("user_to_unfollow", String.valueOf(user.getId()));
        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.POST, Config.HUB_SERVER + "/api/unfollow", params, Success.class, new HashMap<String, String>(),
                new Response.Listener<Success>() {
                    @Override
                    public void onResponse(Success response) {
                        if (response.getSuccess()) {
                            Toast.makeText(getActivity(), "You are no longer following " + user.getName(), Toast.LENGTH_SHORT).show();
                            ((UserUnfollowedEvent) getFragment()).userUnfollowed();
                        }
                        else
                            Toast.makeText(getActivity(), "Could not unfollow user", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Could not unfollow user", Toast.LENGTH_SHORT).show();
                    }
                }));

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

    public interface UserUnfollowedEvent {
        public abstract void userUnfollowed();
    }


}
