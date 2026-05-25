package kz.pompei.scheduler.core;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import kz.pompei.hotconfig.core.ConfigTunnel;
import kz.pompei.hotconfig.core.ConfigTunnelMem;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * The scheduler manager builder.
 */
public class SchedulerBuilder {
  private @NonNull  String                                 schedulerName            = "kz-pompei-scheduler";
  private @Nullable TimeZone                               timeZoneDefault          = null;
  private @NonNull  Consumer<Throwable>                    taskErrorConsumer        = Throwable::printStackTrace;
  private @NonNull  Supplier<ExecutorService>              executorDefaultSupplier  = () -> Executors.newFixedThreadPool(3);
  private final     Map<String, Supplier<ExecutorService>> executorSupplierMap      = new HashMap<>();
  private @NonNull  String                                 configExtension          = ".scheduler";
  private           long                                   runTaskThreadLoopSleepMs = 350;
  private           long                                   refreshConfigMs          = 1700;
  private @NonNull  ConfigTunnel                           tunnel                   = new ConfigTunnelMem();

  SchedulerBuilder() {
    executorSupplierMap.put(SchedulerPools.FIXED_1, () -> Executors.newFixedThreadPool(1));
    executorSupplierMap.put(SchedulerPools.VIRTUAL, Executors::newVirtualThreadPerTaskExecutor);
  }

  /**
   * The name of this scheduler. It will appear in the names of threads started by this scheduler.
   *
   * @param schedulerName The name of this scheduler
   * @return this
   */
  public @NonNull SchedulerBuilder schedulerName(@NonNull String schedulerName) {
    this.schedulerName = schedulerName;
    return this;
  }

  /**
   * TODO add Javadoc
   *
   * @param refreshConfigMs
   * @return
   */
  public @NonNull SchedulerBuilder setRefreshConfigMs(long refreshConfigMs) {
    this.refreshConfigMs = refreshConfigMs;
    return this;
  }

  /**
   * Defines tunnel to save configuration
   *
   * @param tunnel tunnel to save configuration
   * @return this
   */
  public SchedulerBuilder tunnel(@NonNull ConfigTunnel tunnel) {
    this.tunnel = tunnel;
    return this;
  }

  /**
   * Set the file extension for the task collector config file.
   *
   * @param configExtension The file extension to use
   * @return This builder instance for method chaining
   */
  public SchedulerBuilder configExtension(@NonNull String configExtension) {
    this.configExtension = configExtension;
    return this;
  }


  /**
   * Default time zone for scheduler.
   * <p>
   * This time zone will be selected in the very first line of any configuration file.
   * <p>
   * This field can be null. In this case, the very first line of any configuration
   * file will select the time zone set in the system, i.e., the time zone will be selected using the java.util.TimeZone.getDefault() method.
   * <p>
   * It can be set using the TZ environment variable. For example, TZ=Asia/Almaty
   */
  public @NonNull SchedulerBuilder timeZoneDefault(@Nullable TimeZone timeZoneDefault) {
    this.timeZoneDefault = timeZoneDefault;
    return this;
  }

  /**
   * The time to wait in milliseconds at the end of the task check cycle. If 0 is specified, Thread.yield() will be used;
   */
  public @NonNull SchedulerBuilder runTaskThreadLoopSleepMs(long runTaskThreadLoopSleepMs) {
    this.runTaskThreadLoopSleepMs = runTaskThreadLoopSleepMs;
    return this;
  }

  /**
   * To run a task, you need to select an executor. This is the default executor — it's selected if the required one can't be found.
   */
  public @NonNull SchedulerBuilder executorDefault(@NonNull Supplier<ExecutorService> executorDefaultSupplier) {
    this.executorDefaultSupplier = executorDefaultSupplier;
    return this;
  }

  /**
   * To run a task, you need to select an executor. You can specify the executor's name in the schedule,
   * and then you can pass this executor (or rather, its recipient) to a specific name.
   * <p>
   * If a schedule specifies an executor name that you haven't specified,
   * the default executor will be used. This is specified using the {@link  #executorDefault} method.
   *
   * @param executorName     name of executor
   * @param executorSupplier executor supplier.<p>
   *                         This supplier will be used only once, when the corresponding executor is needed.
   *                         If this executor is never needed, this supplier will never be used.
   * @return this
   */
  public @NonNull SchedulerBuilder executor(@NonNull String executorName, @NonNull Supplier<ExecutorService> executorSupplier) {
    this.executorSupplierMap.put(executorName, executorSupplier);
    return this;
  }

  @Override public String toString() {
    return new StringJoiner(", ", getClass().getSimpleName() + "{", "}")
      .add("configExtension='" + configExtension + "'")
      .add("schedulerName='" + schedulerName + "'")
      .add("timeZoneDefault=" + timeZoneDefault)
      .add("taskErrorConsumer=" + taskErrorConsumer)
      .add("executorDefaultSupplier=" + executorDefaultSupplier)
      .add("executorSupplierMap=" + executorSupplierMap)
      .add("runTaskThreadLoopSleepMs=" + runTaskThreadLoopSleepMs)
      .add("tunnel=" + tunnel)
      .toString();
  }

  /**
   * Defines handler for errors from task executions.
   *
   * @param taskErrorConsumer handler of errors from task executions.
   * @return this
   */
  public @NonNull SchedulerBuilder taskErrorConsumer(@NonNull Consumer<Throwable> taskErrorConsumer) {
    this.taskErrorConsumer = taskErrorConsumer;
    return this;
  }

  /**
   * Creates scheduler manager.
   * <p>
   * After create scheduler manager you need call method {@link Scheduler#startUp()} to start scheduler work.
   *
   * @return scheduler manager
   */
  public @NonNull Scheduler build() {

    Scheduler.Def def = new Scheduler.Def(
      schedulerName, timeZoneDefault, runTaskThreadLoopSleepMs, refreshConfigMs, executorDefaultSupplier,
      executorSupplierMap, taskErrorConsumer, configExtension, tunnel
    );

    Collector collector = new Collector(def);

    return new Scheduler(def, collector);
  }
}
