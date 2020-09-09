package org.random.libraryhelper;

public class Artefact {
  String groupId;
  String artefactId;

  public Artefact(String groupId, String artefactId) {
    this.groupId = groupId;
    this.artefactId = artefactId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtefactId() {
    return artefactId;
  }

  public void setArtefactId(String artefactId) {
    this.artefactId = artefactId;
  }

  @Override
  public String toString() {
    return "Artefact{" +
            "groupId='" + groupId + '\'' +
            ", artefactId='" + artefactId + '\'' +
            '}';
  }
}
