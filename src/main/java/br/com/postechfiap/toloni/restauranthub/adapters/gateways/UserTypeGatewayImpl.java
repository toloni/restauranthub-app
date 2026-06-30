package br.com.postechfiap.toloni.restauranthub.adapters.gateways;

import br.com.postechfiap.toloni.restauranthub.adapters.shared.PageRequestMapper;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.UserTypeJpaEntity;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserTypeJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.shared.JpaSpecificationBuilder;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

/// Adapter that implements [UserTypeGateway] using Spring Data JPA.
///
/// Bridges the domain layer and the persistence layer, converting between
/// [UserType] domain entities and [UserTypeJpaEntity] persistence models.
public class UserTypeGatewayImpl implements UserTypeGateway {

    private final UserTypeJpaRepository jpaRepository;

    /// @param jpaRepository the Spring Data JPA repository for [UserTypeJpaEntity]
    public UserTypeGatewayImpl(UserTypeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /// {@inheritDoc}
    @Override
    public UserType save(UserType userType) {
        var entity = UserTypeJpaEntity.fromDomain(userType);
        var saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    /// {@inheritDoc}
    @Override
    public Optional<UserType> findById(UserTypeId id) {
        return jpaRepository.findById(id.getValue())
                .map(UserTypeJpaEntity::toDomain);
    }

    /// {@inheritDoc}
    @Override
    public Page<UserType> findAll(PageRequest pageRequest) {
        var pageable = PageRequestMapper.toPageable(pageRequest);

        var page = pageRequest.hasFilters()
                ? jpaRepository.findAll(JpaSpecificationBuilder.fromFilters(pageRequest.getFilters()), pageable)
                : jpaRepository.findAll(pageable);

        return Page.of(
                page.getContent().stream()
                        .map(UserTypeJpaEntity::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    /// {@inheritDoc}
    @Override
    public void deleteById(UserTypeId id) {
        try {
            jpaRepository.deleteById(id.getValue());
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException("UserType", id.getValue().toString());
        }
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByRole(UserRole role) {
        return jpaRepository.existsByRole(role);
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByRoleAndIdNot(UserRole role, UserTypeId id) {
        return jpaRepository.existsByRoleAndIdNot(role, id.getValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean existsById(UserTypeId id) {
        return jpaRepository.existsById(id.getValue());
    }

}
