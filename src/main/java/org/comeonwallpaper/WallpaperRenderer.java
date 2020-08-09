package org.comeonwallpaper;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.imgsource.DirImgSource;
import org.comeonwallpaper.imgsource.ImgPrefs;
import org.comeonwallpaper.imgsource.ImgSource;
import org.comeonwallpaper.monitor.Monitor;
import org.comeonwallpaper.monitor.MonitorService;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

public class WallpaperRenderer implements Closeable {
    private final MonitorService monitorService;
    private final WallpaperManager wallpaperManager;
    private final ImgSource source;
    private List<Monitor> monitors;
    private Rectangle canvasArea;
    private BufferedImage canvas;
    private final File outputFile = new File("output.jpg");
    public WallpaperRenderer(
            @NonNull ImgSource source,
            @NonNull MonitorService monitorService,
            @NonNull WallpaperManager wallpaperManager) {
        this.source = source;
        this.monitorService = monitorService;
        this.wallpaperManager = wallpaperManager;
    }

    public void render() throws IOException {
        monitors = monitorService.getMonitors();
        if (monitors.isEmpty()) {
            return;
        }
        calCanvasSize();
        canvas = new BufferedImage(
            canvasArea.width,
            canvasArea.height,
            BufferedImage.TYPE_INT_RGB
        );
        renderForMonitors();
        writeWallpaperFile();
        wallpaperManager.setWallpaper(outputFile, WallpaperManager.DisplayStyle.TILED);
    }

    private void writeWallpaperFile() throws IOException {
        // https://stackoverflow.com/questions/17108234/setting-jpg-compression-level-with-imageio-in-java
        JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(0.85f);
        final ImageWriter writer = getJpegImageWriter();
        // TODO Use a folder appropriate for writing application data
        try (FileImageOutputStream outputStream = new FileImageOutputStream(outputFile)) {
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(canvas, null, null), jpegParams);
            outputStream.flush();
            writer.dispose();
        }
    }

    private ImageWriter getJpegImageWriter() throws IOException {
        Iterator<ImageWriter> writerItr = ImageIO.getImageWritersByFormatName("jpg");
        if (!writerItr.hasNext()) {
            throw new IOException("Failed to find a writer for JPEG");
        }
        return writerItr.next();
    }

    private void renderForMonitors() throws IOException {
        Graphics graphics = canvas.getGraphics();
        try {
            List<Future<Image>> futures = new ArrayList<>();
            for (Monitor monitor : monitors) {
                renderForMonitor(monitor, graphics, futures);
            }
            for (Future<Image> future : futures) {
                future.get();
            }
        } catch (Exception ex) {
            throw new IOException("Failed to render image", ex);
        } finally {
            graphics.dispose();
        }
    }

    private void renderForMonitor(
            Monitor monitor,
            Graphics graphics,
            List<Future<Image>> futures) throws IOException {
        if (!source.hasNext()) {
            return;
        }
        Rectangle workingArea = monitor.getWorkingArea();
        ImgPrefs prefs = new ImgPrefs();
        prefs.setWidth(workingArea.width);
        prefs.setHeight(workingArea.height);
        Image image = source.next(prefs);
        ImgObserverFuture future = new ImgObserverFuture();
        boolean done = graphics.drawImage(
                image,
                workingArea.x - canvasArea.x,
                workingArea.y -canvasArea.y,
                workingArea.width,
                workingArea.height,
                future);
        if (!done) {
            futures.add(future);
        }
    }

    private void calCanvasSize() {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE,
            maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Monitor monitor : monitors) {
            Rectangle displayArea = monitor.getDisplayArea();
            minX = Math.min(minX, displayArea.x);
            maxX = Math.max(maxX, displayArea.x + displayArea.width);
            minY = Math.min(minY, displayArea.y);
            maxY = Math.max(maxY, displayArea.y + displayArea.height);
        }
        canvasArea = new Rectangle(
            minX, minY,
            maxX - minX, maxY - minY
        );
    }

    @Override
    public void close() {
        canvasArea = null;
    }

    public static void main(String[] args) throws Exception {
        WallpaperRenderer renderer = new WallpaperRenderer(
            new DirImgSource(new File("F:\\Users\\someone\\Desktop")),
            new MonitorService(),
            new WallpaperManager()
        );
        renderer.render();
    }
}
