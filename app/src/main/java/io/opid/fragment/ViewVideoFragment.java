package io.opid.fragment;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.opid.Config;
import io.opid.OpidioApplication;
import io.opid.R;
import io.opid.model.Channel;
import io.opid.model.VideoInfo;
import io.opid.network.misc.JacksonRequest;
import io.opid.util.ISO8601;

public class ViewVideoFragment extends Fragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {

    private static final String ARG_URL = "url";
    private static final String ARG_CHANNEL_ID = "channelId";
    private String url;
    private int channelId;
    private TextView channelName;
    private TextView videoTitle;
    private TextView hostedBy;
    private TextView uplodateDate;
    private TextView likes;
    private Button shareButton;
    private Button likeButton;
    private VideoView videoView;

    public ViewVideoFragment() {
    }

    public static ViewVideoFragment newInstance(int channelId, String url) {
        ViewVideoFragment viewVideoFragment = new ViewVideoFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_CHANNEL_ID, channelId);
        args.putString(ARG_URL, url);
        viewVideoFragment.setArguments(args);

        return viewVideoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(ARG_URL);
        channelId = getArguments().getInt(ARG_CHANNEL_ID);

        getVideoData();
    }

    private void getVideoData() {
        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.GET, url + "/video.json", VideoInfo.class,
                new Response.Listener<VideoInfo>() {
                    @Override
                    public void onResponse(VideoInfo response) {
                        updateVideoInfo(response);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Could not get video info", Toast.LENGTH_SHORT).show();
                    }
                }));

        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.GET, Config.HUB_SERVER + "/api/channel/" + channelId, Channel.class,
                new Response.Listener<Channel>() {
                    @Override
                    public void onResponse(Channel response) {
                        updateChannelInfo(response);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Could not get channel info", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void updateVideoInfo(VideoInfo videoInfo) {
        videoTitle.setText(videoInfo.getName());
        try {
            Calendar calendar = ISO8601.toCalendar(videoInfo.getUpload_date());
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            format.setTimeZone(calendar.getTimeZone());
            uplodateDate.setText(format.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Could not parse date", Toast.LENGTH_SHORT).show();
        }

    }


    private void updateChannelInfo(Channel channel) {
        hostedBy.setText(channel.getHosted_by());
        channelName.setText(channel.getChannel());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_video, container, false);
        channelName = (TextView) view.findViewById(R.id.channel_name);
        videoTitle = (TextView) view.findViewById(R.id.video_title);
        hostedBy = (TextView) view.findViewById(R.id.hosted_by);
        uplodateDate = (TextView) view.findViewById(R.id.upload_date);
        likes = (TextView) view.findViewById(R.id.likes);
        likeButton = (Button) view.findViewById(R.id.button_like);
        shareButton = (Button) view.findViewById(R.id.button_share);
        videoView = (VideoView) view.findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);
        loadVideo();
        return view;
    }

    private void loadVideo() {
        videoView.setVideoURI(Uri.parse(url + "/1080p.mp4"));
        videoView.start();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnVideoSizeChangedListener(this);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
        MediaController controller = new MediaController(getActivity());
        videoView.setMediaController(controller);
        controller.setAnchorView(videoView);
    }
}
