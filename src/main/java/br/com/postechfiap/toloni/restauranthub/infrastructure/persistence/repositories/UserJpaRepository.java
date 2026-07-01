package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories;

import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/// Spring Data JPA repository for [UserJpaEntity].
///
/// Extends [JpaRepository] and [JpaSpecificationExecutor] to provide standard CRUD
/// operations, pagination, and dynamic specification-based filtering.
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID>,
        JpaSpecificationExecutor<UserJpaEntity> {

    /// Returns a paginated list of all users, eagerly fetching the associated [UserTypeJpaEntity]
    /// to avoid N+1 queries when accessing [UserJpaEntity#getUserType()].
    ///
    /// @param pageable the pagination parameters
    /// @return a [Page] of [UserJpaEntity] with userType loaded
    @EntityGraph(value = "User.withUserType")
    @Override
    Page<UserJpaEntity> findAll(Pageable pageable);

    /// Returns a filtered and paginated list of users matching the given specification,
    /// eagerly fetching the associated [UserTypeJpaEntity].
    ///
    /// @param spec     the [Specification] to filter users
    /// @param pageable the pagination parameters
    /// @return a [Page] of [UserJpaEntity] with userType loaded
    @EntityGraph(value = "User.withUserType")
    @Override
    Page<UserJpaEntity> findAll(Specification<UserJpaEntity> spec, Pageable pageable);

    /// Returns a user by its id, eagerly fetching the associated [UserTypeJpaEntity]
    /// to avoid a lazy-load on [UserJpaEntity#getUserType()].
    ///
    /// @param id the UUID of the user
    /// @return an [Optional] containing the [UserJpaEntity] with userType loaded, or empty if not found
    @EntityGraph(value = "User.withUserType")
    @Override
    Optional<UserJpaEntity> findById(UUID id);

    /// Checks whether a user with the given email exists.
    ///
    /// @param email the email to check
    /// @return `true` if a user with that email exists
    boolean existsByEmail(String email);

    /// Checks whether a user with the given email exists, excluding the one with the given id.
    ///
    /// @param email the email to check
    /// @param id    the id to exclude from the search
    /// @return `true` if another user with that email exists
    boolean existsByEmailAndIdNot(String email, UUID id);

    /// Checks whether any user is associated with the given user type id.
    ///
    /// @param userTypeId the user type UUID to check
    /// @return `true` if at least one user has that user type
    boolean existsByUserTypeId(UUID userTypeId);

}
