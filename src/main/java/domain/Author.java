package domain;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Author {
  @NonNull
  private String name;
  private Country country;

  @NonNull
  private Collection<Book> books;

  public Author(@NonNull String name, Country country) {
    this.name = name;
    this.country = country;
    this.books = new ArrayList<>();
  }
}
