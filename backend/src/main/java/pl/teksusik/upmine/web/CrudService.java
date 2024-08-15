package pl.teksusik.upmine.web;

import pl.teksusik.upmine.storage.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class CrudService<T, DTO> {
    protected final Repository<T> repository;

    public CrudService(Repository<T> repository) {
        this.repository = repository;
    }

    public long count() {
        return this.repository.count();
    }

    public T save(T monitor) {
        return this.repository.save(monitor);
    }

    public Optional<T> findByUuid(UUID uuid) {
        return this.repository.findByUuid(uuid);
    }

    public List<T> findAll() {
        return this.repository.findAll();
    }

    public boolean deleteByUuid(UUID uuid) {
        return this.repository.deleteByUuid(uuid);
    }

    public abstract Optional<T> create(DTO dto);
    public abstract Optional<T> update(UUID uuid, DTO dto);
}
