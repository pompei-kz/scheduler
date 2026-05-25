package app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import kz.pompei.fui.Fui;
import kz.pompei.hotconfig.core.ConfigTunnelFile;
import kz.pompei.scheduler.core.Scheduler;
import kz.pompei.scheduler.core.annotation.FromConf;
import kz.pompei.scheduler.core.annotation.Schedule;

public class Main {
  public static void main(String[] args) {

    Path             appRoot = Paths.get("build/tst-app");
    Fui              fui     = Fui.builder().rootDir(appRoot).build();
    ConfigTunnelFile tunnel  = ConfigTunnelFile.builder().baseDir(appRoot).build();

    Object object = new Object() {
      @FromConf @Schedule("every 2 sec")
      public void ping() {
        System.out.println("dg2NJ20mnE :: ping "+ Instant.now());
      }
    };

    Scheduler scheduler = Scheduler.builder().tunnel(tunnel).build();

    scheduler.collectFromObject(object);

    scheduler.startUp();

    System.out.println("a2sX7k3DfZ :: Application started");
    fui.go();
    System.out.println("J9L0EOzfZT :: Stopping scheduler");
    scheduler.shutDownAndJoinAllRunningTaskFinished();
    System.out.println("rb9l8Sx8tL :: Scheduler stopped. Application finished");
  }
}
