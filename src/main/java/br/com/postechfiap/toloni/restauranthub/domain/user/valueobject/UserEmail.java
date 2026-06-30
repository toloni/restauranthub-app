package br.com.postechfiap.toloni.restauranthub.domain.user.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;
import java.util.regex.Pattern;

/// Represents the email address of a [User].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the email is never null, blank, or malformed.
/// The value is automatically normalized to lowercase on construction.
///
/// ## Example
/// ```java
/// var email = UserEmail.of("john@example.com");
/// ```
public final class UserEmail {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    private UserEmail(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("User email is required.");
        if (!EMAIL_PATTERN.matcher(value).matches())
            throw new DomainException("User email is invalid: " + value);
        this.value = value.toLowerCase();
    }

    /// Creates a `UserEmail` from a string.
    ///
    /// The value is validated against a basic email format and
    /// normalized to lowercase before being stored.
    ///
    /// @param value the email string
    /// @return a new `UserEmail`
    /// @throws DomainException if `value` is null, blank, or not a valid email format
    public static UserEmail of(String value) {
        return new UserEmail(value);
    }

    /// @return the underlying [String] value, always in lowercase
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEmail other)) return false;
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
