package io.opid.opidio.fragment;

import android.support.v4.app.Fragment;
import io.opid.opidio.activity.MainActivity;

/**
 * A base fragment used by all main fragments.
 */
public abstract class OpidioFragment extends Fragment {
    public abstract String getName();

    @Override
    public void onStart() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getName());
        super.onStart();
    }
}
