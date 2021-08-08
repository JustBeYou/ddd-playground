package persistence.models;

import domain.Book;
import persistence.base.MappableModel;
import persistence.base.models.ModelFactory;

public class BookModelFactory implements ModelFactory<Book> {

  @Override
  public String getModelName() {
    return "Book";
  }

  @Override
  public MappableModel<Book> build(Book data) {
    return new BookModel(data);
  }
}
