package org.comeonwallpaper.imgasset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileImgAsset implements ImgAsset {
    private static final Logger logger = LoggerFactory.getLogger(FileImgAsset.class);
    private final File file;
    private final boolean deleteOnClose;
    public FileImgAsset(File file, boolean deleteOnClose) {
        this.file = file;
        this.deleteOnClose = deleteOnClose;
    }

    public FileImgAsset(File file) {
        this(file, false);
    }

    @Override
    public File asFile() {
        return file;
    }

    @Override
    public void close() {
        if (deleteOnClose) {
            if (!file.delete()) {
                logger.warn("Failed to delete image file: {}", file.getAbsolutePath());
            }
        }
    }
}
