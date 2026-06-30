package br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents the description of a [UserType].
///
/// This is an immutable value object that wraps a [String],
/// ensuring the description is never null or blank.
///
/// ## Example
/// ```java
/// var description = UserTypeDescription.of("Owns and manages a restaurant");
/// ```
public final class UserTypeDescription {

    private final String value;

    private UserTypeDescription(String value) {
        if (value == null || value.isBlank())
            throw new DomainException("UserType description is required.");
        this.value = value;
    }

    /// Creates a `UserTypeDescription` from a string.
    ///
    /// @param value the description string
    /// @return a new `UserTypeDescription`
    /// @throws DomainException if `value` is null or blank
    public static UserTypeDescription of(String value) {
        return new UserTypeDescription(value);
    }

    /// @return the underlying [String] value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTypeDescription other)) return false;
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
