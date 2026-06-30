package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;

/// Use case responsible for updating an existing [UserType].
///
/// Orchestrates the domain rules and persistence contract to ensure
/// a [UserType] is updated only when all invariants are satisfied.
///
/// ## Flow
/// 1. Checks whether a [UserType] with the given [UserTypeId] exists
/// 2. Checks whether another [UserType] with the given [UserRole] already exists
/// 3. Updates the [UserType] entity with the provided attributes
/// 4. Persists and returns the updated [UserType]
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [UserType] is found with the given [UserTypeId]
/// - Throws [AlreadyExistsException] if another [UserType] with the given role already exists
public class UpdateUserTypeUseCase {

    private final UserTypeGateway userTypeGateway;

    /// @param userTypeGateway the gateway for [UserType] persistence operations
    public UpdateUserTypeUseCase(UserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    /// Input data required to update a [UserType].
    ///
    /// @param id          the [UserTypeId] of the user type to update
    /// @param name        the new name, or `null` to keep the current value
    /// @param description the new description, or `null` to keep the current value
    /// @param role        the new [UserRole], or `null` to keep the current value
    public record Input(UserTypeId id, String name, String description, UserRole role) {
    }

    /// Output data returned after updating a [UserType].
    ///
    /// @param id          the [UserTypeId] of the updated user type
    /// @param name        the name of the updated user type
    /// @param description the description of the updated user type
    /// @param role        the [UserRole] of the updated user type
    public record Output(UserTypeId id, String name, String description, UserRole role) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to update a [UserType]
    /// @return the [Output] containing the updated [UserType] data
    /// @throws NotFoundException      if no [UserType] is found with the given [UserTypeId]
    /// @throws AlreadyExistsException if another [UserType] with the given role already exists
    public Output execute(Input input) {
        var userType = userTypeGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("UserType", input.id().getValue().toString()));

        if (input.role() != null && userTypeGateway.existsByRoleAndIdNot(input.role(), input.id()))
            throw new AlreadyExistsException("UserType Role", input.role().name());

        userType.update(
                input.name() != null ? UserTypeName.of(input.name()) : null,
                input.description() != null ? UserTypeDescription.of(input.description()) : null,
                input.role()
        );

        var saved = userTypeGateway.save(userType);

        return new Output(
                saved.getId(),
                saved.getName().getValue(),
                saved.getDescription().getValue(),
                saved.getRole()
        );
    }
}
