package kz.pompei.scheduler.core;

import kz.pompei.hotconfig.core.ConfigTunnel;
import lombok.NonNull;

public class ObjectTaskCollector {

  private final @NonNull ConfigTunnel tunnel;

  public ObjectTaskCollector(@NonNull ConfigTunnel tunnel) {
    this.tunnel = tunnel;
  }
}
