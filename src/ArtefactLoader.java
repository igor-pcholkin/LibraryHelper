import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ArtefactLoader implements Runnable {

  private Map<String, String> categories;
  private String category;
  private Consumer<List<Artefact>> callback;

  public ArtefactLoader(Map<String, String> categories, String category, Consumer<List<Artefact>> callback) {
    super();

    this.categories = categories;
    this.category = category;
    this.callback = callback;
  }

  private List<Artefact> fetchArtifacts(String category) throws IOException {
    String categoryUrl = categories.get(category);
    List<Artefact> artifacts = new LinkedList<>();
    if (categoryUrl != null) {
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
          artifacts.add(new Artefact(artifactsInPage.get(i), artifactsInPage.get(i + size),
                  artifactsInPage.get(i + size * 2)));
        }
        readNextPage = size > 0;
      }
    }
    return artifacts;
  }

  private static List<String> fetchArtifactsPage(int nPage, String relativeCategoryUrl, HttpClient httpClient,
                                                 XPathEvaluator xPathEvaluator) throws IOException {
    String baseUrl = "https://mvnrepository.com";
    HttpGet request = new HttpGet(baseUrl + relativeCategoryUrl + "?p=" + nPage);
    HttpResponse response = httpClient.execute(request);
    Document document = Jsoup.parse(response.getEntity().getContent(), null, baseUrl);
    response.getEntity().getContent().close();
    return xPathEvaluator.evaluate(document).list();
  }

  @Override
  public void run() {
    try {
      List<Artefact> artefacts = fetchArtifacts(category);
      callback.accept(artefacts);
    } catch (IOException e) {
    }
  }
}
