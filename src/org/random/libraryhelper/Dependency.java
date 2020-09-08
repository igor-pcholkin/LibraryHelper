package org.random.libraryhelper;

public class Dependency {
  String group;
  String artfefact;
  String name;
  String description;

  public Dependency(String group, String artefact, String name, String description) {
    this.group = group;
    this.artfefact = artefact;
    this.name = name;
    this.description = description;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getArtfefact() {
    return artfefact;
  }

  public void setArtfefact(String artfefact) {
    this.artfefact = artfefact;
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

  @Override
  public String toString() {
    return "Dependency{" +
            "group='" + group + '\'' +
            ", artfefact='" + artfefact + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}';
  }
}

