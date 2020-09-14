package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
  public static String readFileToString(String fileName) throws IOException {
    return new String(Files.readAllBytes(Paths.get(fileName)));
  }

  public static void writeStringToFile(String s, String fileName) {
    try {
      Files.write(Paths.get(fileName), s.getBytes());
    } catch (IOException e) {
      // ignore
    }
  }
}
