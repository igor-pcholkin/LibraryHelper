package com.random.libraryhelper;

import java.util.List;

public class Dependency {
  List<Artefact> artefacts;
  String name;
  String description;

  public Dependency(List<Artefact> artefacts, String name, String description) {
    this.artefacts = artefacts;
    this.name = name;
    this.description = description;
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

  public List<Artefact> getArtefacts() {
    return artefacts;
  }

  public void setArtefacts(List<Artefact> artefacts) {
    this.artefacts = artefacts;
  }

  @Override
  public String toString() {
    return "Dependency{" +
            "artefacts=" + artefacts +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}';
  }
}

