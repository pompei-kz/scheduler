package kz.pompei.scheduler.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

class ReflectUtil {

  /**
   * Gets the specified annotation from the specified class. If it doesn't exist, it gets the annotation from the parent class.
   * If it doesn't exist there either, it moves on to the next parent class.
   *
   * @param clazz           class with annotation
   * @param annotationClass finding annotation
   * @param <Ann>           type of annotation
   * @return found annotation instance or null if not found
   */
  static <Ann extends Annotation> @Nullable Ann findAnnotation(@NonNull Class<?> clazz,
                                                               @NonNull Class<Ann> annotationClass) {
    Class<?> currentClass = clazz;

    while (currentClass != null) {
      @Nullable Ann annotation = currentClass.getDeclaredAnnotation(annotationClass);
      if (annotation != null) {
        return annotation;
      }

      currentClass = currentClass.getSuperclass();
    }

    return null;
  }

  /**
   * Gets the specified annotation from the specified method. If it doesn't exist, it gets the annotation that the method overloaded
   * from the parent class. If it doesn't exist there either, it moves on to the next parent class.
   *
   * @param method          method with annotation
   * @param annotationClass finding annotation
   * @param <Ann>           type of annotation
   * @return found annotation instance or null if not found
   */
  static <Ann extends Annotation> @Nullable Ann findAnnotation(@NonNull Method method,
                                                               @SuppressWarnings("SameParameterValue") @NonNull Class<Ann> annotationClass) {
    {
      @Nullable Ann annotation = method.getAnnotation(annotationClass);
      if (annotation != null) {
        return annotation;
      }
    }

    Class<?> clazz = method.getDeclaringClass().getSuperclass();

    while (clazz != null) {
      try {
        @Nullable Method parentMethod = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
        @Nullable Ann    annotation   = parentMethod.getAnnotation(annotationClass);

        if (annotation != null) {
          return annotation;
        }

      } catch (NoSuchMethodException ignored) {
        // Move to the next parent class.
      }
      clazz = clazz.getSuperclass();
    }

    return null;
  }

}
