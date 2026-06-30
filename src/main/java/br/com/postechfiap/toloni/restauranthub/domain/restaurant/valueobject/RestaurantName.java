package br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the name of a [Restaurant].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the name is never null or blank.
///
/// ## Example
/// ```java
/// var name = RestaurantName.of("The Great Burger");
/// ```
public final class RestaurantName {

    private final String value;

    private RestaurantName(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("Restaurant name is required.");
        this.value = value;
    }

    /// Creates a `RestaurantName` from a string.
    ///
    /// @param value the name string
    /// @return a new `RestaurantName`
    /// @throws DomainException if `value` is null or blank
    public static RestaurantName of(String value) {
        return new RestaurantName(value);
    }

    /// @return the underlying [String] value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantName other)) return false;
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
