package org.comeonwallpaper;

import org.comeonwallpaper.imgsource.BlurFillImgSource;
import org.comeonwallpaper.imgsource.DirImgSource;
import org.comeonwallpaper.imgsource.ImgSource;
import org.comeonwallpaper.monitor.MonitorService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class App
{
    public static void main( String[] args ) throws Exception  {
        ImgSource dirImgSrc = new DirImgSource(new File("/path/to/my/dir"));
        ImgSource scaledImgSrc = new BlurFillImgSource(dirImgSrc, 70);
        MonitorService monitorService = new MonitorService();
        WallpaperManager wallpaperManager = new WallpaperManager();
        WallpaperRenderer renderer = new WallpaperRenderer(scaledImgSrc, monitorService, wallpaperManager);
        WallpaperScheduler scheduler = new WallpaperScheduler(renderer);
        scheduler.schedule(10, TimeUnit.SECONDS);
        Thread.sleep(Long.MAX_VALUE);
    }
}
