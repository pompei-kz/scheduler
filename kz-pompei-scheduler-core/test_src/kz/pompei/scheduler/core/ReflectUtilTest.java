package kz.pompei.scheduler.core;

import kz.pompei.scheduler.core.annotation.Schedule;
import org.testng.annotations.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Optional;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static org.assertj.core.api.Assertions.assertThat;

public class ReflectUtilTest {

  @Test
  public void findAnnotation_shouldReturnAnnotationFromClass() {
    Optional<ReflectUtil.Ann_Class<ClassSchedule>> scheduleOpt = ReflectUtil.findAnnotation(DirectAnnotatedClass.class, ClassSchedule.class);

    assertThat(scheduleOpt).isPresent();
    ReflectUtil.Ann_Class<ClassSchedule> schedule = scheduleOpt.orElseThrow();
    assertThat(schedule.ann.value()).isEqualTo("directClass");
    assertThat(schedule.clazz).isEqualTo(DirectAnnotatedClass.class);
  }

  @Test
  public void findAnnotation_shouldReturnAnnotationFromParentClass() {
    Optional<ReflectUtil.Ann_Class<ClassSchedule>> scheduleOpt = ReflectUtil.findAnnotation(ChildClass.class, ClassSchedule.class);

    assertThat(scheduleOpt).isPresent();
    ReflectUtil.Ann_Class<ClassSchedule> schedule = scheduleOpt.orElseThrow();
    assertThat(schedule.ann.value()).isEqualTo("parentClass");
    assertThat(schedule.clazz).isEqualTo(ParentClass.class);
  }

  @Test
  public void findAnnotation_shouldReturnEmptyWhenClassAnnotationNotFound() {
    Optional<ReflectUtil.Ann_Class<ClassSchedule>> schedule = ReflectUtil.findAnnotation(NotAnnotatedClass.class, ClassSchedule.class);

    assertThat(schedule).isEmpty();
  }

  @Test
  public void findAnnotation_shouldReturnAnotherAnnotationFromClass() {
    Optional<ReflectUtil.Ann_Class<ClassLabel>> labelOpt = ReflectUtil.findAnnotation(DirectLabeledClass.class, ClassLabel.class);

    assertThat(labelOpt).isPresent();
    ReflectUtil.Ann_Class<ClassLabel> label = labelOpt.orElseThrow();
    assertThat(label.ann.name()).isEqualTo("directLabel");
    assertThat(label.clazz).isEqualTo(DirectLabeledClass.class);
  }

  @Test
  public void findAnnotation_shouldReturnAnotherAnnotationFromParentClass() {
    Optional<ReflectUtil.Ann_Class<ClassLabel>> labelOpt = ReflectUtil.findAnnotation(ChildLabeledClass.class, ClassLabel.class);

    assertThat(labelOpt).isPresent();
    ReflectUtil.Ann_Class<ClassLabel> label = labelOpt.orElseThrow();
    assertThat(label.ann.name()).isEqualTo("parentLabel");
    assertThat(label.clazz).isEqualTo(ParentLabeledClass.class);
  }

  @Test
  public void findAnnotation_shouldReturnAnnotationFromMethod() throws NoSuchMethodException {
    Method method = DirectAnnotatedTask.class.getDeclaredMethod("runTask");

    //
    //
    Optional<ReflectUtil.Ann_Method<Schedule>> scheduleOpt = ReflectUtil.findAnnotation(method, Schedule.class);
    //
    //

    assertThat(scheduleOpt).isPresent();
    ReflectUtil.Ann_Method<Schedule> schedule = scheduleOpt.orElseThrow();
    assertThat(schedule.ann.value()).isEqualTo("direct");
    assertThat(schedule.method).isEqualTo(method);
  }

  @Test
  public void findAnnotation_shouldReturnAnnotationFromParentOverriddenMethod() throws NoSuchMethodException {
    Method method = ChildTask.class.getDeclaredMethod("runTask", String.class);

    //
    //
    Optional<ReflectUtil.Ann_Method<Schedule>> scheduleOpt = ReflectUtil.findAnnotation(method, Schedule.class);
    //
    //

    assertThat(scheduleOpt).isPresent();
    ReflectUtil.Ann_Method<Schedule> schedule = scheduleOpt.orElseThrow();
    assertThat(schedule.ann.value()).isEqualTo("parent");
    assertThat(schedule.method).isEqualTo(ParentTask.class.getDeclaredMethod("runTask", String.class));
  }

  @Test
  public void findAnnotation_shouldReturnEmptyWhenAnnotationNotFound() throws NoSuchMethodException {
    Method method = NotAnnotatedTask.class.getDeclaredMethod("runTask");

    //
    //
    Optional<ReflectUtil.Ann_Method<Schedule>> schedule = ReflectUtil.findAnnotation(method, Schedule.class);
    //
    //

    assertThat(schedule).isEmpty();
  }

  @Test
  public void findAnnotation_shouldReturnAnotherAnnotationFromMethod() throws NoSuchMethodException {
    Method method = DirectMarkedTask.class.getDeclaredMethod("runTask");

    Optional<ReflectUtil.Ann_Method<MethodMarker>> markerOpt = ReflectUtil.findAnnotation(method, MethodMarker.class);

    assertThat(markerOpt).isPresent();
    ReflectUtil.Ann_Method<MethodMarker> marker = markerOpt.orElseThrow();
    assertThat(marker.ann.code()).isEqualTo(100);
    assertThat(marker.method).isEqualTo(method);
  }

  @Test
  public void findAnnotation_shouldReturnAnotherAnnotationFromParentOverriddenMethod() throws NoSuchMethodException {
    Method method = ChildMarkedTask.class.getDeclaredMethod("runTask");

    Optional<ReflectUtil.Ann_Method<MethodMarker>> markerOpt = ReflectUtil.findAnnotation(method, MethodMarker.class);

    assertThat(markerOpt).isPresent();
    ReflectUtil.Ann_Method<MethodMarker> marker = markerOpt.orElseThrow();
    assertThat(marker.ann.code()).isEqualTo(200);
    assertThat(marker.method).isEqualTo(ParentMarkedTask.class.getDeclaredMethod("runTask"));
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

  static class DirectMarkedTask {
    @MethodMarker(code = 100)
    void runTask() {
    }
  }

  static class ParentMarkedTask {
    @MethodMarker(code = 200)
    void runTask() {
    }
  }

  static class ChildMarkedTask extends ParentMarkedTask {
    @Override
    void runTask() {
    }
  }

  @Target(TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface ClassSchedule {
    String value();
  }

  @Target(TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface ClassLabel {
    String name();
  }

  @Target(METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  @interface MethodMarker {
    int code();
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

  @ClassLabel(name = "directLabel")
  static class DirectLabeledClass {
  }

  @ClassLabel(name = "parentLabel")
  static class ParentLabeledClass {
  }

  static class ChildLabeledClass extends ParentLabeledClass {
  }
}
