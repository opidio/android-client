package io.opid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import io.opid.model.Video;
import io.opid.model.Videos;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends BaseAdapter {
    private List<Video> videoList = new ArrayList<>();
    private Activity activity;
    private LayoutInflater inflater;
    private Boolean loading;
    private int currentPage = 0;
    private int maxPages = 1; // Will be update upon the first request

    public VideoListAdapter(Activity activity) {
        this.activity = activity;
        loadMoreData();
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.video_list_row, null);
        }

        if (closeToEnd(position) && !loading) {
            loadMoreData();
        }

        return createView(position, convertView);
    }

    private View createView(int position, View view) {
        TextView videoTitle = (TextView) view.findViewById(R.id.video_title);
        TextView channelName = (TextView) view.findViewById(R.id.channel_name);
        NetworkImageView thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);

        Video video = videoList.get(position);
        videoTitle.setText(video.getVideo());
        channelName.setText(video.getChannel_name());
        thumbnail.setImageUrl(video.getUrl() + "/thumbnail.png", OpidioApplication.getInstance().getImageLoader());

        return view;
    }

    private void loadMoreData() {
        loading = true;

        int page = currentPage + 1;

        if (page > maxPages) {
            // The end was reached
            return;
        }

        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.GET, Config.HUB_SERVER + "/api/videos/" + page, Videos.class,
                new Response.Listener<Videos>() {
                    @Override
                    public void onResponse(Videos response) {
                        videoList.addAll(response.getVideos());
                        currentPage = response.getPage();
                        maxPages = response.getTotal_pages();
                        notifyDataSetChanged();
                        loading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, "Could not download videos", Toast.LENGTH_SHORT).show();
                        loading = false;
                    }
                }));
    }

    private boolean closeToEnd(int position) {
        return position + 7 > getCount();
    }
}
