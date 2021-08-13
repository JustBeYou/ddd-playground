package persistence.drivers.inmemory;

import persistence.models.AuthorModelFactory;
import persistence.models.BookModelFactory;

public class InMemoryRepoFinder implements RepoFinder {
    @Override
    // TODO: type safety?
    public InMemoryRepository getRepo(String name) {
        return switch (name) {
            case "Book" -> new InMemoryRepository<>(new BookModelFactory(), this);
            case "Author" -> new InMemoryRepository<>(new AuthorModelFactory(), this);
            default -> null;
        };
    }
}
