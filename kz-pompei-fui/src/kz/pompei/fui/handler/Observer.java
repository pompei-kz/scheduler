package kz.pompei.fui.handler;

import java.util.function.Consumer;
import lombok.NonNull;

public class Observer extends ObserverAbstract<Handler> {

  public Observer(@NonNull Consumer<Throwable> errHandler) {
    super(errHandler);
  }

  public void fire() {
    for (Handler handler : handlers.values()) {
      try {
        handler.handle();
      } catch (Throwable e) {
        errHandler.accept(e);
      }
    }
  }

}
