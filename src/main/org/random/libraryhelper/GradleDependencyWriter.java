package org.random.libraryhelper;

import java.io.IOException;

import static util.FileUtil.readFileToString;
import static util.FileUtil.writeStringToFile;

public class GradleDependencyWriter {
  public void addToGradleProject(Artefact artefact, String version) {
    try {
      String buildGradleContents = readFileToString("build.gradle");
      String newBuildGradleContents = addDependencyToContents(artefact, version, buildGradleContents);
      writeStringToFile(newBuildGradleContents, "build.gradle");
    } catch (IOException ex) {
      //
    }
  }

  public String addDependencyToContents(Artefact artefact, String version, String buildGradleContents) {
    String dependenciesPrefix = "dependencies {";
    int sectionStartIndex = buildGradleContents.indexOf(dependenciesPrefix);
    if (sectionStartIndex != -1) {
      int newLineIndex = buildGradleContents.indexOf('\n',
              sectionStartIndex + dependenciesPrefix.length());
      if (newLineIndex != -1) {
        int numEmptyCharsBeforeFirstDependency = getNumEmptyCharsBeforeFirstDependency(buildGradleContents,
                newLineIndex + 1);
        String newDependencyAsString = String.format("compile group: \'%s\', name: '%s', version:\'%s\'\n",
                artefact.getGroupId(), artefact.getArtefactId(), version);
        String oldFilePrefix = buildGradleContents.substring(0, newLineIndex + 1);
        String emptyCharsPrefix = buildGradleContents.substring(newLineIndex + 1, newLineIndex + 1 +
                numEmptyCharsBeforeFirstDependency);
        if (emptyCharsPrefix.length() == 0) {
          emptyCharsPrefix = "  ";
        }
        return String.format("%s%s%s%s", oldFilePrefix, emptyCharsPrefix, newDependencyAsString,
                buildGradleContents.substring(oldFilePrefix.length()));
      }
    }
    return buildGradleContents;
  }

  private int getNumEmptyCharsBeforeFirstDependency(String buildGradleContents, int i) {
    int buildGradleContentsLen = buildGradleContents.length();
    int n = 0;
    for (; i < buildGradleContentsLen; i++, n++) {
      String ch = String.valueOf(buildGradleContents.charAt(i));
      if (!" \t\n".contains(ch)) {
        break;
      }
    }
    return n;
  }

}
