package test;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class FetchTopLevelCategoriesTest {
  public static void main(String args[]) throws IOException, XPathExpressionException {
    HttpClient httpClient = HttpClientBuilder.create().build();
    List<String> categories = new LinkedList<>();
    XPathEvaluator xPathEvaluator = Xsoup.compile("div/h4/a/text()|div/h4/a/@href");
    for (int i = 1; i <= 10; i++) {
      List<String> categoriesInPage = fetchPage(i, httpClient, xPathEvaluator);
      int nCategories = categoriesInPage.size() / 2;
      for (int j = 0; j < nCategories; j++) {
        categories.add(categoriesInPage.get(j) + ":" + categoriesInPage.get(j + nCategories));
      }
    }
    FileWriter fileWriter = new FileWriter("resources/top_level_categories.txt");
    for (String category: categories) {
      fileWriter.write(category + "\n");
    }
    fileWriter.close();
  }

  private static List<String> fetchPage(int n, HttpClient httpClient, XPathEvaluator xPathEvaluator)
          throws IOException {
    String baseUrl = "https://mvnrepository.com";
    HttpGet request = new HttpGet(baseUrl + "/open-source?p=" + n);
    HttpResponse response = httpClient.execute(request);
    Document document = Jsoup.parse(response.getEntity().getContent(), null, baseUrl);
    response.getEntity().getContent().close();
    return xPathEvaluator.evaluate(document).list();
  }

}
