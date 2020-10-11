package org.comeonwallpaper.conf;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfigLoader {

  private static final String FILE_PATH = "config.json";

  @NonNull
  public Config load() throws FileNotFoundException, IOException {
    return DataSource.fromFile(new File("config.json")).asType(Config.class);
  }
}
