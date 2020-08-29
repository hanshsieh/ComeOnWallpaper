package org.comeonwallpaper.ui.tray;

import java.util.EventListener;

public interface OpenViewListener extends EventListener {
    void onOpen(ViewType viewType);
}
