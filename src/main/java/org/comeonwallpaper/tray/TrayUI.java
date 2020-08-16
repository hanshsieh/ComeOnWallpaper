package org.comeonwallpaper.tray;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TrayUI {
    private TrayIcon trayIcon;
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

    private TrayIcon buildTrayIcon(@NonNull Dimension iconSize) throws IOException {
        try {
            final PopupMenu popup = new PopupMenu();
            // TODO Hey! Make my own icon!
            Image iconImg = ImageIO.read(new URL("https://cdn2.iconfinder.com/data/icons/spring-30/30/Bird-64.png"));
            Image scaledIconImg = iconImg.getScaledInstance(iconSize.width, iconSize.height, Image.SCALE_DEFAULT);
            final TrayIcon trayIcon = new TrayIcon(scaledIconImg);

            MenuItem settings = new MenuItem("Settings");
            MenuItem aboutItem = new MenuItem("About");
            MenuItem exitItem = new MenuItem("Exit");

            //Add components to pop-up menu
            popup.add(settings);
            popup.add(aboutItem);
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            return trayIcon;
        } catch (MalformedURLException ex) {
            throw new IOException(ex);
        }
    }

    public void hide() throws UnsupportedOperationException, IOException {
        if (trayIcon == null) {
            return;
        }
        final SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
    }
}
