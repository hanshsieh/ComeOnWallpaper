package org.comeonwallpaper.imgasset;

import java.io.Closeable;
import java.io.File;

public interface ImgAsset extends Closeable {
    File asFile();
}
