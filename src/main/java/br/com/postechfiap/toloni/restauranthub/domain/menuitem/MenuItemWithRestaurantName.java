package br.com.postechfiap.toloni.restauranthub.domain.menuitem;

/// Represents a [MenuItem] enriched with its [Restaurant] name.
///
/// Used as a read model for queries that need to display the restaurant name
/// without coupling the [MenuItem] entity to the [Restaurant] entity.
///
/// @param menuItem      the [MenuItem] entity
/// @param restaurantName the name of the [Restaurant]
public record MenuItemWithRestaurantName(MenuItem menuItem, String restaurantName) {}
