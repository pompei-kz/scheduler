package kz.pompei.scheduler.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

class ReflectUtil {

  @RequiredArgsConstructor
  public static class Ann_Class<Ann extends Annotation> {
    public final @NonNull Ann      ann;
    public final @NonNull Class<?> clazz;
  }

  @RequiredArgsConstructor
  public static class Ann_Method<Ann extends Annotation> {
    public final @NonNull Ann    ann;
    public final @NonNull Method method;
  }

  /**
   * Gets the specified annotation from the specified class. If it doesn't exist, it gets the annotation from the parent class.
   * If it doesn't exist there either, it moves on to the next parent class.
   *
   * @param clazz           class with annotation
   * @param annotationClass finding annotation
   * @param <Ann>           type of annotation
   * @return found annotation and class where it was found, or empty if not found
   */
  static <Ann extends Annotation> @NonNull Optional<Ann_Class<Ann>> findAnnotation(@NonNull Class<?> clazz,
                                                                                   @NonNull Class<Ann> annotationClass) {
    Class<?> currentClass = clazz;

    while (currentClass != null) {
      @Nullable Ann annotation = currentClass.getDeclaredAnnotation(annotationClass);
      if (annotation != null) {
        return Optional.of(new Ann_Class<>(annotation, currentClass));
      }

      currentClass = currentClass.getSuperclass();
    }

    return Optional.empty();
  }

  /**
   * Gets the specified annotation from the specified method. If it doesn't exist, it gets the annotation that the method overloaded
   * from the parent class. If it doesn't exist there either, it moves on to the next parent class.
   *
   * @param method          method with annotation
   * @param annotationClass finding annotation
   * @param <Ann>           type of annotation
   * @return found annotation and method where it was found, or empty if not found
   */
  static <Ann extends Annotation> @NonNull Optional<Ann_Method<Ann>> findAnnotation(@NonNull Method method,
                                                                                    @NonNull Class<Ann> annotationClass) {
    {
      @Nullable Ann annotation = method.getAnnotation(annotationClass);
      if (annotation != null) {
        return Optional.of(new Ann_Method<>(annotation, method));
      }
    }

    Class<?> clazz = method.getDeclaringClass().getSuperclass();

    while (clazz != null) {
      try {
        @Nullable Method parentMethod = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
        @Nullable Ann    annotation   = parentMethod.getAnnotation(annotationClass);

        if (annotation != null) {
          return Optional.of(new Ann_Method<>(annotation, parentMethod));
        }

      } catch (NoSuchMethodException ignored) {
        // Move to the next parent class.
      }
      clazz = clazz.getSuperclass();
    }

    return Optional.empty();
  }

}
