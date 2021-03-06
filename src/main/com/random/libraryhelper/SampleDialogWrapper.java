package com.random.libraryhelper;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.Gray;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class SampleDialogWrapper extends DialogWrapper {

  JComboBox categoriesComboBox;
  JComboBox versionsComboBox;
  JList artefactsUIList;
  JTextArea artefactDescription;

  java.util.List<String> categories;

  volatile String lastUpdatedCategory;
  DependencyLoader dependencyLoader;

  Project project;

  public SampleDialogWrapper(Project project) {
    super(true); // use current window as parent

    this.project = project;

    dependencyLoader = new DependencyLoader();
    categories = dependencyLoader.readCategories();

    init();
    setTitle("Choose dependency");

    updateArtifactListForCategoryComboBox();
  }

  @Override
  protected Action [] createActions() {
    return new Action[] { new AddAction(), getOKAction() };
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {

    JPanel dialogPanel = new JPanel(new BorderLayout());
    dialogPanel.setPreferredSize(new Dimension(600, 300));

    setOKButtonText("Close");

    categoriesComboBox = new ComboBox();
    for (String category: categories) {
      categoriesComboBox.addItem(category);
    }
    AutoCompleteDecorator.decorate(categoriesComboBox);
    categoriesComboBox.addActionListener(e -> updateArtifactListForCategoryComboBox());

    artefactsUIList = new JBList();
    artefactsUIList.addListSelectionListener(e -> updateArtefactInfo());

    artefactDescription = new JTextArea();
    artefactDescription.setEditable(false);
    artefactDescription.setLineWrap(true);
    artefactDescription.setBackground(Gray._220);

    versionsComboBox = new ComboBox();
    versionsComboBox.setBackground(Gray._250);

    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(categoriesComboBox, BorderLayout.NORTH);
    JScrollPane scrollPane = new JBScrollPane(artefactsUIList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
    leftPanel.add(scrollPane, BorderLayout.CENTER);
    leftPanel.setMinimumSize(new Dimension(100, 300));
    leftPanel.setBorder(BorderFactory.createEmptyBorder());

    JPanel rightPanel = new JPanel(new BorderLayout());
    JPanel versionsPanel = new JPanel(new BorderLayout());
    versionsPanel.add(new JLabel("Versions: "), BorderLayout.WEST);
    versionsPanel.add(versionsComboBox, BorderLayout.CENTER);
    rightPanel.add(artefactDescription, BorderLayout.CENTER);
    rightPanel.add(versionsPanel, BorderLayout.SOUTH);
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
      updateUIFromLocalDependencyInfo(dependency);
      loadVersionsAndUpdateUI(dependency);
    } else {
      artefactDescription.setText("");
      versionsComboBox.removeAllItems();
    }
  }

  private void updateUIFromLocalDependencyInfo(Dependency dependency) {
    String allArtefacts = dependency.getArtefacts().stream().map(a -> a.getGroupId() + ":" + a.getArtefactId())
            .collect(joining( "," ));
    artefactDescription.setText(allArtefacts + "\n\n" + dependency.getDescription());
  }

  private void loadVersionsAndUpdateUI(Dependency dependency) {
    java.util.List<String> versions = dependency.getArtefacts().stream().flatMap(artefactDescription ->
            dependencyLoader.loadVersions(artefactDescription)
            .stream()).collect(Collectors.toList());
    versionsComboBox.removeAllItems();
    for (String version: versions) {
      versionsComboBox.addItem(version);
    }
  }

  private void updateArtifactListForCategoryComboBox() {
    String selectedCategory = categoriesComboBox.getSelectedItem().toString();
    if (!selectedCategory.equals(lastUpdatedCategory)) {
      lastUpdatedCategory = selectedCategory;
      java.util.List<Dependency> dependencies = dependencyLoader.loadLocalInfo(selectedCategory);
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

  private class AddAction extends AbstractAction {
    AddAction() {
      super("Add");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      GradleDependencyWriter gradleDependencyWriter = new GradleDependencyWriter();
      if (artefactsUIList.getSelectedIndex() != -1 && versionsComboBox.getSelectedIndex() != -1) {
        Dependency dependency = (Dependency) artefactsUIList.getModel().getElementAt(artefactsUIList.getSelectedIndex());
        String version = (String) versionsComboBox.getSelectedItem();
        try {
          for (Artefact artefact: dependency.getArtefacts()) {
            gradleDependencyWriter.addToGradleProject(artefact, version, project.getBasePath() + "/build.gradle");
          }
        }
        catch (Exception ex) {
          JOptionPane.showMessageDialog(getContentPane(), ExceptionUtils.getStackTrace(ex));
        }
      }
    }
  }
}