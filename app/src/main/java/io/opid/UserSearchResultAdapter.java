package io.opid;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
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
    public View createView(int position, View view, User video) {
        TextView nameLabel = (TextView) view.findViewById(R.id.text_name);
        nameLabel.setText(video.getName());
        return view;
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
