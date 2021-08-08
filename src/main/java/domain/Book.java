package domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class Book {
  @NonNull
  private String name;
  @NonNull
  private String ISBN;
  @NonNull
  private String publishedAt;

  @NonNull
  private String authorName;
  private Author author;

  public Book(@NonNull String name, @NonNull String ISBN, @NonNull String publishedAt, @NonNull String authorName) {
    this.name = name;
    this.ISBN = ISBN;
    this.publishedAt = publishedAt;
    this.authorName = authorName;
  }
}
