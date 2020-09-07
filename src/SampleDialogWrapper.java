import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import util.FetchArtifacts;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class SampleDialogWrapper extends DialogWrapper {

  ComboBox categoriesComboBox;
  JList artefactsUIList;

  Map<String, String> categories;
  AtomicInteger artifactLoadingsInProgress ;
  volatile String lastUpdatedCategory;

  public SampleDialogWrapper() {
    super(true); // use current window as parent

    categories = readTopCategoriesFromFile();

    init();
    setTitle("Choose dependency");

    artifactLoadingsInProgress = new AtomicInteger(0);
    updateArtifactListForCategoryComboBox();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {

    JPanel dialogPanel = new JPanel(new BorderLayout());
    dialogPanel.setPreferredSize(new Dimension(300, 500));

    categoriesComboBox = new ComboBox();
    artefactsUIList = new JBList();

    for (String category: categories.keySet()) {
      categoriesComboBox.addItem(category);
    }

    AutoCompleteDecorator.decorate(categoriesComboBox);

    categoriesComboBox.addActionListener(e -> updateArtifactListForCategoryComboBox());

    dialogPanel.add(categoriesComboBox, BorderLayout.NORTH);
    dialogPanel.add(new JScrollPane(artefactsUIList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER),
            BorderLayout.CENTER);

    return dialogPanel;
  }

  private void updateArtifactListForCategoryComboBox() {
    while (artifactLoadingsInProgress.get() > 0) {
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        //
      }
    }
    String selectedCategory = categoriesComboBox.getSelectedItem().toString();
    if (!selectedCategory.equals(lastUpdatedCategory)) {
      lastUpdatedCategory = selectedCategory;
      artifactLoadingsInProgress.incrementAndGet();
      new Thread(new ArtefactLoader(categories, selectedCategory, artefacts -> {
        artefactsUIList.setListData(artefacts.stream().map(Artefact::getName).collect(Collectors.toList()).toArray());
        artifactLoadingsInProgress.decrementAndGet();
      })).start();
    }
  }

  private static Map<String, String> readTopCategoriesFromFile() {
    Map<String, String> categories = new HashMap<>();
    InputStream in = FetchArtifacts.class.getClassLoader()
            .getResourceAsStream("top_level_categories.txt");
    Scanner scanner = new Scanner(in);
    while (scanner.hasNextLine()) {
      String lineParts[] = scanner.nextLine().split(":");
      categories.put(lineParts[0], lineParts[1]);
    }
    scanner.close();
    return categories;
  }

}