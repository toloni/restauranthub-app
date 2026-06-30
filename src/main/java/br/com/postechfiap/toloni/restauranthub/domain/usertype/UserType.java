package br.com.postechfiap.toloni.restauranthub.domain.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;

import java.util.Arrays;

///  Represents a user type in the domain layer.
///
///  A `UserType` defines the classification of a user within the system,
///  combining an identity, a human-readable name, a description, and a [UserRole].
///
///  ## Responsibilities
///  - Encapsulates the identity and behavioral role of a user category
///  - Enforces domain invariants on construction
///  - Allows controlled mutation via [#update(UserTypeName, UserTypeDescription, UserRole)]
///
///  ## Example
///  ```java
///  var userType = new UserType(
///      UserTypeId.generate(),
///      UserTypeName.of("Restaurant Owner"),
///      UserTypeDescription.of("Owns and manages a restaurant"),
///      UserRole.RESTAURANT_OWNER
///  );
///  ```
public class UserType {

    private final UserTypeId id;
    private UserTypeName name;
    private UserTypeDescription description;
    private UserRole role;

    ///  Creates a new `UserType` with the given attributes.
    ///
    /// @param id          the unique identifier of this user type
    /// @param name        the name of this user type
    /// @param description a brief description of this user type
    /// @param role        the [UserRole] associated with this user type
    /// @throws DomainException if `role` is `null`
    public UserType(UserTypeId id, UserTypeName name, UserTypeDescription description, UserRole role) {
        if (role == null)
            throw new DomainException("UserType role is required. Valid values: "
                    + Arrays.toString(UserRole.values()));
        this.id = id;
        this.name = name;
        this.description = description;
        this.role = role;
    }

    ///  Updates the mutable attributes of this `UserType`.
    ///
    ///  Only non-null values are applied. If a parameter is `null`, the corresponding
    ///  field remains unchanged. The `role` is only updated if the new value differs
    ///  from the current one.
    ///
    /// @param name        the new name, or `null` to keep the current value
    /// @param description the new description, or `null` to keep the current value
    /// @param role        the new role, or `null` to keep the current value
    public void update(UserTypeName name, UserTypeDescription description, UserRole role) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (role != null && !role.equals(this.role)) this.role = role;
    }

    /// @return the unique identifier of this user type
    public UserTypeId getId() {
        return id;
    }

    /// @return the name of this user type
    public UserTypeName getName() {
        return name;
    }

    /// @return the description of this user type
    public UserTypeDescription getDescription() {
        return description;
    }

    /// @return the [UserRole] associated with this user type
    public UserRole getRole() {
        return role;
    }
}
