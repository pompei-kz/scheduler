package kz.pompei.fui.handler;

import java.util.function.Consumer;
import lombok.NonNull;

public class Observer1<T> extends ObserverAbstract<Handler1<T>> {

  public Observer1(@NonNull Consumer<Throwable> errHandler) {
    super(errHandler);
  }

  public void fire(T t) {
    for (Handler1<T> handler : handlers.values()) {
      try {
        handler.handle(t);
      } catch (Throwable e) {
        errHandler.accept(e);
      }
    }
  }
}
