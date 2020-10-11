package org.comeonwallpaper.imgsource;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DirImgSource implements ImgSource {
  private static final Logger logger = LoggerFactory.getLogger(DirImgSource.class);
  public enum Mode {
    ALPHABETICAL,
    RANDOM
  }
  private final File dir;
  private final Mode mode;
  private final Duration refreshInterval;
  private Instant lastRefreshTime = null;
  private Path nextPath = null;
  private List<Path> oriPaths = null;
  private List<Path> paths = null;
  private int nextIndex = 0;

  public DirImgSource(@NonNull File dir, @NonNull Mode mode, @NonNull Duration refreshInterval) throws IOException {
    this.dir = dir;
    this.mode = mode;
    this.refreshInterval = refreshInterval;
  }

  @Nonnull
  @Override
  public Planar<GrayU8> next(@NonNull ImgPrefs prefs) throws NoSuchElementException, IOException {
    tryNextPath();
    if (nextPath == null) {
      throw new NoSuchElementException("No images under directory " + dir.getAbsolutePath());
    }
    Planar<GrayU8> image = UtilImageIO.loadImage(nextPath.toFile(), true, ImageType.PL_U8);
    image.setNumberOfBands(ImgPrefs.NUM_BANDS);
    nextPath = null;
    return image;
  }

  @Override
  public boolean hasNext() throws IOException {
    tryNextPath();
    return nextPath != null;
  }

  private void tryNextPath() throws IOException {
    if (nextPath != null) {
      return;
    }
    tryNextFiles();
    if (nextIndex < paths.size()) {
      nextPath = paths.get(nextIndex);
    }
    nextIndex = (nextIndex + 1) % paths.size();
  }

  private void tryNextFiles() throws IOException {
    if (paths != null && Duration.between(lastRefreshTime, Instant.now()).compareTo(refreshInterval) < 0) {
      return;
    }
    logger.debug("Refreshing file list of directory {}", dir);
    Path path = Paths.get(dir.getAbsolutePath());
    PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.{jpg,jpeg,png,bmp,gif}");
    List<Path> newPaths = Files.list(path)
        .filter(pathMatcher::matches)
        .collect(Collectors.toList());
    lastRefreshTime = Instant.now();
    if (newPaths.equals(oriPaths)) {
      logger.debug("File list is not changed");
      return;
    }
    logger.debug("File list is changed");
    oriPaths = newPaths;
    paths = new ArrayList<>(newPaths);
    if (mode == Mode.ALPHABETICAL) {
      paths.sort(Comparator.naturalOrder());
    } else if (mode == Mode.RANDOM) {
      Collections.shuffle(paths);
    }
    if (nextIndex >= paths.size()) {
      logger.debug("Next file index {} exceed the size of new file list {}. Rewind.", nextIndex, paths.size());
      nextIndex = 0;
    } else {
      logger.debug("Next file index {} is within size of new file list {}. Keep the file index",
          nextIndex, paths.size());
    }
  }

  @Override
  public void close() throws IOException {
  }
}
