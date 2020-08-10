package org.comeonwallpaper.imgsource;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.NoSuchElementException;

public interface ImgSource extends Closeable {
    @NonNull
    Planar<GrayU8> next(@NonNull ImgPrefs prefs) throws NoSuchElementException, IOException;
    boolean hasNext() throws IOException;
}
