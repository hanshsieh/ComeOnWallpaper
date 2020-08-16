package org.comeonwallpaper.tray;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;

public class TrayUI {
    public void show() throws UnsupportedOperationException, Exception {
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("System tray is not supported");
        }
        final SystemTray tray = SystemTray.getSystemTray();
        final PopupMenu popup = new PopupMenu();
        // TODO Hey! Make my own icon!
        Image iconImg = ImageIO.read(new URL("https://cdn2.iconfinder.com/data/icons/spring-30/30/Bird-64.png"));
        Dimension iconSize = tray.getTrayIconSize();
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
        tray.add(trayIcon);
    }
}
