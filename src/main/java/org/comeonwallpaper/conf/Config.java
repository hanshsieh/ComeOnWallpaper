package org.comeonwallpaper.conf;

import javax.validation.constraints.NotNull;

public class Config {
  @NotNull
  public SourceConf source;
  @NotNull
  public ScheduleConf schedule;
}
