package kz.pompei.scheduler.core;

import kz.pompei.scheduler.core.annotation.Schedule;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectUtilTest {

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
}
