package org.comeonwallpaper.imgsource;

import org.comeonwallpaper.imgasset.ImgAsset;

import java.io.Closeable;
import java.io.IOException;
import java.util.NoSuchElementException;

public interface ImgSource extends Closeable {
    ImgAsset next() throws NoSuchElementException, IOException;
    boolean hasNext() throws IOException;
}
