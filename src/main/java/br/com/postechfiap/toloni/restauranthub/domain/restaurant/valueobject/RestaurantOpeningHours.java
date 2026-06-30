package br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the opening hours of a [Restaurant].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the opening hours is never null or blank.
///
/// ## Example
/// ```java
/// var openingHours = RestaurantOpeningHours.of("Mon-Fri 9am-10pm");
/// ```
public final class RestaurantOpeningHours {

    private final String value;

    private RestaurantOpeningHours(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("Restaurant opening hours is required.");
        this.value = value;
    }

    /// Creates a `RestaurantOpeningHours` from a string.
    ///
    /// @param value the opening hours string
    /// @return a new `RestaurantOpeningHours`
    /// @throws DomainException if `value` is null or blank
    public static RestaurantOpeningHours of(String value) {
        return new RestaurantOpeningHours(value);
    }

    /// @return the underlying [String] value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantOpeningHours other)) return false;
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
