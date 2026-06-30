package br.com.postechfiap.toloni.restauranthub.domain.usertype;

/// Defines system user roles.
public enum UserRole {

    /// System administrator responsible for managing the platform and its resources.
    ADMIN,

    /// Restaurant owner responsible for managing a restaurant.
    RESTAURANT_OWNER,

    /// Customer using the platform to browse and order food.
    CUSTOMER
}