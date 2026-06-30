package br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the address of a [Restaurant].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the address is never null or blank.
///
/// ## Example
/// ```java
/// var address = RestaurantAddress.of("123 Main St, Springfield");
/// ```
public final class RestaurantAddress {

    private final String value;

    private RestaurantAddress(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("Restaurant address is required.");
        this.value = value;
    }

    /// Creates a `RestaurantAddress` from a string.
    ///
    /// @param value the address string
    /// @return a new `RestaurantAddress`
    /// @throws DomainException if `value` is null or blank
    public static RestaurantAddress of(String value) {
        return new RestaurantAddress(value);
    }

    /// @return the underlying [String] value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantAddress other)) return false;
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