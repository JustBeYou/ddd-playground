package persistence.models;

/*
  This file was generated by generate_model.py
  Any changes you will make to it will be lost if you run the
  script again.
 
  DO NOT MODIFY!
 */

import domain.Shelve;
import domain.Book;
import persistence.models.BookModel;


import lombok.Data;
import lombok.NonNull;
import persistence.base.MappableModel;
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
public class ShelveModel implements MappableModel<Shelve> {
    private final String name = "Shelve";

    private Integer id;
    @NonNull
    private Shelve data;

    public ShelveModel(Shelve data) {
        this.data = data;
    }

    public void setData(@NonNull Shelve data) {
        this.data = data;
    }

    @Override
    public FieldsMap map() {
        var map = new HashMap<String, Field>();
        if (this.id != null) {
            map.put("id", new Field("id", FieldType.Integer, this.id.toString()));
        }
        map.put("name", new Field("name", FieldType.String, this.data.getName().toString()));

        return new FieldsMap(map, "Shelve");
    }

    @Override
    public Shelve unmap(FieldsMap map) {
        assert map.getName().equals("Shelve");
        var name = map.getMap().get("name").getValue();
        try {
            this.data = new Shelve(name);
        } catch (Exception ignored) {
            this.data = null;
        }

        var id = map.getMap().get("id");
        if (id != null) {
            this.id = Integer.valueOf(id.getValue());
        }
        return this.data;
    }

    @Override
    public Shelve unmapIfSet(Shelve exitingData, FieldsMap map) {
        assert map.getName().equals("Shelve");
        if (!map.getMap().containsKey("name")) { map.getMap().put("name", new Field("name", FieldType.String, exitingData.getName().toString())); }

        return this.unmap(map);
    }

    @Override
    public Collection<Field> getFields() {
        var fields = new ArrayList<Field>();
        fields.add(new Field("id", FieldType.Integer));
        fields.add(new Field("name", FieldType.String));
        fields.add(new Field("books", FieldType.Reference));

        return fields;
    }

    @Override
    public void loadRelationField(String field, Object object) {
        if ("books".equals(field)){ 
            var entities = (Collection<BookModel>) object; 
            var data = this.data.getBooks();
            if (!data.isEmpty()) {
                data.clear();
            } 
            data.addAll(entities.stream().map(entity -> entity.getData()).collect(Collectors.toList()));
         }

    }

    @Override
    public RelatedField[] getRelatedFields() {
        return new RelatedField[]{
            new RelatedField(
                RelationType.ONE_OWNS_MANY,
                "Shelve",
                "name",
                new Field("books", FieldType.Reference),
                "Book",
                "shelveName",
                new Field("shelve", FieldType.Reference)
            ),
        };
    }
}