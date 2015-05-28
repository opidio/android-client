package io.opid.fragment;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import io.opid.Config;
import io.opid.OpidioApplication;
import io.opid.R;
import io.opid.model.Channel;
import io.opid.model.Likes;
import io.opid.model.Liking;
import io.opid.model.VideoInfo;
import io.opid.network.adapter.CommentsAdapter;
import io.opid.network.adapter.PaginatedListAdapter;
import io.opid.network.misc.JacksonRequest;
import io.opid.util.ISO8601;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ViewVideoFragment extends Fragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, View.OnClickListener, PaginatedListAdapter.AdapterStatusChanged {

    private static final String ARG_URL = "url";
    private static final String ARG_CHANNEL_ID = "channelId";
    private static final String ARG_VIDEO_ID = "videoId";
    private static final int REQUEST_COMMENT = 0;
    private String url;
    private int channelId;
    private int videoId;
    private TextView channelName;
    private TextView videoTitle;
    private TextView hostedBy;
    private TextView uplodateDate;
    private TextView likes;
    private Button shareButton;
    private Button likeButton;
    private VideoView videoView;
    private ListView listComments;

    public ViewVideoFragment() {
    }

    public static ViewVideoFragment newInstance(int channelId, int videoId, String url) {
        ViewVideoFragment viewVideoFragment = new ViewVideoFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_CHANNEL_ID, channelId);
        args.putInt(ARG_VIDEO_ID, videoId);
        args.putString(ARG_URL, url);
        viewVideoFragment.setArguments(args);

        return viewVideoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(ARG_URL);
        videoId = getArguments().getInt(ARG_VIDEO_ID);
        channelId = getArguments().getInt(ARG_CHANNEL_ID);

        getVideoData();
        getLikes();
        getChannelInfo();
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
    }

    private void getChannelInfo() {
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

    private void getLikes() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Token", OpidioApplication.getInstance().getAccessToken());
        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.GET, Config.HUB_SERVER + "/api/likes/" + videoId, new HashMap<String, String>(), Likes.class, headers,
                new Response.Listener<Likes>() {
                    @Override
                    public void onResponse(Likes response) {
                        updateLikes(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Could not get video info", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void updateLikes(Likes likes) {
        String text;
        if (likes.getI_am_liking()) {
            text = "You and " + (likes.getLikes() - 1) + " others like this";
        } else {
            text = likes.getLikes() + " people like this";
        }
        this.likes.setText(text);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COMMENT) {
            if (resultCode == CommentDialogFragment.RESULT_CODE_FAIL) {
                Toast.makeText(getActivity(), "Could not send comment", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Comment sent", Toast.LENGTH_SHORT).show();
                updateComments();
            }
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
        shareButton = (Button) view.findViewById(R.id.button_comment);
        videoView = (VideoView) view.findViewById(R.id.video_view);
        listComments = (ListView) view.findViewById(R.id.list_comments);

        loadVideo();
        likeButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        updateComments();
        return view;
    }

    private void updateComments() {
        listComments.setAdapter(new CommentsAdapter(this, getActivity(), videoId));
    }

    private void loadVideo() {
        videoView.setOnPreparedListener(this);
        videoView.setVideoPath(url + "/1080p.mp4");
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_like) {
            likeVideo();
        } else if (view.getId() == R.id.button_comment) {
            commentVideo();
        }
    }

    private void commentVideo() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        CommentDialogFragment commentDialog = CommentDialogFragment.newInstance(videoId);
        commentDialog.setTargetFragment(this, REQUEST_COMMENT);
        commentDialog.show(fm, "fragment_comment");
    }

    private void likeVideo() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Token", OpidioApplication.getInstance().getAccessToken());
        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.GET, Config.HUB_SERVER + "/api/toggle-like/" + videoId, new HashMap<String, String>(), Liking.class, headers,
                new Response.Listener<Liking>() {
                    @Override
                    public void onResponse(Liking liking) {
                        String message;
                        if (liking.getLiking()) {
                            message = "You are now liking this video";
                        } else {
                            message = "You are no longer liking this video";
                        }
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        getLikes();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Could not like the video", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public void statusChanged(boolean loading) {

    }
}
