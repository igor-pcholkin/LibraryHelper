import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class PopupDialogAction extends AnAction {
  @Override
  public void actionPerformed(AnActionEvent anActionEvent) {
    if (new SampleDialogWrapper().showAndGet()) {
      System.out.println("Blabla");
    }
  }
}
