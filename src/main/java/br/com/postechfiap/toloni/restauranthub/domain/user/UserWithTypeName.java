package br.com.postechfiap.toloni.restauranthub.domain.user;

/// Represents a [User] enriched with its [UserType] name.
///
/// Used as a read model for queries that need to display the user type name
/// without coupling the [User] entity to the [UserType] entity.
///
/// @param user         the [User] entity
/// @param userTypeName the name of the [UserType]
public record UserWithTypeName(User user, String userTypeName) {}
