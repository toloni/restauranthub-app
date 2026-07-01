package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories;

import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.RestaurantJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/// Spring Data JPA repository for [RestaurantJpaEntity].
///
/// Extends [JpaRepository] and [JpaSpecificationExecutor] to provide standard CRUD
/// operations, pagination, and dynamic specification-based filtering.
public interface RestaurantJpaRepository extends JpaRepository<RestaurantJpaEntity, UUID>,
        JpaSpecificationExecutor<RestaurantJpaEntity> {

    /// Returns a paginated list of all restaurants, eagerly fetching the associated [UserJpaEntity] owner
    /// to avoid N+1 queries when accessing [RestaurantJpaEntity#getOwner()].
    ///
    /// @param pageable the pagination parameters
    /// @return a [Page] of [RestaurantJpaEntity] with owner loaded
    @EntityGraph(value = "Restaurant.withOwner")
    @Override
    Page<RestaurantJpaEntity> findAll(Pageable pageable);

    /// Returns a filtered and paginated list of restaurants matching the given specification,
    /// eagerly fetching the associated [UserJpaEntity] owner.
    ///
    /// @param spec     the [Specification] to filter restaurants
    /// @param pageable the pagination parameters
    /// @return a [Page] of [RestaurantJpaEntity] with owner loaded
    @EntityGraph(value = "Restaurant.withOwner")
    @Override
    Page<RestaurantJpaEntity> findAll(Specification<RestaurantJpaEntity> spec, Pageable pageable);

    /// Returns a restaurant by its id, eagerly fetching the associated [UserJpaEntity] owner
    /// to avoid a lazy-load on [RestaurantJpaEntity#getOwner()].
    ///
    /// @param id the UUID of the restaurant
    /// @return an [Optional] containing the [RestaurantJpaEntity] with owner loaded, or empty if not found
    @EntityGraph(value = "Restaurant.withOwner")
    @Override
    Optional<RestaurantJpaEntity> findById(UUID id);

    /// Checks whether a restaurant with the given name exists.
    ///
    /// @param name the restaurant name to check
    /// @return `true` if a restaurant with that name exists
    boolean existsByName(String name);

    /// Checks whether a restaurant with the given name exists, excluding the one with the given id.
    ///
    /// @param name the restaurant name to check
    /// @param id   the id to exclude from the search
    /// @return `true` if another restaurant with that name exists
    boolean existsByNameAndIdNot(String name, UUID id);

    /// Checks whether a restaurant owned by the given owner id exists.
    ///
    /// @param ownerId the owner's UUID to check
    /// @return `true` if a restaurant with that owner exists
    boolean existsByOwnerId(UUID ownerId);
}
