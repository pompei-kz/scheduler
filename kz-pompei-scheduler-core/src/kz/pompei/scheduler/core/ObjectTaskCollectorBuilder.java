package kz.pompei.scheduler.core;

import kz.pompei.hotconfig.core.ConfigTunnel;
import lombok.NonNull;

public class ObjectTaskCollectorBuilder {
  private          ConfigTunnel tunnel    = null;
  private @NonNull String       extension = ".scheduler";

  /**
   * Set the file extension for the task collector config file.
   *
   * @param extension The file extension to use
   * @return This builder instance for method chaining
   */
  public ObjectTaskCollectorBuilder extension(@NonNull String extension) {
    this.extension = extension;
    return this;
  }

  /**
   * Set the config tunnel for the task collector.
   *
   * @param tunnel The tunnel to use
   * @return This builder instance for method chaining
   */
  public ObjectTaskCollectorBuilder tunnel(ConfigTunnel tunnel) {
    this.tunnel = tunnel;
    return this;
  }

  public ObjectTaskCollector build() {
    if (tunnel == null) {
      throw new NullPointerException("uFUHeQZIt4 :: tunnel cannot be null");
    }
    return new ObjectTaskCollector(tunnel, new ObjectTaskCollector.Def(extension));
  }
}
