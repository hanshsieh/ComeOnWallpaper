package org.comeonwallpaper;

import java.awt.*;

public class Test {
    public static void main(String[] args) {
        for(GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            printScreenDevice(gd);
        }
    }

    private static void printScreenDevice(GraphicsDevice gd) {
        DisplayMode displayMode = gd.getDisplayMode();
        int width = displayMode.getWidth();
        int height = displayMode.getHeight();
        System.out.printf("screen: width %d, height %d\n", width, height);
    }
}
