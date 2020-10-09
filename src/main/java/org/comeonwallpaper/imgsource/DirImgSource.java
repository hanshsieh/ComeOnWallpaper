package org.comeonwallpaper.imgsource;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DirImgSource implements ImgSource {
  private final File dir;
  private DirectoryStream<Path> pathStream;
  private Iterator<Path> pathStreamItr;
  private File nextFile = null;

  public DirImgSource(File dir) throws IOException {
    this.dir = dir;
    openNewPathStream();
  }

  private void openNewPathStream() throws IOException {
    if (pathStream != null) {
      pathStream.close();
    }
    Path path = Paths.get(dir.getAbsolutePath());
    PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.{jpg,jpeg,png,bmp,gif}");
    pathStream = Files.newDirectoryStream(path, entry -> {
      if (!Files.isRegularFile(entry)) {
        return false;
      }
      return pathMatcher.matches(entry);
    });
    pathStreamItr = pathStream.iterator();
  }

  @Nonnull
  @Override
  public Planar<GrayU8> next(@NonNull ImgPrefs prefs) throws NoSuchElementException, IOException {
    tryNextPath();
    if (nextFile == null) {
      throw new NoSuchElementException("No images under directory " + dir.getAbsolutePath());
    }
    Planar<GrayU8> image = UtilImageIO.loadImage(nextFile, true, ImageType.PL_U8);
    image.setNumberOfBands(ImgPrefs.NUM_BANDS);
    nextFile = null;
    return image;
  }

  @Override
  public boolean hasNext() throws IOException {
    tryNextPath();
    return nextFile != null;
  }

  private void tryNextPath() throws IOException {
    if (nextFile != null) {
      return;
    }
    // If there's no more paths, rewind
    if (!pathStreamItr.hasNext()) {
      openNewPathStream();
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
