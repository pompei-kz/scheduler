package kz.pompei.fui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import kz.pompei.fui.handler.GetSetBool;
import kz.pompei.fui.handler.Get_Set;
import kz.pompei.fui.handler.Handler;
import kz.pompei.fui.handler.Observer;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FuiEditor {
  private final @NonNull Path                      editFile;
  private final @NonNull Path                      cacheFile;
  private final @NonNull Runnable                  doRemove;
  private final @NonNull AtomicReference<FileTime> lastModified = new AtomicReference<>(null);
  private final @NonNull AtomicReference<String>   cacheValue   = new AtomicReference<>(null);
  private final @NonNull AtomicBoolean             firstIdle    = new AtomicBoolean(true);
  private final @NonNull Observer                  change;

  FuiEditor(@NonNull Path editFile, @NonNull Path cacheFile, @NonNull Runnable doRemove, @NonNull Consumer<Throwable> errHandler) {
    this.editFile  = editFile;
    this.cacheFile = cacheFile;
    this.doRemove  = doRemove;
    this.change    = new Observer(errHandler);
  }

  private void createMyFileFromCache() {
    editFile.toFile().getParentFile().mkdirs();
    try {
      Files.createFile(editFile);
      if (Files.exists(cacheFile)) {
        Files.copy(cacheFile, editFile, REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new RuntimeException("RdFzZV1lmX :: Err on create file " + editFile, e);
    }
  }

  public final GetSetBool visibility = new GetSetBool() {
    final AtomicBoolean value = new AtomicBoolean(true);

    @Override public boolean is() {
      return value.get();
    }

    @Override public void set(boolean value) {
      this.value.set(value);
      if (value) {
        createMyFileFromCache();
      } else {
        try {
          Files.deleteIfExists(editFile);
        } catch (IOException e) {
          throw new RuntimeException("X4wb9PF12l :: Cannot delete file " + editFile, e);
        }
      }
    }
  };

  public final Get_Set<@NonNull String, String> value = new Get_Set<>() {
    @Override public @NonNull String get() {
      String x = cacheValue.get();
      return x == null ? "" : x;
    }

    @Override public void set(String value) {
      String s = value == null ? "" : value;
      cacheValue.set(s);
      writeToFile(editFile, s);
      writeToFile(cacheFile, s);
      lastModified.set(readLastModified(editFile));
    }
  };

  public void remove() {
    doRemove.run();
    visibility.set(false);
  }

  public @NonNull Disconnector change(@NonNull Handler handler) {
    return change.add(handler);
  }

  private static @Nullable FileTime readLastModified(@NonNull Path file) {
    try {
      return Files.getLastModifiedTime(file);
    } catch (NoSuchFileException e) {
      return null;
    } catch (IOException e) {
      throw new RuntimeException("15FcJiR9LM :: ERR from readLastModified for file " + file, e);
    }
  }

  private static void writeToFile(@NonNull Path file, String value) {
    file.toFile().getParentFile().mkdirs();
    try {
      Files.writeString(file, value == null ? "" : value);
    } catch (IOException e) {
      throw new RuntimeException("7n301m9xHU :: Cannot write to file " + file, e);
    }
  }

  private static @NonNull Optional<String> readFile(@NonNull Path file) {
    if (!Files.exists(file)) {
      return Optional.empty();
    }

    try {
      return Optional.of(Files.readString(file, StandardCharsets.UTF_8));
    } catch (NoSuchFileException e) {
      return Optional.empty();
    } catch (IOException e) {
      throw new RuntimeException("ucFaEEPhsC :: ERR while reading file " + file, e);
    }
  }

  void idle() {
    if (firstIdle.get()) {
      synchronized (firstIdle) {
        if (firstIdle.get()) {
          firstIdle.set(false);

          if (Files.exists(editFile)) {
            try {
              Files.copy(editFile, cacheFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
              throw new RuntimeException("k5RXT8Y8cl :: Cannot copy from `" + editFile + "` to `" + cacheFile + "`", e);
            }
            cacheValue.set(readFile(cacheFile).orElse(""));
          } else {
            try {
              Files.deleteIfExists(cacheFile);
              cacheValue.set("");
            } catch (IOException e) {
              throw new RuntimeException("jPejS8VEh5 :: Cannot delete file `" + cacheFile + "`", e);
            }
          }
          return;
        }
      }
    }

    FileTime current = readLastModified(editFile);
    if (current != null && Objects.equals(current, lastModified.get())) {
      return;
    }

    lastModified.set(current);

    String content     = readFile(editFile).orElse(null);
    String cachedValue = cacheValue.get();

    if (content == null) {
      writeToFile(editFile, cachedValue);
      return;
    }

    if (Objects.equals(content, cachedValue)) {
      return;
    }

    cacheValue.set(content);
    writeToFile(cacheFile, content);

    change.fire();
  }
}
