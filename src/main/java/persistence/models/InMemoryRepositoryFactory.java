package persistence.models;

import persistence.base.Repository;
import persistence.base.RepositoryFactory;
import persistence.drivers.inmemory.InMemoryRepository;

public class InMemoryRepositoryFactory implements RepositoryFactory {
    @Override
    // TODO: type safety?
    public Repository<?> build(String name) {
        return switch (name) {
            case "Book" -> new InMemoryRepository<>(new BookModelFactory(), this);
            case "Author" -> new InMemoryRepository<>(new AuthorModelFactory(), this);
            case "User" -> new InMemoryRepository<>(new UserModelFactory(), this);
            case "Right" -> new InMemoryRepository<>(new RightModelFactory(), this);
            default -> null;
        };
    }
}
