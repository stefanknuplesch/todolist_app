package com.campus02.todolist.model.tasks;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "tasks")
public class Task implements Comparable<Task> {

  @PrimaryKey
  @NonNull
  private UUID id;
  @ColumnInfo(name = "title")
  private String title;
  @ColumnInfo(name = "description")
  private String description;
  @ColumnInfo(name = "isPublic")
  private boolean isPublic;
  @ColumnInfo(name = "isCompleted")
  private boolean isCompleted;
  @ColumnInfo(name = "originatorUserId")
  private Integer originatorUserId;
  @ColumnInfo(name = "lastModifiedTimestamp")
  private Long lastModifiedTimestamp;
  @ColumnInfo(name = "isSynced")
  private boolean isSynced;
  @ColumnInfo(name = "isDeleted")
  private boolean isDeleted;

  @NonNull
  public UUID getId() {
    return id;
  }

  public void setId(@NonNull UUID id) {
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
    return isPublic;
  }

  public void setPublic(boolean aPublic) {
    isPublic = aPublic;
  }

  public boolean isCompleted() {
    return isCompleted;
  }

  public void setCompleted(boolean completed) {
    isCompleted = completed;
  }

  public Integer getOriginatorUserId() {
    return originatorUserId;
  }

  public void setOriginatorUserId(Integer originatorUserId) {
    this.originatorUserId = originatorUserId;
  }

  public Long getLastModifiedTimestamp() {
    return lastModifiedTimestamp;
  }

  public void setLastModifiedTimestamp(Long lastModifiedTimestamp) {
    this.lastModifiedTimestamp = lastModifiedTimestamp;
  }

  public boolean isSynced() {
    return isSynced;
  }

  public void setSynced(boolean synced) {
    isSynced = synced;
  }

  public boolean isDeleted() {
    return isDeleted;
  }
  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
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
