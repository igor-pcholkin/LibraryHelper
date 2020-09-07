package util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FetchArtifacts {
  public static void main(String args[]) throws IOException, XPathExpressionException {
    if (args.length == 0) {
      return;
    }
    Map<String, String> topLevelCategories = readTopCategories();
    String categoryUrl = topLevelCategories.get(args[0]);
    HttpClient httpClient = HttpClientBuilder.create().build();
    XPathEvaluator xPathEvaluator = Xsoup.compile(
            "div[@class=im]/div/h2/a[@class!=im-usage]/@href|" +
                     "div[@class=im]/div/h2/a[@class!=im-usage]/text()|" +
                     "div[@class=im]/div[@class=im-description]/text()");
    boolean readNextPage = true;
    for (int page = 1; readNextPage; page++) {
      List<String> artifactsInPage = fetchArtifactsPage(page, categoryUrl, httpClient, xPathEvaluator);
      int size = artifactsInPage.size() / 3;
      for (int i = 0; i < size; i++) {
        System.out.println("href: " + artifactsInPage.get(i));
        System.out.println("Text: " + artifactsInPage.get(i + size));
        System.out.println("Description: " + artifactsInPage.get(i + size * 2) + "\n");
      }
      readNextPage = size > 0;
    }
  }

  private static Map<String, String> readTopCategories() {
    Map<String, String> categories = new HashMap<>();
    InputStream in = FetchArtifacts.class.getClassLoader()
            .getResourceAsStream("top_level_categories.txt");
    Scanner scanner = new Scanner(in);
    while (scanner.hasNextLine()) {
      String lineParts[] = scanner.nextLine().split(":");
      categories.put(lineParts[0], lineParts[1]);
    }
    scanner.close();
    return categories;
  }

  private static List<String> fetchArtifactsPage(int nPage, String relativeCategoryUrl, HttpClient httpClient,
                                                 XPathEvaluator xPathEvaluator)
          throws IOException {
    String baseUrl = "https://mvnrepository.com";
    HttpGet request = new HttpGet(baseUrl + relativeCategoryUrl + "?p=" + nPage);
    HttpResponse response = httpClient.execute(request);
    Document document = Jsoup.parse(response.getEntity().getContent(), null, baseUrl);
    response.getEntity().getContent().close();
    return xPathEvaluator.evaluate(document).list();
  }

}
