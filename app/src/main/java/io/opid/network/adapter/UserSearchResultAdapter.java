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
import io.opid.model.Login;
import io.opid.model.User;
import io.opid.model.Users;
import io.opid.network.misc.JacksonRequest;

public class UserSearchResultAdapter extends PaginatedListAdapter<User, Users> {

    private final String query;

    public UserSearchResultAdapter(Fragment fragment, Activity activity, String query) {
        super(Users.class, fragment, activity, R.layout.row_user_search);
        this.query = query;

        loadMoreData();
    }

    @Override
    public View createView(int position, View view, final User user) {
        TextView nameLabel = (TextView) view.findViewById(R.id.text_name);
        Button followButton = (Button) view.findViewById(R.id.add_button);

        nameLabel.setText(user.getName());
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followUser(user);
            }
        });
        return view;
    }

    private void followUser(final User user) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", OpidioApplication.getInstance().getAccessToken());
        params.put("user_to_follow", String.valueOf(user.getId()));
        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.POST, Config.HUB_SERVER + "/api/follow", params, Login.class,
                new Response.Listener<Login>() {
                    @Override
                    public void onResponse(Login response) {
                        Toast.makeText(getActivity(), "Now following " + user.getName(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Could not follow user", Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    @Override
    protected String getURL() {
        return "/api/users/" + query + "/";
    }

    @Override
    protected void handleResponse(Users response) {
        addItems(response.getUsers());
        setCurrentPage(response.getPage());
        setMaxPages(response.getTotal_pages());
    }

    @Override
    protected int loadNext() {
        return 7;
    }
}
