package kz.pompei.scheduler.core.scheduler_src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of compilation of configuration parameters into schedule sources.
 */
public class CompileResult {

  /**
   * The main compilation result. For each task, there is a schedule for running that task.
   * If there are errors in the configuration parameter, this map will not contain a schedule,
   * but {@link #noticeMessages} will contain an error message.
   */
  public final Map<String, ScheduleSrc> taskName_to_scheduleSrc = new HashMap<>();

  /**
   * List of compilation error messages. The task this message belongs to will be stated in the message itself.
   */
  public final List<String> noticeMessages = new ArrayList<>();

}
