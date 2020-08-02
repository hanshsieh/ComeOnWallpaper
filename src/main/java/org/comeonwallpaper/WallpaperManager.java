package org.comeonwallpaper;

import org.comeonwallpaper.imgasset.FileImgAsset;
import org.comeonwallpaper.imgasset.ImgAsset;
import org.comeonwallpaper.windows.WinUserLib;
import org.comeonwallpaper.windows.WinUserLibLoader;

import java.io.File;
import java.io.IOException;

public class WallpaperManager {

    private final WinUserLib winUserLib;

    public WallpaperManager() {
        this.winUserLib = WinUserLibLoader.loadInstance();
    }

    public void setWallpaper(ImgAsset image) throws IOException {
        String imagePath = image.asFile().getAbsolutePath();

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

    public static void main(String[] args) throws Exception {
        WallpaperManager mgr = new WallpaperManager();
        mgr.setWallpaper(new FileImgAsset(new File("F:\\Users\\someone\\Desktop\\2016-12-01-875132.jpg"), false));
    }
}
