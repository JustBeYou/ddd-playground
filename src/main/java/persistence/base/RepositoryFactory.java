package persistence.base;

public interface RepositoryFactory {
    Repository<?> build(String name);
}
