package kz.pompei.fui;

import java.nio.file.Path;
import java.util.function.Consumer;
import lombok.NonNull;

public class FuiBuilder {
  private          Path                rootDir;
  private @NonNull String              stopApplicationBtnName = "Close_Application";
  private @NonNull String              extensionBtn           = ".btn";
  private @NonNull String              extensionEdit          = ".edit";
  private          long                surveyLoopSleepMs      = 100;
  private @NonNull Consumer<Throwable> errHandler             = Throwable::printStackTrace;

  public @NonNull Fui build() {
    if (rootDir == null) {
      throw new IllegalArgumentException("gQWAfWL4R3 :: Root directory cannot be null");
    }
    return new Fui(new Fui.Def(rootDir, stopApplicationBtnName, extensionBtn, extensionEdit, surveyLoopSleepMs, errHandler));
  }

  public FuiBuilder errHandler(@NonNull Consumer<Throwable> errHandler) {
    this.errHandler = errHandler;
    return this;
  }

  public @NonNull FuiBuilder extensionBtn(@NonNull String extensionBtn) {
    this.extensionBtn = extensionBtn;
    return this;
  }

  public @NonNull FuiBuilder rootDir(@NonNull Path rootDir) {
    this.rootDir = rootDir;
    return this;
  }

  public @NonNull FuiBuilder stopApplicationBtnName(@NonNull String stopApplicationBtnName) {
    this.stopApplicationBtnName = stopApplicationBtnName;
    return this;
  }

  public @NonNull FuiBuilder extensionEdit(@NonNull String extensionEdit) {
    this.extensionEdit = extensionEdit;
    return this;
  }

  public @NonNull FuiBuilder surveyLoopSleepMs(long surveyLoopSleepMs) {
    this.surveyLoopSleepMs = surveyLoopSleepMs;
    return this;
  }
}
