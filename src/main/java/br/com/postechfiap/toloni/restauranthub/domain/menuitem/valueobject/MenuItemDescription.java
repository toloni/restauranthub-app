package br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the description of a [MenuItem].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the description is never null or blank.
///
/// ## Example
/// ```java
/// var description = MenuItemDescription.of("Classic Italian pizza with tomato and mozzarella");
/// ```
public final class MenuItemDescription {

    private final String value;

    private MenuItemDescription(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("MenuItem description is required.");
        this.value = value;
    }

    /// Creates a `MenuItemDescription` from a string.
    ///
    /// @param value the description string
    /// @return a new `MenuItemDescription`
    /// @throws DomainException if `value` is null or blank
    public static MenuItemDescription of(String value) {
        return new MenuItemDescription(value);
    }

    /// @return the underlying [String] value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItemDescription other)) return false;
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
