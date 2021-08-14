package persistence.base;

import persistence.drivers.inmemory.InMemoryRepository;

public interface RepositoryFactory {
    InMemoryRepository<Object> build(String name);
}
