package io.opid.opidio.menu;

import io.opid.opidio.activity.MainActivity;

public abstract class MenuItem {
    public abstract String getName();

    public abstract void run(MainActivity mainActivity);
}
