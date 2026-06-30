package br.com.postechfiap.toloni.restauranthub.domain.restaurant;


import br.com.postechfiap.toloni.restauranthub.domain.user.User;

/// Represents a [Restaurant] enriched with its owner name.
///
/// Used as a read model for queries that need to display the owner name
/// without coupling the [Restaurant] entity to the [User] entity.
///
/// @param restaurant the [Restaurant] entity
/// @param ownerName  the name of the owner [User]
public record RestaurantWithOwnerName(Restaurant restaurant, String ownerName) {
}
