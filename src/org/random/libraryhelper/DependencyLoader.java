package org.random.libraryhelper;

import util.FetchDependenciesTest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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
      List<Artefact> artefacts = Arrays.stream(lineParts[2].split(",")).map(a -> {
        String artefactParts[] = a.split(":");
        return new Artefact(artefactParts[0], artefactParts[1]);
      }).collect(Collectors.toList());
      dependencies.add(new Dependency(artefacts, lineParts[0], lineParts[1]));
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
