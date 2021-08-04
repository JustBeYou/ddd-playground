package persistence.models;

import di.DIManager;
import domain.Book;
import lombok.Data;
import lombok.NonNull;
import persistence.base.*;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;

import java.util.HashMap;

@Data
public class BookModel implements MappableModel<Book> {
    public final String name = "Book";
    private final Repository<Book> associatedRepository = DIManager.getInstance().get("BookRepository");

    private Integer id;
    @NonNull private Book data;

    public BookModel(Book book) {
        this.data = book;
    }

    public BookModel(Integer id) throws InvalidStorageReferenceException, UnknownModelException {
        this.id = id;
        var foundModel = this.associatedRepository.findById(id);
        if (foundModel.isEmpty()) {
            throw new InvalidStorageReferenceException(this.getName(), id);
        }
        this.data = foundModel.get().getData();
    }

    public void setData(@NonNull Book data) {
        this.data = data;
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

    @Override
    public Book unmapIfSet(Book exitingData, FieldsMap map) {
        assert map.getName().equals("Book");
        if (!map.getMap().containsKey("name")) {
            map.getMap().put("name", new Field("name", FieldType.String, exitingData.getName()));
        }
        if (!map.getMap().containsKey("ISBN")) {
            map.getMap().put("ISBN", new Field("ISBN", FieldType.String, exitingData.getISBN()));
        }
        if (!map.getMap().containsKey("publishedAt")) {
            map.getMap().put("publishedAt", new Field("publishedAt", FieldType.String, exitingData.getPublishedAt()));
        }

        return this.unmap(map);
    }
}

