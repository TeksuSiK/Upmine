package pl.teksusik.upmine.storage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    void createTablesIfNotExists();
    long count();
    T save(T model);
    Optional<T> findByUuid(UUID uuid);
    List<T> findAll();
    boolean deleteByUuid(UUID uuid);
}
