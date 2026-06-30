package br.com.postechfiap.toloni.restauranthub.domain.user.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the name of a [User].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the name is never null or blank.
///
/// ## Example
/// ```java
/// var name = UserName.of("John Doe");
/// ```
public final class UserName {

    private final String value;

    private UserName(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("User name is required.");
        this.value = value;
    }

    /// Creates a `UserName` from a string.
    ///
    /// @param value the name string
    /// @return a new `UserName`
    /// @throws DomainException if `value` is null or blank
    public static UserName of(String value) {
        return new UserName(value);
    }

    /// @return the underlying [String] value
    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserName other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }
}