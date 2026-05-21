package kz.pompei.scheduler.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import kz.pompei.hotconfig.core.ConfigTunnel;
import kz.pompei.hotconfig.core.model.Conf;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

public class ConfigTunnelFake implements ConfigTunnel {

  private final AtomicLong timeGoing = new AtomicLong(100);

  @RequiredArgsConstructor
  private static class Dot {
    final Conf conf;
    final long modificationMarker;
  }

  private final ConcurrentHashMap<String, Dot> localPath_to_dot = new ConcurrentHashMap<>();

  @Override public @Nullable Conf read(@NonNull String localPath) {
    Dot dot = localPath_to_dot.get(localPath);
    return dot == null ? null : dot.conf.copy();
  }

  @Override public void write(@NonNull String localPath, @NonNull Conf conf) {
    long modificationMarker = timeGoing.getAndIncrement();
    localPath_to_dot.put(localPath, new Dot(conf.copy(), modificationMarker));
  }

  @Override public @Nullable Long modificationMarker(@NonNull String localPath) {
    Dot dot = localPath_to_dot.get(localPath);
    return dot == null ? null : dot.modificationMarker;
  }
}
