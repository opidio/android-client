package io.opid.network.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import io.opid.OpidioApplication;
import io.opid.R;
import io.opid.model.Comment;
import io.opid.model.Comments;
import io.opid.model.Follower;
import io.opid.model.Followers;

public class CommentsAdapter extends PaginatedListAdapter<Comment, Comments> {

    private int video;

    public CommentsAdapter(Fragment fragment, Activity activity, int video) {
        super(Comments.class, fragment, activity, R.layout.row_comment);
        this.video = video;

        loadMoreData();
    }

    @Override
    public View createView(int position, View view, final Comment comment) {
        TextView textName = (TextView) view.findViewById(R.id.text_name);
        TextView textComment = (TextView) view.findViewById(R.id.text_comment);

        textName.setText(comment.getUser());
        textComment.setText(comment.getComment());
        return view;
    }

    @Override
    protected String getURL() {
        return "/api/comments/" + video + "/";
    }

    @Override
    protected void handleResponse(Comments response) {
        addItems(response.getComments());
        setCurrentPage(response.getPage());
        setMaxPages(response.getTotal_pages());
    }

    @Override
    protected int loadNext() {
        return 7;
    }


}
