package org.comeonwallpaper.imgsource.factory;

import com.google.gson.JsonElement;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.conf.DataSource;
import org.comeonwallpaper.imgsource.ImgSource;
import org.comeonwallpaper.imgsource.RandomImgSource;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class RandomImgSourceFactory extends ImgSourceFactory {
  private static class Settings {
    @NotNull
    public int[] weights;
  }

  public RandomImgSourceFactory(@NonNull DataSource settings, @NonNull List<ImgSourceFactory> dependencies) {
    super(settings, dependencies);
  }

  @Override
  public @NonNull ImgSource create() throws Exception {
    Settings parsedSettings = settings.asType(Settings.class);
    if (parsedSettings.weights.length != dependencies.size()) {
      throw new IllegalArgumentException("The number of weights doesn't match the number of dependencies");
    }
    List<RandomImgSource.Candidate> candidates = new ArrayList<>();
    for (int i = 0; i < parsedSettings.weights.length; ++i) {
      ImgSource dependSource = dependencies.get(i).create();
      int weight = parsedSettings.weights[i];
      candidates.add(new RandomImgSource.Candidate(dependSource, weight));
    }
    return new RandomImgSource(candidates);
  }
}
