package br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject;

import java.util.Objects;
import java.util.UUID;

/// Represents the unique identifier of a [MenuItem].
///
/// This is an immutable value object that wraps a [java.util.UUID],
/// ensuring the identity is always valid and never null.
///
/// ## Example
/// ```java
/// var id = MenuItemId.generate();        // new random ID
/// var id = MenuItemId.of("uuid-string"); // from existing value
/// ```
public final class MenuItemId {

    private final UUID value;

    private MenuItemId(UUID value) {
        this.value = Objects.requireNonNull(value, "MenuItemId must not be null");
    }

    /// Creates a `MenuItemId` from an existing [java.util.UUID].
    ///
    /// @param value the UUID value
    /// @return a new `MenuItemId`
    public static MenuItemId of(UUID value) {
        return new MenuItemId(value);
    }

    /// Creates a `MenuItemId` from a UUID string.
    ///
    /// @param value the UUID string representation
    /// @return a new `MenuItemId`
    /// @throws IllegalArgumentException if the string is not a valid UUID
    public static MenuItemId of(String value) {
        try {
            return new MenuItemId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid MenuItemId: " + value, e);
        }
    }

    /// Generates a new random `MenuItemId`.
    ///
    /// @return a new `MenuItemId` with a random [java.util.UUID]
    public static MenuItemId generate() {
        return new MenuItemId(UUID.randomUUID());
    }

    /// @return the underlying [java.util.UUID] value
    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItemId other)) return false;
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
