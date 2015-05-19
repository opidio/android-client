package io.opid.network.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.opid.R;
import io.opid.model.User;
import io.opid.model.Users;

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

    private void followUser(User user) {

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
