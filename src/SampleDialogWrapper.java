import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.Gray;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.random.libraryhelper.Dependency;
import org.random.libraryhelper.DependencyLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.stream.Collectors;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingConstants.LEFT;

public class SampleDialogWrapper extends DialogWrapper {

  ComboBox categoriesComboBox;
  JList artefactsUIList;
  JTextField artefactInfo;
  JTextArea artefactDescription;

  java.util.List<String> categories;

  volatile String lastUpdatedCategory;

  public SampleDialogWrapper() {
    super(true); // use current window as parent

    categories = DependencyLoader.readCategories();

    init();
    setTitle("Choose dependency");

    updateArtifactListForCategoryComboBox();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {

    JPanel dialogPanel = new JPanel(new BorderLayout());
    dialogPanel.setPreferredSize(new Dimension(600, 300));

    categoriesComboBox = new ComboBox();
    for (String category: categories) {
      categoriesComboBox.addItem(category);
    }
    AutoCompleteDecorator.decorate(categoriesComboBox);
    categoriesComboBox.addActionListener(e -> updateArtifactListForCategoryComboBox());

    artefactsUIList = new JBList();
    artefactsUIList.addListSelectionListener(e -> updateArtefactInfo());

    artefactInfo = new JTextField();
    artefactInfo.setEditable(false);
    artefactInfo.setBackground(Gray._220);
    artefactDescription = new JTextArea();
    artefactDescription.setEditable(false);
    artefactDescription.setLineWrap(true);
    artefactDescription.setFont(artefactInfo.getFont());
    artefactDescription.setBackground(Gray._220);

    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(categoriesComboBox, BorderLayout.NORTH);
    JScrollPane scrollPane = new JBScrollPane(artefactsUIList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
    leftPanel.add(scrollPane, BorderLayout.CENTER);
    leftPanel.setMinimumSize(new Dimension(100, 300));
    leftPanel.setBorder(BorderFactory.createEmptyBorder());

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(artefactInfo, BorderLayout.NORTH);
    rightPanel.add(artefactDescription, BorderLayout.CENTER);
    rightPanel.setMinimumSize(new Dimension(100, 300));

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
    splitPane.setOneTouchExpandable(true);
    splitPane.setContinuousLayout(true);
    splitPane.setDividerLocation(200);
    splitPane.setBorder(BorderFactory.createEmptyBorder());

    dialogPanel.add(splitPane);

    return dialogPanel;
  }

  private void updateArtefactInfo() {
    if (artefactsUIList.getSelectedIndex() >= 0) {
      Dependency dependency = (Dependency) artefactsUIList.getModel().getElementAt(artefactsUIList.getSelectedIndex());
      artefactInfo.setText(dependency.getGroup() + ":" + dependency.getArtfefact());
      artefactDescription.setText(dependency.getDescription());
    } else {
      artefactInfo.setText("");
      artefactDescription.setText("");
    }
  }

  private void updateArtifactListForCategoryComboBox() {
    String selectedCategory = categoriesComboBox.getSelectedItem().toString();
    if (!selectedCategory.equals(lastUpdatedCategory)) {
      lastUpdatedCategory = selectedCategory;
      java.util.List<Dependency> dependencies = DependencyLoader.load(selectedCategory);
      artefactsUIList.setListData(dependencies.toArray());
      artefactsUIList.setCellRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
          Component defaultComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
          Dependency dependency = (Dependency) list.getModel().getElementAt(index);
          setText(dependency.getName());
          return defaultComponent;
        }
      });
    }
  }

}