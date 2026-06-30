package br.com.postechfiap.toloni.restauranthub.domain.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

import java.util.Optional;

/// Gateway interface for [UserType] persistence operations.
///
/// Defines the contract for data access in the domain layer,
/// following the Dependency Inversion Principle — the domain
/// depends on this abstraction, not on any infrastructure implementation.
public interface UserTypeGateway {

    /// Persists a [UserType], either creating or updating it.
    ///
    /// @param userType the user type to save
    /// @return the saved [UserType]
    UserType save(UserType userType);

    /// Finds a [UserType] by its identifier.
    ///
    /// @param id the [UserTypeId] to search for
    /// @return an [java.util.Optional] containing the [UserType], or empty if not found
    Optional<UserType> findById(UserTypeId id);

    /// Returns a paginated list of [UserType] instances.
    ///
    /// @param pageRequest the [PageRequest] carrying page number, size, filters, and sorting
    /// @return a [Page] of [UserType] instances
    Page<UserType> findAll(PageRequest pageRequest);

    /// Deletes a [UserType] by its identifier.
    ///
    /// @param id the [UserTypeId] of the user type to delete
    void deleteById(UserTypeId id);

    /// Checks whether a [UserType] with the given [UserRole] already exists.
    ///
    /// @param role the [UserRole] to check
    /// @return `true` if a user type with the given role exists, `false` otherwise
    boolean existsByRole(UserRole role);

    /// Checks whether a [UserType] with the given [UserRole] exists,
    /// excluding the one with the specified [UserTypeId].
    ///
    /// Useful for validation during update operations.
    ///
    /// @param role the [UserRole] to check
    /// @param id   the [UserTypeId] to exclude from the search
    /// @return `true` if another user type with the given role exists, `false` otherwise
    boolean existsByRoleAndIdNot(UserRole role, UserTypeId id);

    /// Checks whether a [UserType] with the given [UserTypeId] exists.
    ///
    /// @param id the [UserTypeId] to check
    /// @return `true` if a user type with the given identifier exists, `false` otherwise
    boolean existsById(UserTypeId id);
}
