package kz.pompei.fui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

public class Fui {
  private final @NonNull Def           def;
  private final @NonNull AtomicBoolean working = new AtomicBoolean(true);

  Fui(@NonNull Def def) {
    this.def = def;
  }

  public static @NonNull FuiBuilder builder() {
    return new FuiBuilder();
  }

  @RequiredArgsConstructor
  static class Def {
    private final @NonNull Path                rootDir;
    private final @NonNull String              stopApplicationBtnName;
    private final @NonNull String              extensionBtn;
    private final @NonNull String              extensionEdit;
    private final          long                surveyLoopSleepMs;
    private final @NonNull Consumer<Throwable> errHandler;
  }

  private final ConcurrentHashMap<Long, Runnable> updates = new ConcurrentHashMap<>();
  private final AtomicLong                        nextId  = new AtomicLong(1);

  public void go() {

    Path closeBtn = def.rootDir.resolve(def.stopApplicationBtnName + def.extensionBtn);
    closeBtn.toFile().getParentFile().mkdirs();
    try {
      closeBtn.toFile().createNewFile();
    } catch (IOException e) {
      throw new RuntimeException("RiF1pDmNmA :: ERR in createNewFile() of file " + closeBtn, e);
    }

    while (working.get()) {

      updates.values().forEach(Runnable::run);

      long surveyLoopSleepMs = def.surveyLoopSleepMs;
      if (surveyLoopSleepMs > 0) {
        try {
          //noinspection BusyWait
          Thread.sleep(surveyLoopSleepMs);
        } catch (InterruptedException e) {
          working.set(false);
        }
      } else {
        Thread.yield();
      }

      if (!Files.exists(closeBtn)) {
        working.set(false);
      }
    }
  }

  public @NonNull FuiButton button(@NonNull String localPathName) {
    Path      btnFile   = def.rootDir.resolve(removeStartSlashes(localPathName) + def.extensionBtn);
    long      id        = nextId.getAndIncrement();
    FuiButton fuiButton = new FuiButton(btnFile, () -> updates.remove(id), def.errHandler);
    updates.put(id, fuiButton::idle);
    return fuiButton;
  }

  public @NonNull FuiEditor editor(@NonNull String localPathName) {
    Path      editFile  = def.rootDir.resolve(removeStartSlashes(localPathName) + def.extensionEdit);
    Path      cacheFile = fuiCacheDir().resolve(removeStartSlashes(localPathName) + def.extensionEdit);
    long      id        = nextId.getAndIncrement();
    FuiEditor fuiEditor = new FuiEditor(editFile, cacheFile, () -> updates.remove(id), def.errHandler);
    updates.put(id, fuiEditor::idle);
    return fuiEditor;
  }

  public @NonNull FuiCheckbox checkbox(@NonNull String localPathName) {
    Path        chkFileYes  = def.rootDir.resolve(removeStartSlashes(localPathName) + ".YES");
    Path        chkFileNo   = def.rootDir.resolve(removeStartSlashes(localPathName) + ".NO");
    long        id          = nextId.getAndIncrement();
    FuiCheckbox fuiCheckbox = new FuiCheckbox(chkFileYes, chkFileNo, () -> updates.remove(id), def.errHandler);
    updates.put(id, fuiCheckbox::idle);
    return fuiCheckbox;
  }

  private @NonNull Path fuiCacheDir() {
    return def.rootDir.resolve(".fui").resolve("cache");
  }

  private static @Nullable String removeStartSlashes(@Nullable String str) {
    if (str == null) return null;
    for (int i = 0, C = str.length(); i < C; i++) {
      if (str.charAt(i) != '/') {
        return str.substring(i);
      }
    }
    return str;
  }
}
