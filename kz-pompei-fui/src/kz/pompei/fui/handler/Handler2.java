package kz.pompei.fui.handler;

public interface Handler2<T1, T2> {
  void handle(T1 t1, T2 t2) throws Throwable;
}
