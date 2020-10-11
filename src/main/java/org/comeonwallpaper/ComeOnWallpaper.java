package org.comeonwallpaper;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.conf.Config;
import org.comeonwallpaper.conf.ConfigLoader;
import org.comeonwallpaper.imgsource.ImgSource;
import org.comeonwallpaper.imgsource.factory.ImgSourceFactory;
import org.comeonwallpaper.monitor.MonitorService;
import org.comeonwallpaper.ui.setting.SettingUI;
import org.comeonwallpaper.ui.setting.State;
import org.comeonwallpaper.ui.tray.TrayUI;
import org.comeonwallpaper.ui.tray.ViewType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ComeOnWallpaper implements Closeable {
  private static final Logger logger = LoggerFactory.getLogger(ComeOnWallpaper.class);
  private final TrayUI trayUI;
  private SettingUI settingUI = null;
  private final ConfigLoader configLoader = new ConfigLoader();
  private final WallpaperManager wallpaperManager = new WallpaperManager();
  private final MonitorService monitorService = new MonitorService();
  private WallpaperRenderer wallpaperRenderer = null;
  private WallpaperScheduler wallpaperScheduler = null;
  private Config config = null;

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
    try {
      loadConfig();
      logger.info("Config loaded");
    } catch (FileNotFoundException ex) {
      logger.info("Config file is not found, opening settings view: {}", ex.getMessage());
      showSettingUI();
    } catch (Exception ex) {
      logger.error("Fails to open config: ", ex);
      showSettingUI();
    }
  }

  private void loadConfig() throws Exception {
    config = configLoader.load();
    ImgSourceFactory factory = ImgSourceFactory.fromConfig(config.source);
    ImgSource imgSource = factory.create();
    closeWallpaperRenderer();
    closeWallpaperScheduler();
    wallpaperRenderer = new WallpaperRenderer(imgSource, monitorService, wallpaperManager);
    wallpaperScheduler = new WallpaperScheduler(wallpaperRenderer);
    wallpaperScheduler.schedule(config.schedule.intervalMs, TimeUnit.MILLISECONDS);
  }

  private void closeWallpaperRenderer() {
    if (wallpaperRenderer != null) {
      wallpaperRenderer.close();
      wallpaperRenderer = null;
    }
  }

  private void closeWallpaperScheduler() throws IOException {
    if (wallpaperScheduler != null) {
      wallpaperScheduler.close();
      wallpaperScheduler = null;
    }
  }

  public void close() throws IOException {
    closeWallpaperRenderer();
    closeWallpaperScheduler();
  }

  public static void main(String[] args) throws Exception {
    try {
      ComeOnWallpaper app = new ComeOnWallpaper();
      app.run();
    } catch (Throwable throwable) {
      logger.error("Exception is thrown: ", throwable);
      System.exit(1);
    }
  }
}
