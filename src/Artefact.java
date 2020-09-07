public class Artefact {
  String href;
  String name;
  String description;

  public Artefact(String href, String name, String description) {
    this.href = href;
    this.name = name;
    this.description = description;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}

