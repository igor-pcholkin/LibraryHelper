import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

public class SampleDialogWrapper extends DialogWrapper {

  public SampleDialogWrapper() {
    super(true); // use current window as parent
    init();
    setTitle("Test DialogWrapper");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    JPanel dialogPanel = new JPanel(new BorderLayout());

    JLabel label = new JLabel("testing");
    label.setPreferredSize(new Dimension(100, 100));

    ComboBox combo = new ComboBox();
    List<String> categories = readCategoriesFromFile();

    for (String category: categories) {
      combo.addItem(category);
    }

    AutoCompleteDecorator.decorate(combo);

    dialogPanel.add(combo, BorderLayout.CENTER);

    return dialogPanel;
  }

  private List<String> readCategoriesFromFile() {
    List categories = new LinkedList();
    Scanner scanner = null;
    InputStream in = this.getClass().getClassLoader()
            .getResourceAsStream("top_level_categories.txt");
    scanner = new Scanner(in);
    while (scanner.hasNextLine()){
      categories.add(scanner.nextLine());
    }

    scanner.close();

    return categories;
  }
}