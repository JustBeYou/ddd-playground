package persistence.drivers.inmemory;

public interface RepoFinder {
    InMemoryRepository<Object> getRepo(String name);
}
