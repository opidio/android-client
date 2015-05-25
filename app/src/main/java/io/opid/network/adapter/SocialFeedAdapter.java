package io.opid.network.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import io.opid.OpidioApplication;
import io.opid.R;
import io.opid.model.Feed;
import io.opid.model.Item;
import io.opid.model.VideoInf;
import io.opid.model.VideoInf_;
import io.opid.util.ISO8601;
import io.opid.view.AspectNetImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class SocialFeedAdapter extends PaginatedListAdapter<Item, Feed> {

    private String videoUrl;
    private int channelId;
    private int videoId;

    public SocialFeedAdapter(Fragment fragment, Activity activity) {
        super(Feed.class, fragment, activity, R.layout.row_feed_item);
        loadMoreData();
    }

    @Override
    protected View getConvertView(View convertView, LayoutInflater inflater) {
        // Never recycle views since there are several different types
        return inflater.inflate(getRowResource(), null);
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
        View row = getInflater().inflate(layout, frame, true);
        if (element.getType().equals("like")) {
            loadLikeRow(row, element);
        } else {
            loadCommentRow(row, element);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ClickListener) getFragment()).videoClick(channelId, videoId, videoUrl);
            }
        });
        return view;
    }

    private void loadCommentRow(View view, Item element) {
        VideoInf videoInf = element.getComment().getVideoInf();
        channelId = videoInf.getChannel_id();
        videoId = videoInf.getId();
        videoUrl = videoInf.getUrl();

        TextView username = (TextView) view.findViewById(R.id.username);
        TextView videoTitle = (TextView) view.findViewById(R.id.video_title);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView comment = (TextView) view.findViewById(R.id.text_comment);
        AspectNetImageView thumbnail = (AspectNetImageView) view.findViewById(R.id.thumbnail);

        username.setText(element.getUsername());
        videoTitle.setText(videoInf.getVideo());
        date.setText(getDate(element.getDate()));
        comment.setText(element.getComment().getText());
        thumbnail.setImageUrl(videoInf.getUrl() + "/1080p.png", OpidioApplication.getInstance().getImageLoader());
    }

    private void loadLikeRow(View view, Item element) {
        VideoInf_ video = element.getVideoInf();
        channelId = video.getChannel_id();
        videoId = video.getId();
        videoUrl = video.getUrl();

        TextView username = (TextView) view.findViewById(R.id.username);
        TextView videoTitle = (TextView) view.findViewById(R.id.video_title);
        TextView date = (TextView) view.findViewById(R.id.date);
        AspectNetImageView thumbnail = (AspectNetImageView) view.findViewById(R.id.thumbnail);

        username.setText(element.getUsername());
        videoTitle.setText(video.getVideo());
        date.setText(getDate(element.getDate()));
        thumbnail.setImageUrl(video.getUrl() + "/1080p.png", OpidioApplication.getInstance().getImageLoader());

    }

    @Override
    protected String getURL() {
        return "/api/feed/";
    }

    private String getDate(String date) {
        try {
            Calendar calendar = ISO8601.toCalendar(date + "+00:00");
            DateFormat format = SimpleDateFormat.getDateTimeInstance();
            format.setTimeZone(calendar.getTimeZone());
            return format.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Could not parse date", Toast.LENGTH_SHORT).show();
        }
        return "Unknown";
    }

    @Override
    public Map<String, String> getHeaders(Map<String, String> headers) {
        headers.put("Access-Token", OpidioApplication.getInstance().getAccessToken());
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
