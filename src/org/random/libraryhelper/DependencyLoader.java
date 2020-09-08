package org.random.libraryhelper;

import util.FetchDependenciesTest;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class DependencyLoader {

  public static List<Dependency> load(String category) {
    List<Dependency> dependencies = new LinkedList<>();
    InputStream in = FetchDependenciesTest.class.getClassLoader()
            .getResourceAsStream(String.format("categories/%s.txt", category));
    Scanner scanner;
    try {
      scanner = new Scanner(in);
    } catch (Exception ex) {
      return dependencies;
    }
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String lineParts[] = line.split("\\|");
      String artefactParts[] = lineParts[2].split(":");
      dependencies.add(new Dependency(artefactParts[0], artefactParts[1], lineParts[0], lineParts[1]));
    }
    scanner.close();
    return dependencies;

  }

  public static List<String> readCategories() {
    List<String> categories = new LinkedList<>();
    InputStream in = FetchDependenciesTest.class.getClassLoader()
            .getResourceAsStream("top_level_categories.txt");
    Scanner scanner = new Scanner(in);
    while (scanner.hasNextLine()) {
      categories.add(scanner.nextLine());
    }
    scanner.close();
    return categories;
  }


}
