package edu.eci.dosw.tdd.persistence.relacional.repository;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.port.UserRepositoryPort;
import edu.eci.dosw.tdd.persistence.relacional.mapper.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("relational")
@RequiredArgsConstructor
public class UserRepositoryJpaImpl implements UserRepositoryPort {

    private final UserRepository repository;

    @Override
    public User save(User user) {
        return UserPersistenceMapper.toDomain(
                repository.save(UserPersistenceMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream()
                .map(UserPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}