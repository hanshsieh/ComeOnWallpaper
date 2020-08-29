package org.comeonwallpaper.ui.tray;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.event.EventEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

public class TrayUI {
    private static final Logger logger = LoggerFactory.getLogger(TrayUI.class);
    private TrayIcon trayIcon;
    private final EventEmitter<OpenViewListener> openViewEventEmitter = new EventEmitter<>();
    public void show() throws UnsupportedOperationException, IOException {
        if (trayIcon != null) {
            return;
        }
        final SystemTray tray = SystemTray.getSystemTray();
        Dimension iconSize = tray.getTrayIconSize();
        trayIcon = buildTrayIcon(iconSize);
        try {
            tray.add(trayIcon);
        } catch (AWTException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public void addOpenViewListener(@NonNull OpenViewListener listener) {
        openViewEventEmitter.addListener(listener);
    }

    public void removeOpenViewListener(@NonNull OpenViewListener listener) {
        openViewEventEmitter.removeListener(listener);
    }

    private TrayIcon buildTrayIcon(@NonNull Dimension iconSize) throws IOException {
        final PopupMenu popup = new PopupMenu();
        Image iconImage = loadIconImage(iconSize);
        final TrayIcon trayIcon = new TrayIcon(iconImage);
        MenuItem settings = new MenuItem("Settings");
        settings.addActionListener(this::onSettingsAction);
        MenuItem aboutItem = new MenuItem("About");
        MenuItem exitItem = new MenuItem("Exit");
        popup.add(settings);
        popup.add(aboutItem);
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);
        return trayIcon;
    }

    private void onSettingsAction(@NonNull ActionEvent event) {
        openViewEventEmitter.emit((listener) -> listener.onOpen(ViewType.SETTINGS));
    }

    private Image loadIconImage(@NonNull Dimension iconSize) throws IOException {
        // TODO Hey! Make my own icon!
        Image iconImg = ImageIO.read(new URL("https://cdn2.iconfinder.com/data/icons/spring-30/30/Bird-64.png"));
        return iconImg.getScaledInstance(iconSize.width, iconSize.height, Image.SCALE_DEFAULT);
    }

    public void dispose() throws UnsupportedOperationException {
        if (trayIcon == null) {
            return;
        }
        final SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
    }
}
