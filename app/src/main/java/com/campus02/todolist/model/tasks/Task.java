package com.campus02.todolist.model.tasks;

public class Task implements Comparable<Task> {

  private Integer id;
  private String title;
  private String description;
  private boolean isPublic;
  private boolean isCompleted;
  private Integer originatorUserId;
  private Integer lastModifiedUserId;
  private Long lastModifiedTimestamp;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isPublic() {
    return this.isPublic;
  }

  public void setIsPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  public boolean isCompleted() {
    return isCompleted;
  }

  public void setIsCompleted(boolean isCompleted) {
    this.isCompleted = isCompleted;
  }

  public Integer getOriginatorUserId() {
    return originatorUserId;
  }

  public Integer getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  public Long getLastModifiedTimestamp() {
    return lastModifiedTimestamp;
  }

  @Override
  public String toString() {
    return "Task{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", isPublic=" + isPublic +
            ", isCompleted=" + isCompleted +
            ", originatorUserId=" + originatorUserId +
            ", lastModifiedUserId=" + lastModifiedUserId +
            ", lastModifiedTimestamp=" + lastModifiedTimestamp +
            '}';
  }

  @Override
  public int compareTo(Task other) {
    boolean b1 = this.isCompleted();
    boolean b2 = other.isCompleted();

    if (b1 == b2)
      return other.getId().compareTo(this.getId());
    else
      return b1 ? 1 : -1;
  }
}
