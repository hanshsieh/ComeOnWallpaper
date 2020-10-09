package org.comeonwallpaper.conf;

import com.google.gson.JsonElement;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SourceConf {
  @NotNull
  public String factoryClass;
  @NotNull
  public JsonElement settings;
  @NotNull
  public List<SourceConf> dependencies;
}
