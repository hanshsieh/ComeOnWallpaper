package org.comeonwallpaper.imgsource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.imgasset.ImgAsset;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.NoSuchElementException;

public interface ImgSource extends Closeable {
    @Nonnull
    Image next(@NonNull ImgPrefs prefs) throws NoSuchElementException, IOException;
    boolean hasNext() throws IOException;
}
