package io.opid.opidio.menu;

import android.widget.Toast;
import io.opid.opidio.R;
import io.opid.opidio.activity.MainActivity;
import io.opid.opidio.fragment.OpidioFragment;

public class FragmentMenuItem extends MenuItem {
    private Class<? extends OpidioFragment> fragment;
    private String name;

    public FragmentMenuItem(Class<? extends OpidioFragment> fragment, String name) {
        this.fragment = fragment;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void run(MainActivity mainActivity) {
        OpidioFragment fragment = null;
        try {
            fragment = this.fragment.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            Toast.makeText(mainActivity, "Could not create a new fragment", Toast.LENGTH_SHORT).show();
        }
        mainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("topFragment")
                .commit();
    }

    public Class<? extends OpidioFragment> getFragment() {
        return fragment;
    }
}
