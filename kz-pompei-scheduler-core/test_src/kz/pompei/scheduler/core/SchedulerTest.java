package kz.pompei.scheduler.core;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerTest {

  @Test
  public void test() {
    Scheduler scheduler = new Scheduler();
    assertThat(scheduler).isNotNull();
  }

}
