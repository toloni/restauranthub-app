package br.com.postechfiap.toloni.restauranthub.domain.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantName;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

import java.util.Optional;

/// Gateway interface for [Restaurant] persistence operations.
///
/// Defines the contract for data access in the domain layer,
/// following the Dependency Inversion Principle — the domain
/// depends on this abstraction, not on any infrastructure implementation.
public interface RestaurantGateway {

    /// Persists a [Restaurant], either creating or updating it.
    ///
    /// @param restaurant the restaurant to save
    /// @return the saved [Restaurant]
    Restaurant save(Restaurant restaurant);

    /// Finds a [Restaurant] by its identifier.
    ///
    /// @param id the [RestaurantId] to search for
    /// @return an [java.util.Optional] containing the [Restaurant], or empty if not found
    Optional<Restaurant> findById(RestaurantId id);

    /// Returns a paginated list of [Restaurant] instances.
    ///
    /// @param pageRequest the [PageRequest] carrying page number, size, filters, and sorting
    /// @return a [Page] of [Restaurant] instances
    Page<Restaurant> findAll(PageRequest pageRequest);

    /// Deletes a [Restaurant] by its identifier.
    ///
    /// @param id the [RestaurantId] of the restaurant to delete
    void deleteById(RestaurantId id);

    /// Checks whether a [Restaurant] with the given name already exists.
    ///
    /// @param name the [RestaurantName] to check
    /// @return `true` if a restaurant with the given name exists, `false` otherwise
    boolean existsByName(RestaurantName name);

    /// Checks whether a [Restaurant] with the given name exists,
    /// excluding the one with the specified [RestaurantId].
    ///
    /// Useful for validation during update operations.
    ///
    /// @param name the [RestaurantName] to check
    /// @param id   the [RestaurantId] to exclude from the search
    /// @return `true` if another restaurant with the given name exists, `false` otherwise
    boolean existsByNameAndIdNot(RestaurantName name, RestaurantId id);

    /// Checks whether a [Restaurant] with the given owner [UserId] exists.
    ///
    /// @param ownerId the [UserId] to check
    /// @return `true` if a restaurant with the given owner exists, `false` otherwise
    boolean existsByOwnerId(UserId ownerId);

    /// Finds a [Restaurant] by its identifier, enriched with the owner name.
    ///
    /// @param id the [RestaurantId] to search for
    /// @return an [java.util.Optional] containing the [RestaurantWithOwnerName], or empty if not found
    Optional<RestaurantWithOwnerName> findByIdWithOwnerName(RestaurantId id);

    /// Returns a paginated list of [Restaurant] instances, enriched with the owner name.
    ///
    /// @param pageRequest the [PageRequest] carrying page number, size, filters, and sorting
    /// @return a [Page] of [RestaurantWithOwnerName]
    Page<RestaurantWithOwnerName> findAllWithOwnerName(PageRequest pageRequest);
}