package org.comeonwallpaper;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.ui.setting.SettingUI;
import org.comeonwallpaper.ui.setting.State;
import org.comeonwallpaper.ui.tray.TrayUI;
import org.comeonwallpaper.ui.tray.ViewType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class ComeOnWallpaper {
    private static final Logger logger = LoggerFactory.getLogger(ComeOnWallpaper.class);
    private final TrayUI trayUI;
    private SettingUI settingUI = null;
    public ComeOnWallpaper() {
        trayUI = new TrayUI();
        trayUI.addOpenViewListener(this::openView);
    }
    private void openView(@NonNull ViewType viewType) {
        if (viewType == ViewType.SETTINGS) {
            showSettingUI();
        } else {
            throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }
    private void showSettingUI() {
        if (settingUI != null) {
            return;
        }
        settingUI = new SettingUI();
        settingUI.addStatusChangeListener(this::onSettingUIStatusChanged);
        settingUI.addOnSaveListener(this::onSettingUpdated);
        settingUI.show();
    }
    private void onSettingUpdated() {
        // TODO
    }
    private synchronized void onSettingUIStatusChanged(State oldState, State newState) {
        if (newState == State.CLOSE) {
            settingUI = null;
        }
    }
    public void run() {
        SwingUtilities.invokeLater(() -> {
            try {
                trayUI.show();
            } catch (Exception ex) {
                logger.error("Failed to show tray UI: ", ex);
            }
        });
    }
    public static void main(String[] args) throws Exception  {
        try {
            ComeOnWallpaper app = new ComeOnWallpaper();
            app.run();
        } catch (Throwable throwable) {
            logger.error("Exception is thrown: ", throwable);
            System.exit(1);
        }
    }
}
