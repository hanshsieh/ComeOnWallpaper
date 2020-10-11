package org.comeonwallpaper.imgsource.factory;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.conf.DataSource;
import org.comeonwallpaper.imgsource.DirImgSource;
import org.comeonwallpaper.imgsource.ImgSource;
import org.comeonwallpaper.imgsource.RandomImgSource;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirImgSourceFactory extends ImgSourceFactory {
  private static class Settings {
    @NotNull
    public String path;
  }

  public DirImgSourceFactory(@NonNull DataSource settings, @NonNull List<ImgSourceFactory> dependencies) {
    super(settings, dependencies);
  }

  @Override
  public @NonNull ImgSource create() throws Exception {
    Validate.isTrue(dependencies.isEmpty(), getClass().getName() + " has no dependencies");
    Settings parsedSettings = settings.asType(Settings.class);
    return new DirImgSource(new File(parsedSettings.path));
  }
}
