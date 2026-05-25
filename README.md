# kz-pompei-scheduler

> Lightweight Java scheduler with annotation-based tasks and hot-reloadable schedules.

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](#requirements)
[![Gradle](https://img.shields.io/badge/build-Gradle-green.svg)](#build-and-test)
[![TestNG](https://img.shields.io/badge/tests-TestNG-orange.svg)](#build-and-test)
[![Version](https://img.shields.io/badge/version-0.0.1-lightgrey.svg)](versions/version.txt)

`kz-pompei-scheduler` runs Java methods according to schedule expressions.
Schedules can be written directly in annotations or loaded from hot configuration backed by
`kz-pompei-hotconfig`.

```java
Scheduler scheduler = Scheduler.builder().build();

scheduler.collectFromObject(new Object() {
  @Schedule("repeat every 10 s")
  public void ping() {
    System.out.println("ping");
  }
});

scheduler.startUp();
```

## Contents

- [Quick Start](#quick-start)
- [Features](#features)
- [Modules](#modules)
- [Installation](#installation)
- [Annotated Tasks](#annotated-tasks)
- [Hot Configuration](#hot-configuration)
- [Schedule Text Prefixes](#schedule-text-prefixes)
- [Schedule Expressions](#schedule-expressions)
- [Executors](#executors)
- [Build And Test](#build-and-test)
- [Project Layout](#project-layout)
- [Roadmap Ideas](#roadmap-ideas)

## Quick Start

This example mirrors the task collection pattern used by the scheduler core and uses the in-memory configuration tunnel by default.

### 1. Dependency

Add this to build.gradle / dependencies

```groovy
implementation "kz.pompei.scheduler:kz-pompei-scheduler-core:0.0.1"
implementation "kz.pompei.hotconfig:kz-pompei-hotconfig-core:0.0.7"
```

### 2. Define Scheduled Methods

```java
import java.time.Instant;
import kz.pompei.scheduler.core.annotation.Schedule;

public class Jobs {

  @Schedule("13:00")
  public void dailyAtOne() {
    System.out.println("dailyAtOne " + Instant.now());
  }

  @Schedule("repeat every 10 s")
  public void everyTenSeconds() {
    System.out.println("everyTenSeconds " + Instant.now());
  }
}
```

Scheduled methods must have no parameters. If a scheduled method throws, the scheduler sends the exception to the configured task error consumer.

### 3. Create And Start Scheduler

```java
import kz.pompei.scheduler.core.Scheduler;

Scheduler scheduler = Scheduler.builder()
                               .schedulerName("app-scheduler")
                               .build();

scheduler.collectFromObject(new Jobs());
scheduler.startUp();
```

### 4. Stop Scheduler

```java
scheduler.shutDownAndJoinAllRunningTaskFinished();
```

This stops scheduler loops, shuts down created executors, and waits until running task threads finish.

## Features

- Annotation-based task collection with `@Schedule`.
- Optional hot-reloadable schedules with `@FromConf`.
- Schedule expressions for fixed times, time ranges, day, month, year, weekday, and periodic repeats.
- English and Russian aliases for days, months, and repeat syntax.
- Union (`+`), intersection (`*`), and parentheses in schedule expressions.
- Per-task timezone through `@UseTimeZone` or configuration `/TZ` rows.
- Per-task executor selection through `Exe(...)` prefixes or configuration `/EXECUTOR` rows.
- Self-parallel execution opt-in with `parallel` / `паралельно` prefixes.
- Default, named, fixed-one-thread, and virtual-thread executor support.
- Pluggable configuration storage through hotconfig `ConfigTunnel`.

## Modules

| Module                     | Description                                                |
|----------------------------|------------------------------------------------------------|
| `kz-pompei-scheduler-core` | Core scheduler API, annotations, task collector, parser.   |
| `tst-app`                  | Small local application for manual scheduler experiments.  |

## Installation

The library requires Java 21. Add both scheduler core and hotconfig core:
scheduler uses hotconfig `ConfigTunnel` APIs for configuration-backed schedules.

### Gradle

```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation "kz.pompei.scheduler:kz-pompei-scheduler-core:0.0.1"
  implementation "kz.pompei.hotconfig:kz-pompei-hotconfig-core:0.0.7"
}
```

### Maven

```xml
<dependencies>
  <dependency>
    <groupId>kz.pompei.scheduler</groupId>
    <artifactId>kz-pompei-scheduler-core</artifactId>
    <version>0.0.1</version>
  </dependency>
  <dependency>
    <groupId>kz.pompei.hotconfig</groupId>
    <artifactId>kz-pompei-hotconfig-core</artifactId>
    <version>0.0.7</version>
  </dependency>
</dependencies>
```

## Annotated Tasks

Use `@Schedule` on no-argument public methods.

```java
import kz.pompei.scheduler.core.annotation.Schedule;
import kz.pompei.scheduler.core.annotation.UseTimeZone;

public class ReportJobs {

  @UseTimeZone("Asia/Almaty")
  @Schedule("Monday * 09:00")
  public void mondayReport() {
    // run report
  }
}
```

Annotations:

| Annotation               | Target | Purpose                                                       |
|--------------------------|--------|---------------------------------------------------------------|
| `@Schedule("text")`     | method | Defines schedule text for a task.                             |
| `@FromConf`              | method | Loads and refreshes this method schedule from configuration.   |
| `@UseTimeZone("zone")`   | method | Sets timezone for an inline `@Schedule` method.               |

For `@FromConf` methods, the method name becomes the configuration parameter name.

## Hot Configuration

When a method is annotated with `@FromConf`, scheduler writes default schedule rows and reloads edited rows through a hotconfig `ConfigTunnel`.

```java
import java.nio.file.Path;
import kz.pompei.hotconfig.core.ConfigTunnelFile;
import kz.pompei.scheduler.core.Scheduler;
import kz.pompei.scheduler.core.annotation.FromConf;
import kz.pompei.scheduler.core.annotation.Schedule;

class Jobs {
  @FromConf
  @Schedule("repeat every 10 s")
  public void ping() {
    System.out.println("ping");
  }
}

ConfigTunnelFile tunnel = ConfigTunnelFile.builder()
                                          .baseDir(Path.of("config"))
                                          .build();

Scheduler scheduler = Scheduler.builder()
                               .tunnel(tunnel)
                               .configExtension(".scheduler")
                               .build();

scheduler.collectFromObject(new Jobs());
```

The generated configuration path is based on the collected class name and the configured extension.
For configuration-backed tasks, control rows can change defaults for following task rows:

```text
/TZ       = Asia/Almaty
/EXECUTOR = background-pool
ping      = repeat every 10 s
report    = Exe(io-pool) 13:00
```

Control rows:

| Row name    | Case-sensitive | Effect                                                     |
|-------------|----------------|------------------------------------------------------------|
| `/TZ`       | no             | Changes timezone for following task schedule rows.         |
| `/EXECUTOR` | no             | Changes default executor for following task schedule rows. |

`Exe(...)` in a schedule text overrides the current `/EXECUTOR` value for that task only.

## Schedule Text Prefixes

Leading and trailing spaces in schedule text are ignored.

| Prefix                         | Effect                                                                 |
|--------------------------------|------------------------------------------------------------------------|
| `#`                            | Disables the task. The returned schedule never runs.                   |
| `Exe(executorName)`            | Sets executor name for this task. `Exe` is case-insensitive.           |
| `parallel` / `paral...`        | Allows the task to run again while its previous run is still running.  |
| `парал...`                     | Russian parallel marker with the same behavior.                        |

Spaces around `executorName` are removed:

```text
Exe(  background-pool  ) 13:00
```

`Exe(...)` and `parallel` prefixes can be combined in any order:

```text
Exe(background-pool) parallel 13:00
parallel Exe(background-pool) 13:00
```

## Schedule Expressions

Schedule expressions can be combined with:

| Operator | Meaning      | Example                                      |
|----------|--------------|----------------------------------------------|
| `+`      | union        | `13:00 + 14:00`                              |
| `*`      | intersection | `(13:00 + 14:00) * (Monday + Tuesday)`       |
| `(...)`  | grouping     | `(13:00 + 14:00) * Tuesday`                  |

Supported expressions:

| Expression                                      | Description                                      |
|-------------------------------------------------|--------------------------------------------------|
| `hh:MM[:SS]`                                    | Specific time of day.                            |
| `hh:MM[:SS] - hh:MM[:SS]`                      | Time interval inside a day.                      |
| `hh:MM[:SS] - hh:MM[:SS] every PERIOD`         | Periodic instants inside a daily time interval.  |
| `hh:MM[:SS] - hh:MM[:SS] кажд... PERIOD`       | Russian alias for interval periodicity.          |
| `day D` / `день D`                              | Day of month, from 1 to 31.                      |
| `Monday`, `mon`, `Понедельник`, `Пн`            | Day of week.                                     |
| `May`, `май`                                    | Month.                                           |
| `2026 year` / `2026 год` / `2026 г.`            | Year.                                            |
| `repeat every PERIOD`                           | Periodic repeat from scheduler startup.          |
| `повторять каждые PERIOD`                       | Russian alias for periodic repeat.               |
| `repeat every PERIOD starts with PERIOD`        | Periodic repeat with start offset.               |
| `повторять каждые PERIOD начиная с PERIOD`      | Russian alias for repeat with start offset.      |

Examples:

```text
13:00
13:00:15
10:00 - 12:00 every 30 min
repeat every 10 s starts with 5 s
10:00 - 11:00 каждую 15 минут * Пн * Май * 2026 год
```

Period units:

| Units                                                       | Meaning      |
|-------------------------------------------------------------|--------------|
| `ms`, `millis`, `milliseconds`, `мс`, `миллисекунд...`      | milliseconds |
| `s`, `sec`, `seconds`, `с`, `сек`, `секунд...`              | seconds      |
| `m`, `min`, `minute`, `minutes`, `м`, `минут...`            | minutes      |
| `h`, `hour`, `hours`, `ч`, `час...`                         | hours        |
| `d`, `day`, `days`, `д`, `дн`, `дня`, `день...`, `дней...`  | days         |
| `month`, `months`, `мес`, `месяц...`                        | months       |
| `y`, `year`, `years`, `г`, `лет`, `года`                    | years        |

## Executors

By default, scheduler uses a fixed thread pool with 3 threads.

```java
import java.util.concurrent.Executors;
import kz.pompei.scheduler.core.Scheduler;
import kz.pompei.scheduler.core.SchedulerPools;

Scheduler scheduler = Scheduler.builder()
  .executor("background-pool", () -> Executors.newFixedThreadPool(4))
  .executor(SchedulerPools.VIRTUAL, Executors::newVirtualThreadPerTaskExecutor)
  .build();
```

Built-in executor names:

| Name                       | Executor supplier                         |
|----------------------------|-------------------------------------------|
| `SchedulerPools.FIXED_1`   | `Executors.newFixedThreadPool(1)`         |
| `SchedulerPools.VIRTUAL`   | `Executors.newVirtualThreadPerTaskExecutor()` |

If a schedule selects an unknown executor name, the scheduler uses the default executor.

## Build And Test

Requirements:

- JDK 21
- Gradle wrapper is included in this repository

Run core tests:

```bash
./gradlew :kz-pompei-scheduler-core:test
```

Run all tests:

```bash
./gradlew test
```

Compile the test application:

```bash
./gradlew :tst-app:classes
```

## Project Layout

```text
kz-pompei-scheduler
├── kz-pompei-scheduler-core
├── tst-app
├── versions
├── build.gradle
└── settings.gradle
```

Notes for contributors:

- Source directories are `src` and `test_src`.
- Tests use TestNG and AssertJ.
- Lombok and JetBrains annotations are used throughout the codebase.
- Java toolchain is configured for Java 21.
- `kz-pompei-scheduler-core` depends on `kz-pompei-hotconfig-core` for configuration tunnels and models.

## Roadmap Ideas

- Add CI badges once a GitHub Actions workflow exists.
- Add more examples for file-backed production configuration.
- Add a changelog once releases are published.
