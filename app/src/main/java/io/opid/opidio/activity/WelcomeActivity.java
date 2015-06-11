package io.opid.opidio.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import io.opid.opidio.R;

public class WelcomeActivity extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    private boolean signInClicked;
    private ConnectionResult connectionResult;
    private GoogleApiClient googleApiClient;
    private boolean intentInProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button
                && !googleApiClient.isConnecting()) {
            signInClicked = true;
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        if (connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                startIntentSenderForResult(connectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                intentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Connection failed, either because
        // a) the user is not logged in (In which case show the login button)
        // b) the user clicked the login button, but has to select something
        //    (if so, show the dialog)
        if (!intentInProgress) {
            connectionResult = result;

            if (signInClicked) {
                resolveSignInError();
            } else {
                toggleLoginButton(true);
            }
        }
    }

    /**
     * Toggle between loading and showing login button
     *
     * @param visible true ? login button shown, loading animation hidden
     */
    private void toggleLoginButton(boolean visible) {
        View loginButton = findViewById(R.id.sign_in_button);
        View loginSpinner = findViewById(R.id.login_loading_indicator);
        loginButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        loginSpinner.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // User was successfully connected, send the user to the main activity
        signInClicked = false;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        toggleLoginButton(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            // Probably handling a account selector dialog
            if (responseCode != RESULT_OK) {
                signInClicked = false;
            }

            intentInProgress = false;

            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        googleApiClient.connect();
    }
}
