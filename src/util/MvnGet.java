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
import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class MvnGet {
  public static void main(String args[]) throws IOException, XPathExpressionException {
    HttpClient httpClient = HttpClientBuilder.create().build();
    List<String> categories = new LinkedList<>();
    XPathEvaluator xPathEvaluator = Xsoup.compile("div/h4/a/text()");
    for (int i = 1; i <= 10; i++) {
      categories.addAll(fetchPage(i, httpClient, xPathEvaluator));
    }
    FileWriter fileWriter = new FileWriter("top_level_categories.txt");
    for (String category: categories) {
      fileWriter.write(category);
    }
    fileWriter.close();
  }

  private static List<String> fetchPage(int n, HttpClient httpClient, XPathEvaluator xPathEvaluator)
          throws IOException, XPathExpressionException {
    HttpGet request = new HttpGet("https://mvnrepository.com/open-source?p=" + n);
    HttpResponse response = httpClient.execute(request);
    Document document = Jsoup.parse(response.getEntity().getContent(), null, "https://mvnrepository.com");
    response.getEntity().getContent().close();
    return xPathEvaluator.evaluate(document).list();
  }

}
