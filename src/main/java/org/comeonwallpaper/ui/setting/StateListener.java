package org.comeonwallpaper.ui.setting;

import java.util.EventListener;

public interface StateListener extends EventListener {
    void onChange(State oldState, State newState);
}
