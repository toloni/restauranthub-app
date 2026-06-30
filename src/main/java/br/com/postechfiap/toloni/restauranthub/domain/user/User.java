package br.com.postechfiap.toloni.restauranthub.domain.user;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Represents a user in the domain layer.
///
/// A `User` is an entity that holds identity, credentials, and a reference
/// to a [UserType] through its [UserTypeId]. It enforces domain invariants
/// on construction and allows controlled mutation via [#update].
///
/// ## Example
/// ```java
/// var user = new User(
///     UserId.generate(),
///     UserName.of("John Doe"),
///     UserEmail.of("john@example.com"),
///     UserPassword.of("secret123"),
///     UserTypeId.of("uuid-string")
/// );
/// ```
public class User {

    private final UserId id;
    private UserName name;
    private UserEmail email;
    private UserPassword password;
    private UserTypeId userTypeId;

    /// Creates a new `User` with the given attributes.
    ///
    /// @param id         the unique identifier of this user
    /// @param name       the name of this user
    /// @param email      the email of this user
    /// @param password   the password of this user
    /// @param userTypeId the [UserTypeId] referencing the type of this user
    /// @throws DomainException if `userTypeId` is `null`
    public User(UserId id, UserName name, UserEmail email, UserPassword password, UserTypeId userTypeId) {
        if (userTypeId == null)
            throw new DomainException("User must have a type.");
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userTypeId = userTypeId;
    }

    /// Updates the mutable attributes of this `User`.
    ///
    /// Only non-null values are applied. If a parameter is `null`,
    /// the corresponding field remains unchanged.
    ///
    /// @param name       the new name, or `null` to keep the current value
    /// @param email      the new email, or `null` to keep the current value
    /// @param password   the new password, or `null` to keep the current value
    /// @param userTypeId the new [UserTypeId], or `null` to keep the current value
    public void update(UserName name, UserEmail email, UserPassword password, UserTypeId userTypeId) {
        if (name != null) this.name = name;
        if (email != null) this.email = email;
        if (password != null) this.password = password;
        if (userTypeId != null) this.userTypeId = userTypeId;
    }

    /// @return the unique identifier of this user
    public UserId getId() {
        return id;
    }

    /// @return the name of this user
    public UserName getName() {
        return name;
    }

    /// @return the email of this user
    public UserEmail getEmail() {
        return email;
    }

    /// @return the password of this user
    public UserPassword getPassword() {
        return password;
    }

    /// @return the [UserTypeId] referencing the type of this user
    public UserTypeId getUserTypeId() {
        return userTypeId;
    }
}
