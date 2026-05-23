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

public class FuiButton {

  private final @NonNull Path     btnFile;
  private final @NonNull Runnable doRemove;
  private final @NonNull Observer clicking;

  FuiButton(@NonNull Path btnFile, @NonNull Runnable doRemove, @NonNull Consumer<Throwable> errHandler) {
    this.btnFile  = btnFile;
    this.doRemove = doRemove;
    clicking      = new Observer(errHandler);
    createMyFile();
  }

  private void createMyFile() {
    if (!Files.exists(btnFile)) {
      btnFile.toFile().getParentFile().mkdirs();
      try {
        Files.createFile(btnFile);
      } catch (IOException e) {
        throw new RuntimeException("QauPJ1Cr0o :: Err on create file " + btnFile, e);
      }
    }
  }

  public @NonNull Disconnector click(@NonNull Handler handler) {
    return clicking.add(handler);
  }

  public final GetSetBool visibility = new GetSetBool() {
    final AtomicBoolean value = new AtomicBoolean(true);

    @Override public boolean is() {
      return value.get();
    }

    @Override public void set(boolean value) {
      this.value.set(value);
      if (value) {
        createMyFile();
      } else {
        try {
          Files.deleteIfExists(btnFile);
        } catch (IOException e) {
          throw new RuntimeException("PGmC6ij1Pl :: Cannot delete file " + btnFile, e);
        }
      }
    }
  };

  void idle() {
    if (visibility.is()) {
      if (Files.exists(btnFile)) return;
      createMyFile();
      clicking.fire();
      return;
    }
  }

  public void remove() {
    doRemove.run();
    visibility.set(false);
  }
}
