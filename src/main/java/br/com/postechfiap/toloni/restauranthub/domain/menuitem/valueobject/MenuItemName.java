package br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the name of a [MenuItem].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the name is never null or blank.
///
/// ## Example
/// ```java
/// var name = MenuItemName.of("Margherita Pizza");
/// ```
public final class MenuItemName {

    private final String value;

    private MenuItemName(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("MenuItem name is required.");
        this.value = value;
    }

    /// Creates a `MenuItemName` from a string.
    ///
    /// @param value the name string
    /// @return a new `MenuItemName`
    /// @throws DomainException if `value` is null or blank
    public static MenuItemName of(String value) {
        return new MenuItemName(value);
    }

    /// @return the underlying [String] value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItemName other)) return false;
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
