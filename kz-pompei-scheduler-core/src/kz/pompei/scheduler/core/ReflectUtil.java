package kz.pompei.scheduler.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

class ReflectUtil {

  /**
   * Gets the specified annotation from the specified method. If it doesn't exist, it gets the annotation that the method overloaded
   * from the parent class. If it doesn't exist there either, it moves on to the next parent class.
   *
   * @param method          method with annotation
   * @param annotationClass finding annotation
   * @param <Ann>           type of annotation
   * @return found annotation instance or null if not found
   */
  static <Ann extends Annotation> @Nullable Ann findAnnotation(@NonNull Method method, @NonNull Class<Ann> annotationClass) {
    throw new RuntimeException("e6PW4DR8Zj :: Not implemented yet");
  }

}
