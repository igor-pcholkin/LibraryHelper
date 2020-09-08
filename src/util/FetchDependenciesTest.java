package util;

import org.random.libraryhelper.Dependency;

import java.util.List;
import org.random.libraryhelper.DependencyLoader;

public class FetchDependenciesTest {
  public static void main(String args[]) {
    if (args.length == 0) {
      return;
    }
    List<String> categories = DependencyLoader.readCategories();
    for (String category: categories) {
      System.out.println(String.format("--- Category: %s ---\n", category));
      List<Dependency> dependencies = DependencyLoader.load(category);
      for (Dependency dependency: dependencies) {
        System.out.println(dependency);
      }
    }
  }

}
