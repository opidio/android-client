package io.opid.network.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import io.opid.OpidioApplication;
import io.opid.R;
import io.opid.model.Video;
import io.opid.model.Videos;

/**
 * The class responsible for populating the ListView of recently
 * uploaded videos.
 */
public class VideoListAdapter extends PaginatedListAdapter<Video, Videos> {

    public VideoListAdapter(Fragment fragment, Activity activity) {
        super(Videos.class, fragment, activity, R.layout.row_video_list);
        loadMoreData();
    }

    /**
     * Create the view for one element in the ListView
     */
    @Override
    public View createView(int position, View view, final Video video) {
        TextView videoTitle = (TextView) view.findViewById(R.id.video_title);
        TextView channelName = (TextView) view.findViewById(R.id.channel_name);
        NetworkImageView thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);

        videoTitle.setText(video.getVideo());
        channelName.setText(video.getChannel_name());
        thumbnail.setImageUrl(video.getUrl() + "/1080p.png", OpidioApplication.getInstance().getImageLoader());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ClickListener) getFragment()).videoClick(video.getChannel_id(), video.getVideo_id(), video.getUrl());
            }
        });

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

    /**
     * This interface has to be implemented by the fragment that
     * is using this adapter.
     */
    public interface ClickListener {
        public abstract void videoClick(int channelId, int videoId, String url);
    }
}
