package br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the cuisine type of [Restaurant].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the cuisine type is never null or blank.
///
/// ## Example
/// ```java
/// var cuisineType = RestaurantCuisineType.of("Italian");
/// ```
public final class RestaurantCuisineType {

    private final String value;

    private RestaurantCuisineType(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("Restaurant cuisine type is required.");
        this.value = value;
    }

    /// Creates a `RestaurantCuisineType` from a string.
    ///
    /// @param value the cuisine type string
    /// @return a new `RestaurantCuisineType`
    /// @throws DomainException if `value` is null or blank
    public static RestaurantCuisineType of(String value) {
        return new RestaurantCuisineType(value);
    }

    /// @return the underlying [String] value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantCuisineType other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
