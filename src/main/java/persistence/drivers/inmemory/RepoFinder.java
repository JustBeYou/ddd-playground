package persistence.drivers.inmemory;

/**
 * Hack for inmemory repositories
 */
public interface RepoFinder {
  InMemoryRepository<Object> getRepo(String name);
}
