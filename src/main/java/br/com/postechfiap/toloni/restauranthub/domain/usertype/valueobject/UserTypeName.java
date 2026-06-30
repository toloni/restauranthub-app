package br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the name of a [UserType].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the name is never null or blank.
///
/// ## Example
/// ```java
/// var name = UserTypeName.of("Restaurant Owner");
/// ```
public final class UserTypeName {

    private final String value;

    private UserTypeName(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("UserType name is required.");
        this.value = value;
    }

    /// Creates a `UserTypeName` from a string.
    ///
    /// @param value the name string
    /// @return a new `UserTypeName`
    /// @throws DomainException if `value` is null or blank
    public static UserTypeName of(String value) {
        return new UserTypeName(value);
    }

    /// @return the underlying [String] value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTypeName other)) return false;
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