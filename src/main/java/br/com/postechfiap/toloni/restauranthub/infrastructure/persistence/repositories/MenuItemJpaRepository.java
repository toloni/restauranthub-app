package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories;

import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.MenuItemJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/// Spring Data JPA repository for [MenuItemJpaEntity].
///
/// Extends [JpaRepository] and [JpaSpecificationExecutor] to provide standard CRUD
/// operations, pagination, and dynamic specification-based filtering.
public interface MenuItemJpaRepository extends JpaRepository<MenuItemJpaEntity, UUID>,
        JpaSpecificationExecutor<MenuItemJpaEntity> {

    /// Returns a paginated list of all menu items, eagerly fetching the associated [RestaurantJpaEntity]
    /// to avoid N+1 queries when accessing [MenuItemJpaEntity#getRestaurant()].
    ///
    /// @param pageable the pagination parameters
    /// @return a [Page] of [MenuItemJpaEntity] with restaurant loaded
    @EntityGraph(value = "MenuItem.withRestaurant")
    @Override
    Page<MenuItemJpaEntity> findAll(Pageable pageable);

    /// Returns a filtered and paginated list of menu items matching the given specification,
    /// eagerly fetching the associated [RestaurantJpaEntity].
    ///
    /// @param spec     the [Specification] to filter menu items
    /// @param pageable the pagination parameters
    /// @return a [Page] of [MenuItemJpaEntity] with restaurant loaded
    @EntityGraph(value = "MenuItem.withRestaurant")
    @Override
    Page<MenuItemJpaEntity> findAll(Specification<MenuItemJpaEntity> spec, Pageable pageable);

    /// Returns a menu item by its id, eagerly fetching the associated [RestaurantJpaEntity]
    /// to avoid a lazy-load on [MenuItemJpaEntity#getRestaurant()].
    ///
    /// @param id the UUID of the menu item
    /// @return an [Optional] containing the [MenuItemJpaEntity] with restaurant loaded, or empty if not found
    @EntityGraph(value = "MenuItem.withRestaurant")
    @Override
    Optional<MenuItemJpaEntity> findById(UUID id);

    /// Checks whether a menu item with the given name exists in the given restaurant.
    ///
    /// @param name         the menu item name to check
    /// @param restaurantId the restaurant UUID to scope the check
    /// @return `true` if a menu item with that name exists in the restaurant
    boolean existsByNameAndRestaurantId(String name, UUID restaurantId);

    /// Checks whether a menu item with the given name exists in the given restaurant,
    /// excluding the one with the given id.
    ///
    /// @param name         the menu item name to check
    /// @param restaurantId the restaurant UUID to scope the check
    /// @param id           the menu item id to exclude
    /// @return `true` if another menu item with that name exists in the restaurant
    boolean existsByNameAndRestaurantIdAndIdNot(String name, UUID restaurantId, UUID id);

    /// Checks whether any menu item belongs to the given restaurant.
    ///
    /// @param restaurantId the restaurant UUID to check
    /// @return `true` if the restaurant has at least one menu item
    boolean existsByRestaurantId(UUID restaurantId);

    /// Returns a paginated list of menu items belonging to the given restaurant.
    ///
    /// @param restaurantId the restaurant UUID to filter by
    /// @param pageable     the pagination parameters
    /// @return a [Page] of [MenuItemJpaEntity]
    /// Returns a paginated list of menu items belonging to the given restaurant,
    /// eagerly fetching the associated [RestaurantJpaEntity].
    ///
    /// @param restaurantId the restaurant UUID to filter by
    /// @param pageable     the pagination parameters
    /// @return a [Page] of [MenuItemJpaEntity] with restaurant loaded
    @EntityGraph(value = "MenuItem.withRestaurant")
    Page<MenuItemJpaEntity> findAllByRestaurantId(UUID restaurantId, Pageable pageable);

    /// Deletes all menu items belonging to the given restaurant.
    ///
    /// @param restaurantId the restaurant UUID whose items should be deleted
    @Transactional
    void deleteAllByRestaurantId(UUID restaurantId);
}
