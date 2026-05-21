package kz.pompei.scheduler.core;

import kz.pompei.scheduler.core.annotation.Schedule;
import org.testng.annotations.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.TYPE;
import static org.assertj.core.api.Assertions.assertThat;

public class ReflectUtilTest {

  @Test
  public void findAnnotation_shouldReturnAnnotationFromClass() {
    ClassSchedule schedule = ReflectUtil.findAnnotation(DirectAnnotatedClass.class, ClassSchedule.class);

    assertThat(schedule).isNotNull();
    assertThat(schedule.value()).isEqualTo("directClass");
  }

  @Test
  public void findAnnotation_shouldReturnAnnotationFromParentClass() {
    ClassSchedule schedule = ReflectUtil.findAnnotation(ChildClass.class, ClassSchedule.class);

    assertThat(schedule).isNotNull();
    assertThat(schedule.value()).isEqualTo("parentClass");
  }

  @Test
  public void findAnnotation_shouldReturnNullWhenClassAnnotationNotFound() {
    ClassSchedule schedule = ReflectUtil.findAnnotation(NotAnnotatedClass.class, ClassSchedule.class);

    assertThat(schedule).isNull();
  }

  @Test
  public void findAnnotation_shouldReturnAnnotationFromMethod() throws NoSuchMethodException {
    Method method = DirectAnnotatedTask.class.getDeclaredMethod("runTask");

    //
    //
    Schedule schedule = ReflectUtil.findAnnotation(method, Schedule.class);
    //
    //

    assertThat(schedule).isNotNull();
    assertThat(schedule.value()).isEqualTo("direct");
  }

  @Test
  public void findAnnotation_shouldReturnAnnotationFromParentOverriddenMethod() throws NoSuchMethodException {
    Method method = ChildTask.class.getDeclaredMethod("runTask", String.class);

    //
    //
    Schedule schedule = ReflectUtil.findAnnotation(method, Schedule.class);
    //
    //

    assertThat(schedule).isNotNull();
    assertThat(schedule.value()).isEqualTo("parent");
  }

  @Test
  public void findAnnotation_shouldReturnNullWhenAnnotationNotFound() throws NoSuchMethodException {
    Method method = NotAnnotatedTask.class.getDeclaredMethod("runTask");

    //
    //
    Schedule schedule = ReflectUtil.findAnnotation(method, Schedule.class);
    //
    //

    assertThat(schedule).isNull();
  }

  static class DirectAnnotatedTask {
    @Schedule("direct")
    void runTask() {
    }
  }

  static class ParentTask {
    @Schedule("parent")
    void runTask(String arg) {
    }
  }

  static class ChildTask extends ParentTask {
    @Override
    void runTask(String arg) {
    }
  }

  static class NotAnnotatedTask {
    void runTask() {
    }
  }

  @Target(TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface ClassSchedule {
    String value();
  }

  @ClassSchedule("directClass")
  static class DirectAnnotatedClass {
  }

  @ClassSchedule("parentClass")
  static class ParentClass {
  }

  static class ChildClass extends ParentClass {
  }

  static class NotAnnotatedClass {
  }
}
