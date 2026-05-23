package kz.pompei.scheduler.core;

import java.util.TimeZone;
import kz.pompei.hotconfig.core.ConfigTunnel;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class ObjectTaskCollectorBuilder {
  private           ConfigTunnel tunnel          = null;
  private @NonNull  String       extension       = ".scheduler";
  private @NonNull  String       extensionError  = ".scheduler.err";
  private @Nullable TimeZone     timeZoneDefault = null;

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
   * Set the file extension for an error file in the task collector config file.
   *
   * @param extensionError The error file extension to use
   * @return This builder instance for method chaining
   */
  public ObjectTaskCollectorBuilder extensionError(@NonNull String extensionError) {
    this.extensionError = extensionError;
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


  /**
   * Default time zone for scheduler.
   * <p>
   * This time zone will be selected in the very first line of any configuration file.
   * <p>
   * This field can be null. In this case, the very first line of any configuration
   * file will select the time zone set in the system, i.e., the time zone will be selected using the java.util.TimeZone.getDefault() method.
   * <p>
   * It can be set using the TZ environment variable. For example, TZ=Asia/Almaty
   */
  public ObjectTaskCollectorBuilder timeZoneDefault(@Nullable TimeZone timeZoneDefault) {
    this.timeZoneDefault = timeZoneDefault;
    return this;
  }

  public ObjectTaskCollector build() {
    if (tunnel == null) {
      throw new NullPointerException("uFUHeQZIt4 :: tunnel cannot be null");
    }
    return new ObjectTaskCollector(tunnel, new ObjectTaskCollector.Def(extension, extractTimeZoneDefault()));
  }

  private @NonNull TimeZone extractTimeZoneDefault() {
    TimeZone tz = timeZoneDefault;
    return tz != null ? tz : TimeZone.getDefault();
  }
}
