package com.random.libraryhelper;

import java.io.IOException;

import static util.FileUtil.readFileToString;
import static util.FileUtil.writeStringToFile;

public class GradleDependencyWriter {
  public void addToGradleProject(Artefact artefact, String version, String buildGradlePath) throws Exception {
    String buildGradleContents = readFileToString(buildGradlePath);
    String newBuildGradleContents = addDependencyToContents(artefact, version, buildGradleContents);
    writeStringToFile(newBuildGradleContents, buildGradlePath);
  }

  public String addDependencyToContents(Artefact artefact, String version, String buildGradleContents) {
    String dependenciesPrefix = "dependencies {";
    int sectionStartIndex = findDependenciesBlock(buildGradleContents, dependenciesPrefix);
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

  int findDependenciesBlock(String buildGradleContents, String dependenciesPrefix) {
    int i = -1;
    do {
      i = buildGradleContents.indexOf(dependenciesPrefix, ++i);
      if (i == -1) {
        return -1;
      }
    } while (i > 0 && buildGradleContents.charAt(i - 1) != '\n');
    return i;
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
