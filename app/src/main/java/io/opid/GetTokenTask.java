package io.opid;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import io.opid.model.Login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetTokenTask extends AsyncTask<Void, Void, String> {
    Activity mActivity;
    String mScope;
    String mEmail;
    private Bundle appActivities;

    GetTokenTask(Activity activity, String name, String scope, Bundle appActivities) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = name;
        this.appActivities = appActivities;
    }
    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            mActivity.startActivityForResult(userRecoverableException.getIntent(), 2000);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "Could not sign in (A)", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (GoogleAuthException fatalException) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "Could not sign in (B)", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return null;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String token = fetchToken();
            if (token != null) {
                return token;
            }
        } catch (IOException e) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "Could not sign in (C)", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(String token) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", token);
        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.POST, Config.HUB_SERVER + "/auth", params, Login.class,
                new Response.Listener<Login>() {
                    @Override
                    public void onResponse(Login response) {
                        TextView loggedInAs = (TextView) mActivity.findViewById(R.id.logged_in_as);
                        loggedInAs.setText(response.getName());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(mActivity, "Could not authenticate with server", Toast.LENGTH_SHORT).show();
                    }
                }));

    }
}