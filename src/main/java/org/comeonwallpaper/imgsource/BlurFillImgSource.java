package org.comeonwallpaper.imgsource;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.NoSuchElementException;

public class BlurFillImgSource implements ImgSource {

    private final ImgSource delegate;

    public BlurFillImgSource(@NonNull ImgSource delegate) {
        this.delegate = delegate;
    }

    @NonNull
    @Override
    public Planar<GrayU8> next(@NonNull ImgPrefs prefs) throws NoSuchElementException, IOException {
        // TODO
        return null;
    }

    @Override
    public boolean hasNext() throws IOException {
        return delegate.hasNext();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
