package io.opid.opidio.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import io.opid.opidio.Config;
import io.opid.opidio.OpidioApplication;
import io.opid.opidio.R;
import io.opid.opidio.model.Success;
import io.opid.opidio.network.misc.JacksonRequest;

import java.util.HashMap;
import java.util.Map;

public class CommentDialogFragment extends DialogFragment {

    public static final int RESULT_CODE_SUCCESS = 0;
    public static final int RESULT_CODE_FAIL = 1;
    private static final String ARG_VIDEO_ID = "videoId";
    private EditText commentField;
    private int videoId;

    public CommentDialogFragment() {
    }

    public static CommentDialogFragment newInstance(int videoId) {
        CommentDialogFragment commentDialogFragment = new CommentDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_VIDEO_ID, videoId);
        commentDialogFragment.setArguments(args);

        return commentDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoId = getArguments().getInt(ARG_VIDEO_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_comment, null);
        commentField = (EditText) view.findViewById(R.id.editText);
        return builder.setView(view)
                .setPositiveButton(R.string.send_comment, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendComment();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getDialog().cancel();
                    }
                })
                .create();
    }

    private void sendComment() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", OpidioApplication.getInstance().getAccessToken());
        params.put("message", commentField.getText().toString());
        params.put("video", String.valueOf(videoId));
        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.POST, Config.HUB_SERVER + "/api/comment", params, Success.class, new HashMap<String, String>(),
                new Response.Listener<Success>() {
                    @Override
                    public void onResponse(Success response) {
                        getTargetFragment().onActivityResult(0, RESULT_CODE_SUCCESS, new Intent());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        getTargetFragment().onActivityResult(0, RESULT_CODE_FAIL, new Intent());
                    }
                }));
    }
}
