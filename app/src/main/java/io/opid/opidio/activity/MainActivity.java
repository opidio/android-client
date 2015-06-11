package io.opid.opidio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import io.opid.opidio.Config;
import io.opid.opidio.OpidioApplication;
import io.opid.opidio.R;
import io.opid.opidio.fragment.NavigationDrawerFragment;
import io.opid.opidio.fragment.SocialFeedFragment;
import io.opid.opidio.fragment.VideoListFragment;
import io.opid.opidio.fragment.ViewVideoFragment;
import io.opid.opidio.network.misc.GetTokenTask;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, VideoListFragment.OnVideoSelectListener, SocialFeedFragment.OnVideoSelectListener {

    private NavigationDrawerFragment drawerFragment;
    private GoogleApiClient googleApiClient;

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerFragment = (NavigationDrawerFragment)
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
        OpidioApplication.getInstance().getMenuItems().get(position).run(this);
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
    public void onVideoSelect(int channelId, int videoId, String videoUrl) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ViewVideoFragment.newInstance(channelId, videoId, videoUrl))
                .addToBackStack("video")
                .commit();
        getSupportActionBar().setTitle(getString(R.string.video_video));
    }

    public void setMenuIndex(int menuIndex) {
        drawerFragment.setMenuIndex(menuIndex);
    }
}
