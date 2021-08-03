package persistence.models;

import di.DIManager;
import domain.Book;
import lombok.Data;
import lombok.NonNull;
import persistence.base.*;

import java.util.HashMap;

@Data
public class BookModel implements MappableModel<Book> {
    private Integer id;
    @NonNull
    private Book data;

    public BookModel(Book book) {
        this.data = book;
    }

    public BookModel(Integer id) throws Exception {
        this.id = id;
        this.reload();
    }

    @Override
    public FieldsMap map() {
        var map = new HashMap<String, Field>();
        if (this.id != null) {
            map.put("id", new Field("id", FieldType.Integer, this.id.toString()));
        }
        map.put("name", new Field("name", FieldType.String, this.data.getName()));
        map.put("ISBN", new Field("ISBN", FieldType.String, this.data.getISBN()));
        map.put("publishedAt", new Field("publishedAt", FieldType.String, this.data.getPublishedAt()));
        return new FieldsMap(map, "Book");
    }

    @Override
    public domain.Book unmap(FieldsMap map) {
        assert map.getName().equals("Book");
        var name = map.getMap().get("name").getValue();
        var ISBN = map.getMap().get("ISBN").getValue();
        var publishedAt = map.getMap().get("publishedAt").getValue();
        this.data = new domain.Book(name, ISBN, publishedAt);
        var id = map.getMap().get("id");
        if (id != null) {
            this.id = Integer.valueOf(id.getValue());
        }
        return this.data;
    }

    Repository<domain.Book> bookRepository = DIManager.getInstance().get("BookRepository");

    @Override
    public void save() {
        bookRepository.update(this, this.map());
    }

    @Override
    public void delete() {
        bookRepository.delete(this);
    }

    @Override
    public void reload() throws Exception {
        var newValue = bookRepository.findById(this.id);
        if (newValue.isPresent()) {
            this.data = newValue.get();
        } else {
            throw new Exception("[Book Model] Record not present");
        }
    }
}

