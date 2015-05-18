package io.opid;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import io.opid.model.Video;
import io.opid.model.Videos;

public class VideoListAdapter extends PaginatedListAdapter<Video, Videos> {

    public VideoListAdapter(Fragment fragment, Activity activity) {
        super(Videos.class, fragment, activity);
        loadMoreData();
    }

    @Override
    public View createView(int position, View view, Video video) {
        TextView videoTitle = (TextView) view.findViewById(R.id.video_title);
        TextView channelName = (TextView) view.findViewById(R.id.channel_name);
        NetworkImageView thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);

        videoTitle.setText(video.getVideo());
        channelName.setText(video.getChannel_name());
        thumbnail.setImageUrl(video.getUrl() + "/thumbnail.png", OpidioApplication.getInstance().getImageLoader());

        return view;
    }

    @Override
    protected String getURL() {
        return "/api/videos/";
    }

    @Override
    protected void handleResponse(Videos response) {
        addItems(response.getVideos());
        setCurrentPage(response.getPage());
        setMaxPages(response.getTotal_pages());
    }

    @Override
    protected int loadNext() {
        return 7;
    }
}
