package br.com.postechfiap.toloni.restauranthub.adapters.gateways;

import br.com.postechfiap.toloni.restauranthub.adapters.shared.PageRequestMapper;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserWithTypeName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.UserJpaEntity;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.shared.JpaSpecificationBuilder;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

/// Adapter that implements [UserGateway] using Spring Data JPA.
///
/// Bridges the domain layer and the persistence layer, converting between
/// [User] domain entities and [UserJpaEntity] persistence models.
public class UserGatewayImpl implements UserGateway {

    private final UserJpaRepository jpaRepository;

    /// @param jpaRepository the Spring Data JPA repository for [UserJpaEntity]
    public UserGatewayImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /// {@inheritDoc}
    @Override
    public User save(User user) {
        var entity = UserJpaEntity.fromDomain(user);
        var saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    /// {@inheritDoc}
    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.getValue())
                .map(UserJpaEntity::toDomain);
    }

    /// {@inheritDoc}
    @Override
    public Optional<UserWithTypeName> findByIdWithUserTypeName(UserId id) {
        return jpaRepository.findById(id.getValue())
                .map(entity -> new UserWithTypeName(
                        entity.toDomain(),
                        entity.getUserType() != null ? entity.getUserType().getName() : null
                ));
    }

    /// {@inheritDoc}
    @Override
    public Page<UserWithTypeName> findAllWithUserTypeName(PageRequest pageRequest) {
        var pageable = PageRequestMapper.toPageable(pageRequest);
        var page = pageRequest.hasFilters()
                ? jpaRepository.findAll(JpaSpecificationBuilder.fromFilters(pageRequest.getFilters()), pageable)
                : jpaRepository.findAll(pageable);

        return Page.of(
                page.getContent().stream()
                        .map(entity -> new UserWithTypeName(
                                entity.toDomain(),
                                entity.getUserType() != null ? entity.getUserType().getName() : null
                        ))
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    /// {@inheritDoc}
    @Override
    public Page<User> findAll(PageRequest pageRequest) {
        var pageable = PageRequestMapper.toPageable(pageRequest);
        var page = pageRequest.hasFilters()
                ? jpaRepository.findAll(JpaSpecificationBuilder.fromFilters(pageRequest.getFilters()), pageable)
                : jpaRepository.findAll(pageable);

        return Page.of(
                page.getContent().stream()
                        .map(UserJpaEntity::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    /// {@inheritDoc}
    @Override
    public void deleteById(UserId id) {
        try {
            jpaRepository.deleteById(id.getValue());
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException("User", id.getValue().toString());
        }
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByEmail(UserEmail email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByEmailAndIdNot(UserEmail email, UserId id) {
        return jpaRepository.existsByEmailAndIdNot(email.getValue(), id.getValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean existsById(UserId id) {
        return jpaRepository.existsById(id.getValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByUserTypeId(UserTypeId userTypeId) {
        return jpaRepository.existsByUserTypeId(userTypeId.getValue());
    }
}
