package org.comeonwallpaper.imgsource;

import org.comeonwallpaper.imgasset.FileImgAsset;
import org.comeonwallpaper.imgasset.ImgAsset;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DirImgSource implements ImgSource {
    private final File dir;
    private final DirectoryStream<Path> pathStream;
    private Iterator<Path> pathStreamItr;
    private File nextFile = null;

    public DirImgSource(File dir) throws IOException {
        this.dir = dir;
        Path path = Paths.get(dir.getAbsolutePath());
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{jpg,jpeg,bmp,gif}");
        this.pathStream = Files.newDirectoryStream(path, entry -> {
            if (!Files.isRegularFile(entry)) {
                return false;
            }
            return pathMatcher.matches(entry);
        });
        this.pathStreamItr = pathStream.iterator();
    }

    @Override
    public ImgAsset next() throws NoSuchElementException {
        tryNextPath();
        if (nextFile == null) {
            throw new NoSuchElementException("No images under directory " + dir.getAbsolutePath());
        }
        FileImgAsset asset = new FileImgAsset(nextFile, false);
        nextFile = null;
        return asset;
    }

    @Override
    public boolean hasNext() {
        tryNextPath();
        return nextFile != null;
    }

    private void tryNextPath() {
        if (nextFile != null) {
            return;
        }
        // If there's no more paths, rewind
        if (!pathStreamItr.hasNext()) {
            pathStreamItr = pathStream.iterator();
        }
        if (pathStreamItr.hasNext()) {
            nextFile = pathStreamItr.next().toFile();
        }
    }

    @Override
    public void close() throws IOException {
        pathStream.close();
    }
}
