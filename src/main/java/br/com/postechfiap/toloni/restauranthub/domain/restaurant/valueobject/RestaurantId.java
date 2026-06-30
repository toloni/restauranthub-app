package br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject;

import java.util.Objects;
import java.util.UUID;

/// Represents the unique identifier of a [Restaurant].
///
/// This is an immutable value object that wraps a [java.util.UUID],
/// ensuring the identity is always valid and never null.
///
/// ## Example
/// ```java
/// var id = RestaurantId.generate();        // new random ID
/// var id = RestaurantId.of("uuid-string"); // from existing value
/// ```
public final class RestaurantId {

    private final UUID value;

    private RestaurantId(UUID value) {
        this.value = Objects.requireNonNull(value, "RestaurantId must not be null");
    }

    /// Creates a `RestaurantId` from an existing [java.util.UUID].
    ///
    /// @param value the UUID value
    /// @return a new `RestaurantId`
    public static RestaurantId of(UUID value) {
        return new RestaurantId(value);
    }

    /// Creates a `RestaurantId` from a UUID string.
    ///
    /// @param value the UUID string representation
    /// @return a new `RestaurantId`
    /// @throws IllegalArgumentException if the string is not a valid UUID
    public static RestaurantId of(String value) {
        try {
            return new RestaurantId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid RestaurantId: " + value, e);
        }
    }

    /// Generates a new random `RestaurantId`.
    ///
    /// @return a new `RestaurantId` with a random [java.util.UUID]
    public static RestaurantId generate() {
        return new RestaurantId(UUID.randomUUID());
    }

    /// @return the underlying [java.util.UUID] value
    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantId other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
