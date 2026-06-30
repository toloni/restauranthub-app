package br.com.postechfiap.toloni.restauranthub.domain.user;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

import java.util.Optional;

/// Gateway interface for [User] persistence operations.
///
/// Defines the contract for data access in the domain layer,
/// following the Dependency Inversion Principle — the domain
/// depends on this abstraction, not on any infrastructure implementation.
public interface UserGateway {

    /// Persists a [User], either creating or updating it.
    ///
    /// @param user the user to save
    /// @return the saved [User]
    User save(User user);

    /// Finds a [User] by its identifier.
    ///
    /// @param id the [UserId] to search for
    /// @return an [java.util.Optional] containing the [User], or empty if not found
    Optional<User> findById(UserId id);

    /// Returns a paginated list of [User] instances.
    ///
    /// @param pageRequest the [PageRequest] carrying page number, size, filters, and sorting
    /// @return a [Page] of [User] instances
    Page<User> findAll(PageRequest pageRequest);

    /// Deletes a [User] by its identifier.
    ///
    /// @param id the [UserId] of the user to delete
    void deleteById(UserId id);

    /// Checks whether a [User] with the given email already exists.
    ///
    /// @param email the [UserEmail] to check
    /// @return `true` if a user with the given email exists, `false` otherwise
    boolean existsByEmail(UserEmail email);

    /// Checks whether a [User] with the given email exists,
    /// excluding the one with the specified [UserId].
    ///
    /// Useful for validation during update operations.
    ///
    /// @param email the [UserEmail] to check
    /// @param id    the [UserId] to exclude from the search
    /// @return `true` if another user with the given email exists, `false` otherwise
    boolean existsByEmailAndIdNot(UserEmail email, UserId id);

    /// Checks whether a [User] with the given [UserId] exists.
    ///
    /// @param id the [UserId] to check
    /// @return `true` if a user with the given identifier exists, `false` otherwise
    boolean existsById(UserId id);

    /// Checks whether a [User] with the given [UserTypeId] exists.
    ///
    /// @param userTypeId the [UserTypeId] to check
    /// @return `true` if a user with the given user type exists, `false` otherwise
    boolean existsByUserTypeId(UserTypeId userTypeId);

    /// Finds a [User] by its identifier, enriched with the [UserType] name.
    ///
    /// @param id the [UserId] to search for
    /// @return an [java.util.Optional] containing the [UserWithTypeName], or empty if not found
    Optional<UserWithTypeName> findByIdWithUserTypeName(UserId id);

    /// Returns a paginated list of [User] instances, enriched with the [UserType] name.
    ///
    /// @param pageRequest the [PageRequest] carrying page number, size, filters, and sorting
    /// @return a [Page] of [UserWithTypeName]
    Page<UserWithTypeName> findAllWithUserTypeName(PageRequest pageRequest);
}
