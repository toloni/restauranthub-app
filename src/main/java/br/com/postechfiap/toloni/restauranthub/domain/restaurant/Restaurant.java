package br.com.postechfiap.toloni.restauranthub.domain.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Represents a restaurant in the domain layer.
///
/// A `Restaurant` is an entity that holds identity, location, culinary profile,
/// and a reference to its owner through a [UserId]. It enforces domain invariants
/// on construction and allows controlled mutation via [#update].
///
/// ## Example
/// ```java
/// var restaurant = new Restaurant(
///     RestaurantId.generate(),
///     RestaurantName.of("The Great Burger"),
///     RestaurantAddress.of("123 Main St, Springfield"),
///     RestaurantCuisineType.of("American"),
///     RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
///     ownerId
/// );
/// ```
public class Restaurant {

    private final RestaurantId id;
    private RestaurantName name;
    private RestaurantAddress address;
    private RestaurantCuisineType cuisineType;
    private RestaurantOpeningHours openingHours;
    private UserId ownerId;

    /// Creates a new `Restaurant` with the given attributes.
    ///
    /// @param id           the unique identifier of this restaurant
    /// @param name         the name of this restaurant
    /// @param address      the address of this restaurant
    /// @param cuisineType  the cuisine type of this restaurant
    /// @param openingHours the opening hours of this restaurant
    /// @param ownerId      the [UserId] referencing the owner of this restaurant
    /// @throws DomainException if `ownerId` is `null`
    public Restaurant(RestaurantId id, RestaurantName name, RestaurantAddress address,
                      RestaurantCuisineType cuisineType, RestaurantOpeningHours openingHours, UserId ownerId) {
        if (ownerId == null)
            throw new DomainException("Restaurant must have an owner.");
        this.id = id;
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.openingHours = openingHours;
        this.ownerId = ownerId;
    }

    /// Updates the mutable attributes of this `Restaurant`.
    ///
    /// Only non-null values are applied. If a parameter is `null`,
    /// the corresponding field remains unchanged.
    ///
    /// @param name         the new name, or `null` to keep the current value
    /// @param address      the new address, or `null` to keep the current value
    /// @param cuisineType  the new cuisine type, or `null` to keep the current value
    /// @param openingHours the new opening hours, or `null` to keep the current value
    /// @param ownerId      the new [UserId], or `null` to keep the current value
    public void update(RestaurantName name, RestaurantAddress address,
                       RestaurantCuisineType cuisineType, RestaurantOpeningHours openingHours, UserId ownerId) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (cuisineType != null) this.cuisineType = cuisineType;
        if (openingHours != null) this.openingHours = openingHours;
        if (ownerId != null) this.ownerId = ownerId;
    }

    /// @return the unique identifier of this restaurant
    public RestaurantId getId() {
        return id;
    }

    /// @return the name of this restaurant
    public RestaurantName getName() {
        return name;
    }

    /// @return the address of this restaurant
    public RestaurantAddress getAddress() {
        return address;
    }

    /// @return the cuisine type of this restaurant
    public RestaurantCuisineType getCuisineType() {
        return cuisineType;
    }

    /// @return the opening hours of this restaurant
    public RestaurantOpeningHours getOpeningHours() {
        return openingHours;
    }

    /// @return the [UserId] referencing the owner of this restaurant
    public UserId getOwnerId() {
        return ownerId;
    }
}
