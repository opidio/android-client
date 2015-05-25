package io.opid.network.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import io.opid.OpidioApplication;
import io.opid.R;
import io.opid.model.Feed;
import io.opid.model.Item;

import java.util.Map;

public class SocialFeedAdapter extends PaginatedListAdapter<Item, Feed> {

    public SocialFeedAdapter(Fragment fragment, Activity activity) {
        super(Feed.class, fragment, activity, R.layout.row_feed_item);
        loadMoreData();
    }

    @Override
    public View createView(int position, View view, Item element) {
        FrameLayout frame = (FrameLayout) view.findViewById(R.id.frame);
        int layout;
        if (element.getType().equals("like")) {
            layout = R.layout.row_feed_like;
        } else {
            layout = R.layout.row_feed_comment;
        }
        View rowComment = getInflater().inflate(layout, frame, true);
        return view;
    }

    /*
        @Override
        public View createView(int position, View view, final Item video) {
            TextView videoTitle = (TextView) view.findViewById(R.id.video_title);
            TextView channelName = (TextView) view.findViewById(R.id.channel_name);
            NetworkImageView thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);

            /*
            videoTitle.setText(video.get);
            channelName.setText(video.getChannel_name());
            thumbnail.setImageUrl(video.getUrl() + "/1080p.png", OpidioApplication.getInstance().getImageLoader());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ClickListener) getFragment()).videoClick(video.getChannel_id(), video.getVideo_id(), video.getUrl());
                }
            });
    *//*
        return view;
    }
*/
    @Override
    protected String getURL() {
        return "/api/feed/";
    }

    @Override
    public Map<String, String> getHeaders(Map<String, String> headers) {
        headers.put("access_token", OpidioApplication.getInstance().getAccessToken());
        return super.getHeaders(headers);
    }

    @Override
    protected void handleResponse(Feed response) {
        addItems(response.getItems());
        setCurrentPage(response.getPage());
        setMaxPages(response.getTotal_pages());
    }

    @Override
    protected int loadNext() {
        return 7;
    }

    public interface ClickListener {
        public abstract void videoClick(int channelId, int videoId, String url);
    }
}
