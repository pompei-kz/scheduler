package kz.pompei.scheduler.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to describe the schedule for running a single task
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {

  /**
   * Schedule expression
   *
   * @return Schedule expression
   */
  String value();
}
