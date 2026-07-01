package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.application.gateways.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;

/// Use case responsible for creating a new [UserType].
///
/// Orchestrates the domain rules and persistence contract to ensure
/// a [UserType] is created only when all invariants are satisfied.
///
/// ## Flow
/// 1. Checks whether a [UserType] with the given [UserRole] already exists
/// 2. Creates the [UserType] entity with the provided attributes
/// 3. Persists and returns the created [UserType]
///
/// ## Exceptions
/// - Throws [AlreadyExistsException] if a [UserType] with the given role already exists
public class CreateUserTypeUseCase {

    private final UserTypeGateway userTypeGateway;

    /// @param userTypeGateway the gateway for [UserType] persistence operations
    public CreateUserTypeUseCase(UserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    /// Input data required to create a [UserType].
    ///
    /// @param name        the name of the user type
    /// @param description the description of the user type
    /// @param role        the [UserRole] of the user type
    public record Input(String name, String description, UserRole role) {
    }

    /// Output data returned after creating a [UserType].
    ///
    /// @param id          the generated [UserTypeId]
    /// @param name        the name of the created user type
    /// @param description the description of the created user type
    /// @param role        the [UserRole] of the created user type
    public record Output(UserTypeId id, UserTypeName name, UserTypeDescription description, UserRole role) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to create a [UserType]
    /// @return the [Output] containing the created [UserType] data
    /// @throws AlreadyExistsException if a [UserType] with the given role already exists
    public Output execute(Input input) {
        if (userTypeGateway.existsByRole(input.role()))
            throw new AlreadyExistsException("UserType with Role", input.role().name());

        var userType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of(input.name()),
                UserTypeDescription.of(input.description()),
                input.role()
        );

        var saved = userTypeGateway.save(userType);

        return new Output(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getRole()
        );
    }
}
