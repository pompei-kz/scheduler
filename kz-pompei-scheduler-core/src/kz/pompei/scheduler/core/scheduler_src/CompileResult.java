package kz.pompei.scheduler.core.scheduler_src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kz.pompei.hotconfig.core.model.Conf;
import kz.pompei.hotconfig.core.model.ConfParam;
import lombok.NonNull;

/**
 * Result of compilation of configuration parameters into schedule sources.
 */
public class CompileResult {

  /**
   * TODO add Javadoc here
   */
  public final Map<String, CompiledScheduleSrc> taskName_to_scheduleSrc = new HashMap<>();

  /**
   * List of compilation error messages. The task this message belongs to will be stated in the message itself.
   */
  public final List<String> noticeMessages = new ArrayList<>();

  public void assignTo(@NonNull Conf conf) {
    for (ConfParam param : conf.params) {
      CompiledScheduleSrc src = taskName_to_scheduleSrc.get(param.name);
      if (src == null) {
        param.error = null;
      } else {
        param.error = src.error;
      }
    }
  }
}
