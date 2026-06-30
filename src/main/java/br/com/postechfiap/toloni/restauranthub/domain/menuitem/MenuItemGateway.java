package br.com.postechfiap.toloni.restauranthub.domain.menuitem;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.MenuItemId;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.MenuItemName;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;

import java.util.Optional;

/// Gateway interface for [MenuItem] persistence operations.
///
/// Defines the contract for data access in the domain layer,
/// following the Dependency Inversion Principle — the domain
/// depends on this abstraction, not on any infrastructure implementation.
public interface MenuItemGateway {

    /// Persists a [MenuItem], either creating or updating it.
    ///
    /// @param menuItem the menu item to save
    /// @return the saved [MenuItem]
    MenuItem save(MenuItem menuItem);

    /// Finds a [MenuItem] by its identifier.
    ///
    /// @param id the [MenuItemId] to search for
    /// @return an [java.util.Optional] containing the [MenuItem], or empty if not found
    Optional<MenuItem> findById(MenuItemId id);

    /// Returns a paginated list of [Restaurant] instances.
    ///
    /// @param pageRequest the [PageRequest] carrying page number, size, filters, and sorting
    /// @return a [Page] of [MenuItem] instances
    Page<MenuItem> findAll(PageRequest pageRequest);

    /// Returns all [MenuItem] instances belonging to a specific [Restaurant].
    ///
    /// @param restaurantId the [RestaurantId] to filter by
    /// @return a [java.util.List] of menu items for the given restaurant, or an empty list if none exist
    Page<MenuItem> findAllByRestaurantId(RestaurantId restaurantId, PageRequest pageRequest);

    /// Deletes a [MenuItem] by its identifier.
    ///
    /// @param id the [MenuItemId] of the menu item to delete
    void deleteById(MenuItemId id);

    /// Checks whether a [MenuItem] with the given name already exists in a specific [Restaurant].
    ///
    /// @param name         the [MenuItemName] to check
    /// @param restaurantId the [RestaurantId] to scope the search
    /// @return `true` if a menu item with the given name exists in the restaurant, `false` otherwise
    boolean existsByNameAndRestaurantId(MenuItemName name, RestaurantId restaurantId);

    /// Checks whether a [MenuItem] with the given name exists in a specific [Restaurant],
    /// excluding the one with the specified [MenuItemId].
    ///
    /// Useful for validation during update operations.
    ///
    /// @param name         the [MenuItemName] to check
    /// @param restaurantId the [RestaurantId] to scope the search
    /// @param id           the [MenuItemId] to exclude from the search
    /// @return `true` if another menu item with the given name exists in the restaurant, `false` otherwise
    boolean existsByNameAndRestaurantIdAndIdNot(MenuItemName name, RestaurantId restaurantId, MenuItemId id);

    /// Checks whether any [MenuItem] exists for the given [RestaurantId].
    ///
    /// @param restaurantId the [RestaurantId] to check
    /// @return `true` if any menu item exists for the given restaurant, `false` otherwise
    boolean existsByRestaurantId(RestaurantId restaurantId);

    /// Finds a [MenuItem] by its identifier, enriched with the [Restaurant] name.
    ///
    /// @param id the [MenuItemId] to search for
    /// @return an [java.util.Optional] containing the [MenuItemWithRestaurantName], or empty if not found
    Optional<MenuItemWithRestaurantName> findByIdWithRestaurantName(MenuItemId id);

    /// Returns a paginated list of [MenuItem] instances, enriched with the [Restaurant] name.
    ///
    /// @param restaurantId the [RestaurantId] to filter by, or `null` for all items
    /// @param pageRequest  the [PageRequest] carrying page number, size, filters, and sorting
    /// @return a [Page] of [MenuItemWithRestaurantName]
    Page<MenuItemWithRestaurantName> findAllWithRestaurantName(RestaurantId restaurantId, PageRequest pageRequest);

    /// Deletes all [MenuItem] instances belonging to the given [RestaurantId].
    ///
    /// @param restaurantId the [RestaurantId] of the restaurant whose items will be deleted
    void deleteAllByRestaurantId(RestaurantId restaurantId);
}