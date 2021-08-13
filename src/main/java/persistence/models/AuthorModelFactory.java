package persistence.models;

import domain.Author;
import persistence.base.MappableModel;
import persistence.base.models.ModelFactory;

public class AuthorModelFactory implements ModelFactory<Author> {

    @Override
    public String getModelName() {
        return "Author";
    }

    @Override
    public MappableModel<Author> build(Author data) {
        return new AuthorModel(data);
    }
}
