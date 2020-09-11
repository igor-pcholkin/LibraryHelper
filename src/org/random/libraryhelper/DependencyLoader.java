package org.random.libraryhelper;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.version.Version;
import util.FetchDependenciesTest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DependencyLoader {
  private RemoteRepository central;
  private RepositorySystem repoSystem;
  private RepositorySystemSession session;

  public DependencyLoader() {
    central = new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build();
    repoSystem = newRepositorySystem();
    session = newSession(repoSystem);
  }

  public List<String> loadVersions(Artefact artefact) {
    Artifact artifact = new DefaultArtifact(String.format("%s:%s:(0,]", artefact.getGroupId(), artefact.getArtefactId()));
    VersionRangeRequest request = new VersionRangeRequest(artifact, Arrays.asList(central), null);
    VersionRangeResult versionResult = null;
    try {
      versionResult = repoSystem.resolveVersionRange(session, request);
    } catch (VersionRangeResolutionException e) {
      return Collections.emptyList();
    }
    return versionResult.getVersions().stream().map(Version::toString).collect(Collectors.toList());
  }

  private static RepositorySystem newRepositorySystem() {
    DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
    locator.addService(TransporterFactory.class, FileTransporterFactory.class);
    locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
    return locator.getService(RepositorySystem.class);
  }

  private static RepositorySystemSession newSession(RepositorySystem system) {
    DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    LocalRepository localRepo = new LocalRepository("target/local-repo");
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
    return session;
  }

  public List<Dependency> loadLocalInfo(String category) {
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

  public List<String> readCategories() {
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
