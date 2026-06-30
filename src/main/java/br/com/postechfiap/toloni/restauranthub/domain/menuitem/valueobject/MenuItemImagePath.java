package br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject;

import java.util.Objects;

/// Represents the image path of a [MenuItem].
///
/// This is an immutable value object that wraps a [String].
/// Unlike other value objects, the image path is optional —
/// a [MenuItem] may exist without an associated image.
///
/// ## Example
/// ```java
/// var imagePath = MenuItemImagePath.of("/images/pizza.jpg");
/// ```
public final class MenuItemImagePath {

    private final String value;

    private MenuItemImagePath(String value) {
        this.value = value;
    }

    /// Creates a `MenuItemImagePath` from a string.
    ///
    /// @param value the image path string, or `null` if no image is available
    /// @return a new `MenuItemImagePath`
    public static MenuItemImagePath of(String value) {
        return new MenuItemImagePath(value);
    }

    /// @return the underlying [String] value, or `null` if no image is available
    public String getValue() {
        return value;
    }

    /// @return `true` if an image path is present, `false` otherwise
    public boolean isPresent() {
        return value != null && !value.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItemImagePath other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}
