import org.junit.Assert;
import org.junit.Test;
import org.random.libraryhelper.Artefact;
import org.random.libraryhelper.GradleDependencyWriter;

public class GradleDependencyWriterTest {

  GradleDependencyWriter gradleDependencyWriter;
  Artefact artefact = new Artefact("com.google.guava", "guava");
  String artefactVersion = "10.0";

  public GradleDependencyWriterTest() {
    gradleDependencyWriter = new GradleDependencyWriter();
  }

  @Test
  public void testAddDependencyToExistingDependencyBlock() {
    String newContents = gradleDependencyWriter.addDependencyToContents(artefact, artefactVersion,
            "dependencies {\n" +
                    "  compile group: \'com.google.code.gson\', name: \'gson\', version:\'2.6.1\'\n" +
                    "}");
    Assert.assertEquals("dependencies {\n" +
            "  compile group: \'com.google.guava\', name: \'guava\', version:\'10.0\'\n" +
            "  compile group: \'com.google.code.gson\', name: \'gson\', version:\'2.6.1\'\n" +
            "}", newContents);
  }

  @Test
  public void testAddDependencyToEmptyDependencyBlock() {
    String newContents = gradleDependencyWriter.addDependencyToContents(artefact, artefactVersion,
            "dependencies {\n" +
                    "}");
    Assert.assertEquals("dependencies {\n" +
            "  compile group: \'com.google.guava\', name: \'guava\', version:\'10.0\'\n" +
            "}", newContents);
  }
}
