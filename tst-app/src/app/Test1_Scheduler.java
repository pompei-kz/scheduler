package app;

import java.time.Instant;
import kz.pompei.hotconfig.core.ann.ConfFolder;
import kz.pompei.scheduler.core.annotation.FromConf;
import kz.pompei.scheduler.core.annotation.Schedule;

@ConfFolder("hi")
public class Test1_Scheduler {
  @FromConf @Schedule("every 2 sec")
  public void ping() {
    System.out.println("dg2NJ20mnE :: ping "+ Instant.now());
  }

}
