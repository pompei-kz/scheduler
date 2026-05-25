package kz.pompei.scheduler.core.scheduler_src;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

// TODO add Javadoc here
@RequiredArgsConstructor
public class CompiledScheduleSrc {
  public final @NonNull  ScheduleSrc src;
  public final @Nullable String      error;
}
