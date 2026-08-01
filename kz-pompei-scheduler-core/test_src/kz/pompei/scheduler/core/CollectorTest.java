package kz.pompei.scheduler.core;

import java.util.Set;
import kz.pompei.hotconfig.core.ann.ConfFolder;
import kz.pompei.scheduler.core.annotation.FromConf;
import kz.pompei.scheduler.core.annotation.Schedule;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectorTest {

  @ConfFolder("folder_from_annotation")
  public static class TaskInFolder {
    @FromConf @Schedule("every 2 sec")
    public void ping() {}
  }

  public static class TaskWithoutFolder {
    @FromConf @Schedule("every 2 sec")
    public void ping() {}
  }

  public static class TaskInInheritedFolder extends TaskInFolder {}

  @Test
  public void collect__confFolder__usesAnnotationValueAsFolderName() {

    ConfigTunnelFake tunnel = new ConfigTunnelFake();

    Scheduler scheduler = Scheduler.builder().tunnel(tunnel).configExtension(".scheduler").build();

    scheduler.collectFromObject(new TaskInFolder());

    assertThat(tunnel.writtenLocalPaths()).containsExactly("folder_from_annotation/TaskInFolder.scheduler");
  }

  @Test
  public void collect__noConfFolder__noFolderInPath() {

    ConfigTunnelFake tunnel = new ConfigTunnelFake();

    Scheduler scheduler = Scheduler.builder().tunnel(tunnel).configExtension(".scheduler").build();

    scheduler.collectFromObject(new TaskWithoutFolder());

    assertThat(tunnel.writtenLocalPaths()).containsExactly("TaskWithoutFolder.scheduler");
  }

  @Test
  public void collect__confFolderOnParentClass__usesAnnotationValueAsFolderName() {

    ConfigTunnelFake tunnel = new ConfigTunnelFake();

    Scheduler scheduler = Scheduler.builder().tunnel(tunnel).configExtension(".scheduler").build();

    scheduler.collectFromObject(new TaskInInheritedFolder());

    assertThat(tunnel.writtenLocalPaths()).containsExactly("folder_from_annotation/TaskInInheritedFolder.scheduler");
  }

  /**
   * The folder name must not depend on the runtime state: after a restart the config must be read from the same place.
   */
  @Test
  public void collect__confFolder__folderNameIsStableBetweenRestarts() {

    Set<String> paths1;
    {
      ConfigTunnelFake tunnel    = new ConfigTunnelFake();
      Scheduler        scheduler = Scheduler.builder().tunnel(tunnel).build();
      scheduler.collectFromObject(new TaskInFolder());
      paths1 = tunnel.writtenLocalPaths();
    }

    Set<String> paths2;
    {
      ConfigTunnelFake tunnel    = new ConfigTunnelFake();
      Scheduler        scheduler = Scheduler.builder().tunnel(tunnel).build();
      scheduler.collectFromObject(new TaskInFolder());
      paths2 = tunnel.writtenLocalPaths();
    }

    assertThat(paths1).isEqualTo(paths2);
  }

  /**
   * On the second collect the config must be found by the same path, so nothing new is written.
   */
  @Test
  public void collect__confFolder__secondCollectReadsTheSameConfig() {

    ConfigTunnelFake tunnel = new ConfigTunnelFake();

    Scheduler scheduler1 = Scheduler.builder().tunnel(tunnel).build();
    scheduler1.collectFromObject(new TaskInFolder());

    Scheduler scheduler2 = Scheduler.builder().tunnel(tunnel).build();
    scheduler2.collectFromObject(new TaskInFolder());

    assertThat(tunnel.writtenLocalPaths()).hasSize(1);
  }

}
