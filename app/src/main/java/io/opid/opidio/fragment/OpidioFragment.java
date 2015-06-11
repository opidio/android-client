package io.opid.opidio.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import io.opid.opidio.OpidioApplication;
import io.opid.opidio.activity.MainActivity;
import io.opid.opidio.menu.FragmentMenuItem;
import io.opid.opidio.menu.MenuItem;

import java.util.Map;

/**
 * A base fragment used by all main fragments.
 */
public abstract class OpidioFragment extends Fragment {
    public abstract String getName(Context context);

    @Override
    public void onStart() {
        MainActivity activity = (MainActivity) getActivity();
        activity.setTitle(getName(activity));
        Map<Integer, MenuItem> menuItems = OpidioApplication.getInstance().getMenuItems();
        Integer menuIndex = findMenuIndex(menuItems);
        if (menuIndex != null) {
            activity.setMenuIndex(menuIndex);
        }
        super.onStart();
    }

    /**
     * Searches the Map reversely in order to associate a Fragment with an ID
     */
    private Integer findMenuIndex(Map<Integer, MenuItem> menuItems) {
        for (Map.Entry<Integer, MenuItem> item : menuItems.entrySet()) {
            if (item.getValue() instanceof FragmentMenuItem) {
                if (((FragmentMenuItem) item.getValue()).getFragment() == this.getClass()) {
                    return item.getKey();
                }
            }
        }
        return null;
    }
}
