package org.comeonwallpaper;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.comeonwallpaper.imgasset.FileImgAsset;
import org.comeonwallpaper.imgasset.ImgAsset;
import org.comeonwallpaper.windows.WinUserLib;
import org.comeonwallpaper.windows.WinUserLibLoader;

import java.io.File;
import java.io.IOException;

public class WallpaperManager {

    public enum DisplayStyle {
        STRETCHED("2", "0"),
        CENTERED("0", "0"),
        TILED("0", "1"),
        FULL_SCREEN("6", "0"),
        FILLED("10", "0"),
        CROSS_SCREEN("22", "0");
        private String style;
        private String tile;
        DisplayStyle(String style, String tile) {
            this.style = style;
            this.tile = tile;
        }
    }

    private final WinUserLib winUserLib;

    public WallpaperManager() {
        this.winUserLib = WinUserLibLoader.loadInstance();
    }

    public void setWallpaper(ImgAsset image, DisplayStyle displayStyle) throws IOException {
        String imagePath = image.asFile().getAbsolutePath();
        applyDisplayStyle(displayStyle);
        boolean success = winUserLib.SystemParametersInfo (
                WinUserLib.SPI_SETDESKWALLPAPER,
                0,
                imagePath,
                WinUserLib.SPIF_UPDATEINIFILE | WinUserLib.SPIF_SENDWININICHANGE
        );
        if (!success) {
            throw new IOException("Fail to set wallpaper to path " + imagePath);
        }
    }

    private void applyDisplayStyle(DisplayStyle style) {
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Desktop",
                "WallpaperStyle",
                style.style);
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Desktop",
                "TileWallpaper",
                style.tile);
        // Some articles say "WallpaperOriginX" and "WallpaperOriginY" controls the position of the
        // wallpaper for "CENTERED" style. But as I tested, it doesn't have effect on Win 10.
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Desktop",
                "WallpaperOriginX",
                0);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Desktop",
                "WallpaperOriginY",
                0);
    }

    public static void main(String[] args) throws Exception {
        WallpaperManager mgr = new WallpaperManager();
        ImgAsset asset = new FileImgAsset(new File("F:\\Users\\someone\\Desktop\\2016-12-01-875132.jpg"), false);
        mgr.setWallpaper(asset, DisplayStyle.CENTERED);
    }
}
