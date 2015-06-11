package io.opid.opidio;

import android.app.Application;
import android.content.Intent;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import io.opid.opidio.activity.MainActivity;
import io.opid.opidio.activity.WelcomeActivity;
import io.opid.opidio.fragment.*;
import io.opid.opidio.menu.ActionMenuItem;
import io.opid.opidio.menu.FragmentMenuItem;
import io.opid.opidio.menu.MenuItem;
import io.opid.opidio.network.misc.OpidioImageCache;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpidioApplication extends Application {
    private static OpidioApplication instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private String accessToken;
    private final Map<Integer, MenuItem> menuItems = new LinkedHashMap<>();

    public Map<Integer, MenuItem> getMenuItems() {
        return menuItems;
    }

    public static OpidioApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        menuItems.put(0, new FragmentMenuItem(SocialFeedFragment.class, getString(R.string.social_activity)));
        menuItems.put(1, new FragmentMenuItem(VideoListFragment.class, getString(R.string.all_videos)));
        menuItems.put(2, new FragmentMenuItem(FollowingFragment.class, getString(R.string.following)));
        menuItems.put(3, new FragmentMenuItem(MyFollowersFragment.class, getString(R.string.my_followers)));
        menuItems.put(4, new FragmentMenuItem(SearchUserFragment.class, getString(R.string.search_users)));
        menuItems.put(5, new ActionMenuItem() {
            @Override
            public void run(MainActivity mainActivity) {
                GoogleApiClient googleApiClient = mainActivity.getGoogleApiClient();
                Plus.AccountApi.clearDefaultAccount(googleApiClient);
                googleApiClient.disconnect();

                googleApiClient.connect();
                startActivity(new Intent(mainActivity, WelcomeActivity.class));
            }

            @Override
            public String getName() {
                return getString(R.string.sign_out);
            }
        });
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(getRequestQueue(), new OpidioImageCache());
        }
        return imageLoader;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
