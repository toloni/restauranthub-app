package br.com.postechfiap.toloni.restauranthub.adapters.gateways;

import br.com.postechfiap.toloni.restauranthub.adapters.shared.PageRequestMapper;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItem;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemWithRestaurantName;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.MenuItemId;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.MenuItemName;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.MenuItemJpaEntity;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.MenuItemJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.RestaurantJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.shared.JpaSpecificationBuilder;

import java.util.Optional;

/// Adapter that implements [MenuItemGateway] using Spring Data JPA.
///
/// Bridges the domain layer and the persistence layer, converting between
/// [MenuItem] domain entities and [MenuItemJpaEntity] persistence models.
public class MenuItemGatewayImpl implements MenuItemGateway {

    private final MenuItemJpaRepository jpaRepository;
    private final RestaurantJpaRepository restaurantJpaRepository;

    public MenuItemGatewayImpl(MenuItemJpaRepository jpaRepository,
                               RestaurantJpaRepository restaurantJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.restaurantJpaRepository = restaurantJpaRepository;
    }
    /// {@inheritDoc}
    @Override
    public MenuItem save(MenuItem menuItem) {
        var restaurantRef = restaurantJpaRepository.getReferenceById(menuItem.getRestaurantId().getValue());
        var entity = MenuItemJpaEntity.fromDomain(menuItem, restaurantRef);
        var saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    /// {@inheritDoc}
    @Override
    public Optional<MenuItem> findById(MenuItemId id) {
        return jpaRepository.findById(id.getValue())
                .map(MenuItemJpaEntity::toDomain);
    }

    /// {@inheritDoc}
    @Override
    public Page<MenuItem> findAll(PageRequest pageRequest) {
        var pageable = PageRequestMapper.toPageable(pageRequest);
        var page = jpaRepository.findAll(pageable);
        return Page.of(
                page.getContent().stream()
                        .map(MenuItemJpaEntity::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    /// {@inheritDoc}
    @Override
    public Page<MenuItem> findAllByRestaurantId(RestaurantId restaurantId, PageRequest pageRequest) {
        var pageable = PageRequestMapper.toPageable(pageRequest);
        var page = jpaRepository.findAllByRestaurantId(restaurantId.getValue(), pageable);
        return Page.of(
                page.getContent().stream()
                        .map(MenuItemJpaEntity::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    /// {@inheritDoc}
    @Override
    public void deleteById(MenuItemId id) {
        jpaRepository.deleteById(id.getValue());
    }

    /// {@inheritDoc}
    @Override
    public void deleteAllByRestaurantId(RestaurantId restaurantId) {
        jpaRepository.deleteAllByRestaurantId(restaurantId.getValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByNameAndRestaurantId(MenuItemName name, RestaurantId restaurantId) {
        return jpaRepository.existsByNameAndRestaurantId(name.getValue(), restaurantId.getValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByNameAndRestaurantIdAndIdNot(MenuItemName name, RestaurantId restaurantId, MenuItemId id) {
        return jpaRepository.existsByNameAndRestaurantIdAndIdNot(name.getValue(), restaurantId.getValue(), id.getValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean existsByRestaurantId(RestaurantId restaurantId) {
        return jpaRepository.existsByRestaurantId(restaurantId.getValue());
    }

    /// {@inheritDoc}
    @Override
    public Optional<MenuItemWithRestaurantName> findByIdWithRestaurantName(MenuItemId id) {
        return jpaRepository.findById(id.getValue())
                .map(entity -> new MenuItemWithRestaurantName(
                        entity.toDomain(),
                        entity.getRestaurant() != null ? entity.getRestaurant().getName() : null
                ));
    }

    /// {@inheritDoc}
    @Override
    public Page<MenuItemWithRestaurantName> findAllWithRestaurantName(RestaurantId restaurantId, PageRequest pageRequest) {
        var pageable = PageRequestMapper.toPageable(pageRequest);

        org.springframework.data.domain.Page<MenuItemJpaEntity> page;

        if (restaurantId != null) {
            page = jpaRepository.findAllByRestaurantId(restaurantId.getValue(), pageable);
        } else if (pageRequest.hasFilters()) {
            page = jpaRepository.findAll(JpaSpecificationBuilder.fromFilters(pageRequest.getFilters()), pageable);
        } else {
            page = jpaRepository.findAll(pageable);
        }

        return Page.of(
                page.getContent().stream()
                        .map(entity -> new MenuItemWithRestaurantName(
                                entity.toDomain(),
                                entity.getRestaurant() != null ? entity.getRestaurant().getName() : null
                        ))
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}