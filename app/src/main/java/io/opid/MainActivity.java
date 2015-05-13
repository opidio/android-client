package io.opid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private GoogleApiClient googleApiClient;

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
                .build();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (position == 0) {
            // Social Activity
            fragmentManager.beginTransaction()
                    .replace(R.id.container, VideoListFragment.newInstance(VideoViewType.ACTIONGS_BY_MY_FOLLOWERS))
                    .commit();
            getSupportActionBar().setTitle("Social Activity");
        } else if(position == 5) {
            // Sign Out
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();

            googleApiClient.connect();
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }

}
