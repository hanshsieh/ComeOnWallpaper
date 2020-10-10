package org.comeonwallpaper.conf;

import javax.validation.constraints.NotNull;

public class ScheduleConf {
  @NotNull
  public Long intervalMs;
}
