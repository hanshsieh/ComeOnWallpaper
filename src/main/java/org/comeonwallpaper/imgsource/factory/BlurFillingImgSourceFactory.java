package org.comeonwallpaper.imgsource.factory;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.conf.DataSource;
import org.comeonwallpaper.imgsource.BlurFillImgSource;
import org.comeonwallpaper.imgsource.ImgSource;
import org.comeonwallpaper.imgsource.RandomImgSource;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class BlurFillingImgSourceFactory extends ImgSourceFactory {
  private static class Settings {
    @NotNull
    public Integer radius;
  }

  public BlurFillingImgSourceFactory(@NonNull DataSource settings, @NonNull List<ImgSourceFactory> dependencies) {
    super(settings, dependencies);
  }

  @Override
  public @NonNull ImgSource create() throws Exception {
    Validate.isTrue(dependencies.size() == 1,
        "Only one dependency is allowed for " + getClass().getName());
    Settings parsedSettings = settings.asType(Settings.class);
    return new BlurFillImgSource(dependencies.get(0).create(), parsedSettings.radius);
  }
}
