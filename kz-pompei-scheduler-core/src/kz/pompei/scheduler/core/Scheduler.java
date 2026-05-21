package kz.pompei.scheduler.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Scheduler {
  private final @NonNull Def           def;
  private final @NonNull AtomicBoolean working = new AtomicBoolean(true);

  private final ConcurrentHashMap<Integer, ScheduledTask> taskId_to_task = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Thread>> taskId_runId_to_thread = new ConcurrentHashMap<>();

  private final AtomicInteger taskIdSource = new AtomicInteger(1);
  private final AtomicLong    runIdSource  = new AtomicLong(1L);

  Scheduler(@NonNull Def def) {
    this.def = def;
  }

  public static @NonNull SchedulerBuilder builder() {
    return new SchedulerBuilder();
  }

  @RequiredArgsConstructor
  static class Def {

    final @NonNull  String                                 schedulerName;
    final @Nullable TimeZone                               timeZoneDefault;
    final           long                                   runTaskThreadLoopSleepMs;
    final @NonNull  Supplier<ExecutorService>              executorDefaultSupplier;
    final @NonNull  Map<String, Supplier<ExecutorService>> executorSupplierMap;
    final @NonNull  Consumer<Throwable>                    taskErrorConsumer;

    @NonNull TimeZone getTimezone() {
      return timeZoneDefault != null ? (TimeZone) timeZoneDefault.clone() : TimeZone.getDefault();
    }
  }

  public void add(@NonNull ScheduledTask task) {
    taskId_to_task.put(taskIdSource.getAndIncrement(), task);
  }

  public <T extends ScheduledTask> void addAll(@NonNull Collection<T> tasks) {
    tasks.forEach(this::add);
  }

  public void startUp() {
    new Thread(() -> {

      final long    timestampStartedAt = System.currentTimeMillis();
      long          timestampCurrent   = timestampStartedAt;
      List<Integer> taskIdsToRun       = new ArrayList<>();

      while (working.get()) {

        {
          long sleepMs = def.runTaskThreadLoopSleepMs;
          if (sleepMs <= 0) {
            Thread.yield();
          } else {
            try {
              //noinspection BusyWait
              Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
              throw new RuntimeException("KE1Vt9Dyw2 :: Interrupted from scheduler run thread from sleep", e);
            }
          }
        }

        long newTimestamp = System.currentTimeMillis();

        for (Map.Entry<Integer, ScheduledTask> e : taskId_to_task.entrySet()) {

          int           taskId        = e.getKey();
          ScheduledTask scheduledTask = e.getValue();
          ScheduleSrc   src           = scheduledTask.src();

          if (src.isScheduled(timestampStartedAt, timestampCurrent, newTimestamp)) {

            if (src.isParallel() || runCount(taskId) == 0) {
              taskIdsToRun.add(taskId);
              continue;
            }
          }
        }

        timestampCurrent = newTimestamp;

        runTasks(taskIdsToRun);

        taskIdsToRun.clear();
      }
    }).start();
  }

  private int runCount(int taskId) {
    ConcurrentHashMap<Long, Thread> map = taskId_runId_to_thread.get(taskId);
    return map == null ? 0 : map.size();
  }

  public void shutDownAndJoinAllRunningTaskFinished() {
    working.set(false);

    while (true) {
      List<Thread> allRunningThreads = taskId_runId_to_thread.values().stream().flatMap(x -> x.values().stream()).toList();

      boolean noJoins = true;

      for (Thread thread : allRunningThreads) {
        if (thread.isAlive()) {
          try {
            thread.join();
          } catch (InterruptedException e) {
            throw new RuntimeException("857e0PEClZ :: Interrupted while joining scheduler thread " + thread.getName(), e);
          }
          noJoins = false;
        }
      }

      if (noJoins) break;
    }
  }

  private static @NonNull String nullToEmptyStr(String str) {
    return str == null ? "" : str;
  }

  private final ConcurrentHashMap<String, ExecutorService> name_to_executorService = new ConcurrentHashMap<>();

  private @NonNull ExecutorService getExecutorService(@Nullable String executorName) {
    return name_to_executorService.computeIfAbsent(executorName, this::createExecutorByName);
  }

  private @NotNull ExecutorService createExecutorByName(@Nullable String executorName) {

    @NonNull String executorName_orEmptyStr = nullToEmptyStr(executorName);

    {
      @Nullable Supplier<ExecutorService> supplier = def.executorSupplierMap.get(executorName_orEmptyStr);
      if (supplier != null) {
        return supplier.get();
      }
    }

    return def.executorDefaultSupplier.get();
  }

  private void runTasks(@NonNull List<Integer> taskIdsToRun) {
    for (int taskId : taskIdsToRun) {
      runTask(taskId);
    }
  }

  private void runTask(int taskId) {

    @Nullable ScheduledTask scheduledTask = taskId_to_task.get(taskId);

    if (scheduledTask == null) return;

    @NonNull Task task = scheduledTask.task();

    @NonNull ExecutorService executorService = getExecutorService(scheduledTask.src().executorName());

    @NonNull ConcurrentHashMap<Long, Thread> runId_to_thread = taskId_runId_to_thread.computeIfAbsent(taskId, __ -> new ConcurrentHashMap<>());


    executorService.submit(() -> {
      long   runId  = runIdSource.getAndIncrement();
      Thread thread = Thread.currentThread();
      runId_to_thread.put(runId, thread);
      thread.setName(def.schedulerName + "/" + task.taskName() + " #" + runId);

      try {

        //
        //
        task.run();
        //
        //

      } catch (Throwable e) {
        def.taskErrorConsumer.accept(e);
      } finally {
        runId_to_thread.remove(runId);
      }


    });
  }
}
