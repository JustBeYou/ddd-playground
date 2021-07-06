package domain;

import di.DIManager;
import lombok.Data;
import lombok.NonNull;
import models.*;

import java.util.HashMap;

@Data
public class Book implements Model<Book>, Mappable<Book> {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private String ISBN;
    @NonNull
    private String publishedAt;

    @Override
    public ModelMap map() {
        var map = new HashMap<String, Field>();
        map.put("name", new Field("name", FieldType.String, this.name));
        map.put("ISBN", new Field("ISBN", FieldType.String, this.ISBN));
        map.put("publishedAt", new Field("publishedAt", FieldType.String, this.publishedAt));
        return new ModelMap(map, "Book");
    }

    @Override
    public Book unmap(ModelMap map) {
        assert map.getName().equals("Book");
        var name = map.getMap().get("name").getValue();
        var ISBN = map.getMap().get("ISBN").getValue();
        var publishedAt = map.getMap().get("publishedAt").getValue();
        return new Book(name, ISBN, publishedAt);
    }

    Repository<Book> bookRepository = DIManager.getInstance().get("BookRepository");

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
            var value = newValue.get();
            this.name = value.getName();
            this.ISBN = value.getISBN();
            this.publishedAt = value.getPublishedAt();
        } else {
            throw new Exception("[Book Model] Record not present anymore");
        }
    }
}
