package kz.pompei.fui;

import java.nio.file.Paths;
import kz.pompei.fui.handler.Handler;
import org.testng.annotations.Test;

public class FuiTest {

  @Test
  public void application() {

    Fui fui = Fui.builder().rootDir(Paths.get("build/application")).build();

    fui.button("Hello1").click(() -> System.out.println("LZK7eKsZD1 :: Clicked BTN hello1"));
    fui.button("Hello2").click(() -> System.out.println("kIp3ygQ7PT :: Clicked BTN hello2"));

    FuiEditor stone = fui.editor("Stone");

    stone.change(() -> System.out.println("017Irf4fGM :: Stone changed to `" + stone.value.get() + "`"));

    fui.button("SetStoneTo_HELLO").click(() -> stone.value.set("HELLO"));
    fui.button("SetStoneTo_ByBy").click(() -> stone.value.set("ByBy"));

    fui.button("ReadStone").click(() -> System.out.println("ReadStone :: Stone value is `" + stone.value.get() + "`"));

    System.out.println("dgq18MmsQO :: Application started");

    FuiCheckbox done = fui.checkbox("Done");

    done.change(() -> System.out.println("Gi7f5ywo9t :: Checkbox changed to " + done.value.is()));

    fui.button("ChangeDoneTo_YES").click(() -> done.value.set(true));
    fui.button("ChangeDoneTo_NO").click(() -> done.value.set(false));
    fui.button("ReadDone").click(new Handler() {
      @Override public void handle() throws Throwable {
        System.out.println("Szj05g0Jkw :: Checkbox value is " + done.value.is());
      }
    });

    fui.go();

    System.out.println("717SnB3Dxk :: Application exited");

  }
}
