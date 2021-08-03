package persistence.models;

import domain.Book;
import persistence.base.MappableModel;
import persistence.base.ModelFactory;
import persistence.base.exceptions.UnknownModelException;

public class BooksModelsFactory implements ModelFactory {
    @Override
    public <T> MappableModel<T> build(String modelName, T data) throws UnknownModelException {
        switch (modelName) {
            case "Book":
                return (MappableModel<T>) new BookModel((Book) data);
            default:
                throw new UnknownModelException(modelName);
        }
    }
}
