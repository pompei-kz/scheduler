package kz.pompei.fui.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import kz.pompei.fui.Disconnector;
import lombok.NonNull;

public abstract class ObserverAbstract<H> {
  protected final @NonNull Consumer<Throwable>        errHandler;
  protected final          ConcurrentHashMap<Long, H> handlers = new ConcurrentHashMap<>();
  private final            AtomicLong                 nextId   = new AtomicLong(1L);

  public ObserverAbstract(@NonNull Consumer<Throwable> errHandler) {
    this.errHandler = errHandler;
  }

  public @NonNull Disconnector add(@NonNull H handler) {
    long id = nextId.getAndIncrement();
    handlers.put(id, handler);
    return () -> handlers.remove(id);
  }


}
