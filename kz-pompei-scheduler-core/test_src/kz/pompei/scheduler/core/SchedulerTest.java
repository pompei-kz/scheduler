package kz.pompei.scheduler.core;

import org.testng.annotations.Test;

public class SchedulerTest {

  @Test
  public void test() {
    Scheduler scheduler = Scheduler.builder().build();
  }

}
