package br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject;

import java.util.Objects;
import java.util.UUID;

/// Represents the unique identifier of a [UserType].
///
/// This is an immutable value object that wraps a [java.util.UUID],
/// ensuring the identity is always valid and never null.
///
/// ## Example
/// ```java
/// var id = UserTypeId.generate();         // new random ID
/// var id = UserTypeId.of("uuid-string");  // from existing value
/// ```
public final class UserTypeId {

    private final UUID value;

    private UserTypeId(UUID value) {
        this.value = Objects.requireNonNull(value, "UserTypeId must not be null");
    }

    /// Creates a `UserTypeId` from an existing [java.util.UUID].
    ///
    /// @param value the UUID value
    /// @return a new `UserTypeId`
    public static UserTypeId of(UUID value) {
        return new UserTypeId(value);
    }

    /// Creates a `UserTypeId` from a UUID string.
    ///
    /// @param value the UUID string representation
    /// @return a new `UserTypeId`
    /// @throws IllegalArgumentException if the string is not a valid UUID
    public static UserTypeId of(String value) {
        try {
            return new UserTypeId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserTypeId: " + value, e);
        }
    }

    /// Generates a new random `UserTypeId`.
    ///
    /// @return a new `UserTypeId` with a random [java.util.UUID]
    public static UserTypeId generate() {
        return new UserTypeId(UUID.randomUUID());
    }

    /// @return the underlying [java.util.UUID] value
    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTypeId other)) return false;
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