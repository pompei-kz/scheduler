package kz.pompei.fui.handler;

import java.util.function.Consumer;
import lombok.NonNull;

public class Observer2<T1, T2> extends ObserverAbstract<Handler2<T1, T2>> {

  public Observer2(@NonNull Consumer<Throwable> errHandler) {
    super(errHandler);
  }

  public void fire(T1 t1, T2 t2) {
    for (Handler2<T1, T2> handler : handlers.values()) {
      try {
        handler.handle(t1, t2);
      } catch (Throwable e) {
        errHandler.accept(e);
      }
    }
  }
}
