package kz.pompei.fui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import kz.pompei.fui.handler.GetSetBool;
import kz.pompei.fui.handler.Handler;
import kz.pompei.fui.handler.Observer;
import lombok.NonNull;

public class FuiCheckbox {


  private final @NonNull Path          chkFileYes;
  private final @NonNull Path          chkFileNo;
  private final @NonNull Runnable      doRemove;
  private final @NonNull Observer      change;
  private final @NonNull AtomicBoolean cachedValue = new AtomicBoolean(false);
  private final          AtomicBoolean firstIdle   = new AtomicBoolean(true);

  public FuiCheckbox(@NonNull Path chkFileYes, @NonNull Path chkFileNo, @NonNull Runnable doRemove, @NonNull Consumer<Throwable> errHandler) {
    this.chkFileYes = chkFileYes;
    this.chkFileNo  = chkFileNo;
    this.doRemove   = doRemove;
    this.change     = new Observer(errHandler);
    syncFiles();
  }

  private void syncFiles() {
    setFileExists(chkFileYes, cachedValue.get());
    setFileExists(chkFileNo, !cachedValue.get());
  }

  private static void setFileExists(@NonNull Path file, boolean exists) {
    boolean existsReal = Files.exists(file);
    if (existsReal == exists) return;

    if (exists) {
      file.toFile().getParentFile().mkdirs();
      try {
        file.toFile().createNewFile();
      } catch (IOException e) {
        throw new RuntimeException("PoVCTQ09dg :: ERR in File.createNewFile() for file " + file, e);
      }
    } else {
      try {
        Files.deleteIfExists(file);
      } catch (IOException e) {
        throw new RuntimeException("CFk8Ce4jRF :: ERR in Files.deleteIfExists() for file " + file, e);
      }
    }
  }

  public @NonNull Disconnector change(@NonNull Handler handler) {
    return change.add(handler);
  }

  public final GetSetBool value = new GetSetBool() {
    @Override public boolean is() {
      return cachedValue.get();
    }

    @Override public void set(boolean value) {
      cachedValue.set(value);
      syncFiles();
    }
  };

  void idle() {
    if (firstIdle.get()) {
      synchronized (firstIdle) {
        if (firstIdle.get()) {
          firstIdle.set(false);
          cachedValue.set(Files.exists(chkFileYes));
          syncFiles();
          return;
        }
      }
    }

    boolean existsYes = Files.exists(chkFileYes);
    boolean existsNo  = Files.exists(chkFileNo);

    if (existsYes && existsNo || !existsYes && !existsNo) {
      cachedValue.set(!cachedValue.get());
      syncFiles();
      change.fire();
    }
  }
}
