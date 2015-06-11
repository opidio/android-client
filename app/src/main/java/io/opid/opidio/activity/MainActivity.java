package io.opid.opidio.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import io.opid.opidio.Config;
import io.opid.opidio.R;
import io.opid.opidio.fragment.*;
import io.opid.opidio.network.misc.GetTokenTask;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, SearchUserFragment.OnFragmentInteractionListener, VideoListFragment.OnVideoSelectListener, SocialFeedFragment.OnVideoSelectListener {

    private GoogleApiClient googleApiClient;
    private int oldDrawerPosition = -1;

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        drawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                // Social Activity
                navigateFragment(SocialFeedFragment.newInstance(), 0);
                break;
            case 1:
                // All Videos
                navigateFragment(VideoListFragment.newInstance(), 1);
                break;
            case 2:
                // Following
                navigateFragment(FollowingFragment.newInstance(), 2);
                break;
            case 3:
                // My Followers
                navigateFragment(MyFollowersFragment.newInstance(), 3);
                break;
            case 4:
                // Search Users
                navigateFragment(SearchUserFragment.newInstance(), 4);
                break;
            case 5:
                // Sign Out
                Plus.AccountApi.clearDefaultAccount(googleApiClient);
                googleApiClient.disconnect();

                googleApiClient.connect();
                startActivity(new Intent(this, WelcomeActivity.class));
                break;
        }
    }


    private void navigateFragment(OpidioFragment fragment, int drawerPosition) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        if (oldDrawerPosition != -1) {
            transaction.addToBackStack("topFragment");
        }
        transaction.commit();

        oldDrawerPosition = drawerPosition;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        startActivity(new Intent(this, WelcomeActivity.class));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Bundle appActivities = new Bundle();
        appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES, "");
        String scopes = Config.GOOGLE_SCOPES;
        new GetTokenTask(this,
                Plus.AccountApi.getAccountName(googleApiClient),
                scopes).execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onVideoSelect(int channelId, int videoId, String videoUrl) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ViewVideoFragment.newInstance(channelId, videoId, videoUrl))
                .addToBackStack("video")
                .commit();
        getSupportActionBar().setTitle(getString(R.string.video_video));
    }
}
