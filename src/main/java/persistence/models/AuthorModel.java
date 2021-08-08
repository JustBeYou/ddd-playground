package persistence.models;

import di.DIManager;
import domain.Author;
import domain.Country;
import lombok.Data;
import lombok.NonNull;
import persistence.base.MappableModel;
import persistence.base.Repository;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;
import persistence.base.relations.RelatedField;
import persistence.base.relations.RelationType;
import persistence.base.serialization.Field;
import persistence.base.serialization.FieldType;
import persistence.base.serialization.FieldsMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Data
public class AuthorModel implements MappableModel<Author> {
  public final String name = "Author";
  private final Repository<Author> associatedRepository = DIManager.getInstance().get("AuthorRepository");

  private Integer id;
  @NonNull
  private Author data;

  public AuthorModel(Author author) {
    this.data = author;
  }

  public AuthorModel(Integer id) throws InvalidStorageReferenceException, UnknownModelException {
    this.id = id;
    var foundModel = this.associatedRepository.findById(id);
    if (foundModel.isEmpty()) {
      throw new InvalidStorageReferenceException(this.getName(), id);
    }
    this.data = foundModel.get().getData();
  }

  public void setData(@NonNull Author data) {
    this.data = data;
  }

  @Override
  public RelatedField[] getRelatedFields() {
    return new RelatedField[]{
      new RelatedField(
        RelationType.ONE_OWNS_MANY,
        "Author",
        "name",
        new Field("books", FieldType.Reference),
        "Book",
        "authorName",
        new Field("author", FieldType.Reference)
      )
    };
  }

  @Override
  public FieldsMap map() {
    var map = new HashMap<String, Field>();
    if (this.id != null) {
      map.put("id", new Field("id", FieldType.Integer, this.id.toString()));
    }
    map.put("name", new Field("name", FieldType.String, this.data.getName()));
    map.put("country", new Field("country", FieldType.String, this.data.getCountry().toString()));
    return new FieldsMap(map, "Author");
  }

  @Override
  public Author unmap(FieldsMap map) {
    assert map.getName().equals("Author");
    var name = map.getMap().get("name").getValue();
    var country = map.getMap().get("country").getValue();
    this.data = new Author(name, Country.valueOf(country));
    var id = map.getMap().get("id");
    if (id != null) {
      this.id = Integer.valueOf(id.getValue());
    }
    return this.data;
  }

  @Override
  public Author unmapIfSet(Author exitingData, FieldsMap map) {
    assert map.getName().equals("Author");
    if (!map.getMap().containsKey("name")) {
      map.getMap().put("name", new Field("name", FieldType.String, exitingData.getName()));
    }
    if (!map.getMap().containsKey("country")) {
      map.getMap().put("country", new Field("country", FieldType.String, exitingData.getCountry().toString()));
    }

    return this.unmap(map);
  }

  @Override
  public Collection<Field> getFields() {
    var fields = new ArrayList<Field>();
    fields.add(new Field("id", FieldType.Integer));
    fields.add(new Field("name", FieldType.String));
    fields.add(new Field("country", FieldType.String));
    fields.add(new Field("books", FieldType.Reference));
    return fields;
  }

  @Override
  public void loadRelationField(String field, Object object) {
    if (field.equals("books")) {
      var entities = (Collection<BookModel>) object;
      var books = this.data.getBooks();
      if (!books.isEmpty()) {
        books.clear();
      }
      books.addAll(entities.stream().map(entity -> entity.getData()).collect(Collectors.toList()));
    }
  }
}


