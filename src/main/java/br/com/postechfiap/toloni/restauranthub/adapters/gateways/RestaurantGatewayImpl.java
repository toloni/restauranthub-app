package br.com.postechfiap.toloni.restauranthub.adapters.gateways;

import br.com.postechfiap.toloni.restauranthub.adapters.shared.PageRequestMapper;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantWithOwnerName;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantName;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.RestaurantJpaEntity;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.RestaurantJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.shared.JpaSpecificationBuilder;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

/// Adapter that implements [RestaurantGateway] using Spring Data JPA.
///
/// Bridges the domain layer and the persistence layer, converting between
/// [Restaurant] domain entities and [RestaurantJpaEntity] persistence models.
public class RestaurantGatewayImpl implements RestaurantGateway {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final UserJpaRepository userJpaRepository;

    /// @param restaurantJpaRepository the Spring Data JPA repository for [RestaurantJpaEntity]
    public RestaurantGatewayImpl(RestaurantJpaRepository restaurantJpaRepository,
                                 UserJpaRepository userJpaRepository) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    /// {@inheritDoc}
    @Override
    public Restaurant save(Restaurant restaurant) {
        var ownerRef = userJpaRepository.getReferenceById(restaurant.getOwnerId().getValue());
        var entity = RestaurantJpaEntity.fromDomain(restaurant, ownerRef);
        var saved = restaurantJpaRepository.save(entity);
        return saved.toDomain();
    }

    /// {@inheritDoc}
    @Override
    public Optional<Restaurant> findById(RestaurantId id) {
        return restaurantJpaRepository.findById(id.getValue())
                .map(RestaurantJpaEntity::toDomain);
    }

    /// {@inheritDoc}
    @Override
    public Page<Restaurant> findAll(PageRequest pageRequest) {
        var pageable = PageRequestMapper.toPageable(pageRequest);
        var page = restaurantJpaRepository.findAll(pageable);
        return Page.of(
                page.getContent().stream()
                        .map(RestaurantJpaEntity::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    /// {@inheritDoc}
    @Override
    public void deleteById(RestaurantId id) {
        try {
            restaurantJpaRepository.deleteById(id.getValue());
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException("Restaurant", id.getValue().toString());
        }
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByName(RestaurantName name) {
        return restaurantJpaRepository.existsByName(name.getValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByNameAndIdNot(RestaurantName name, RestaurantId id) {
        return restaurantJpaRepository.existsByNameAndIdNot(name.getValue(), id.getValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByOwnerId(UserId ownerId) {
        return restaurantJpaRepository.existsByOwnerId(ownerId.getValue());
    }

    /// {@inheritDoc}
    @Override
    public Optional<RestaurantWithOwnerName> findByIdWithOwnerName(RestaurantId id) {
        return restaurantJpaRepository.findById(id.getValue())
                .map(entity -> new RestaurantWithOwnerName(
                        entity.toDomain(),
                        entity.getOwner() != null ? entity.getOwner().getName() : null
                ));
    }

    /// {@inheritDoc}
    @Override
    public Page<RestaurantWithOwnerName> findAllWithOwnerName(PageRequest pageRequest) {
        var pageable = PageRequestMapper.toPageable(pageRequest);

        var page = pageRequest.hasFilters()
                ? restaurantJpaRepository.findAll(JpaSpecificationBuilder.fromFilters(pageRequest.getFilters()), pageable)
                : restaurantJpaRepository.findAll(pageable);

        return Page.of(
                page.getContent().stream()
                        .map(entity -> new RestaurantWithOwnerName(
                                entity.toDomain(),
                                entity.getOwner() != null ? entity.getOwner().getName() : null
                        ))
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}
