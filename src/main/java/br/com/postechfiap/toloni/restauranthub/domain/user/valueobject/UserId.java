package br.com.postechfiap.toloni.restauranthub.domain.user.valueobject;

import java.util.Objects;
import java.util.UUID;

/// Represents the unique identifier of a [User].
///
/// This is an immutable value object that wraps a [java.util.UUID],
/// ensuring the identity is always valid and never null.
///
/// ## Example
/// ```java
/// var id = UserId.generate();        // new random ID
/// var id = UserId.of("uuid-string"); // from existing value
/// ```
public final class UserId {

    private final UUID value;

    private UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "UserId must not be null");
    }

    /// Creates a `UserId` from an existing [java.util.UUID].
    ///
    /// @param value the UUID value
    /// @return a new `UserId`
    public static UserId of(UUID value) {
        return new UserId(value);
    }

    /// Creates a `UserId` from a UUID string.
    ///
    /// @param value the UUID string representation
    /// @return a new `UserId`
    /// @throws IllegalArgumentException if the string is not a valid UUID
    public static UserId of(String value) {
        try {
            return new UserId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId: " + value, e);
        }
    }

    /// Generates a new random `UserId`.
    ///
    /// @return a new `UserId` with a random [java.util.UUID]
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    /// @return the underlying [java.util.UUID] value
    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId other)) return false;
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
