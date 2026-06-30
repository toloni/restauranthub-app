package br.com.postechfiap.toloni.restauranthub.domain.user.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the password of a [User].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the password is never null, blank, or shorter than 8 characters.
///
/// The [#toString] method intentionally returns `***` to prevent
/// accidental exposure of the password in logs or stack traces.
///
/// ## Example
/// ```java
/// var password = UserPassword.of("secret123");
/// ```
public final class UserPassword {

    private final String value;

    private UserPassword(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("User password is required.");
        if (value.length() < 8)
            throw new DomainException("User password must be at least 8 characters.");
        this.value = value;
    }

    /// Creates a `UserPassword` from a string.
    ///
    /// @param value the password string
    /// @return a new `UserPassword`
    /// @throws DomainException if `value` is null, blank, or shorter than 8 characters
    public static UserPassword of(String value) {
        return new UserPassword(value);
    }

    /// @return the underlying [String] value
    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPassword other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    /// @return `***` to prevent accidental exposure of the password
    @Override
    public String toString() { return "***"; }
}
