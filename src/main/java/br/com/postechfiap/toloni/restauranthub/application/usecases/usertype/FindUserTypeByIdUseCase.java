package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Use case responsible for retrieving a [UserType] by its identifier.
///
/// ## Flow
/// 1. Searches for a [UserType] with the given [UserTypeId]
/// 2. Returns the [UserType] data if found
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [UserType] is found with the given [UserTypeId]
public class FindUserTypeByIdUseCase {

    private final UserTypeGateway userTypeGateway;

    /// @param userTypeGateway the gateway for [UserType] persistence operations
    public FindUserTypeByIdUseCase(UserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    /// Input data required to find a [UserType].
    ///
    /// @param id the [UserTypeId] of the user type to find
    public record Input(UserTypeId id) {
    }

    /// Output data returned for the found [UserType].
    ///
    /// @param id          the [UserTypeId] of the user type
    /// @param name        the name of the user type
    /// @param description the description of the user type
    /// @param role        the [UserRole] of the user type
    public record Output(UserTypeId id, String name, String description, UserRole role) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to find a [UserType]
    /// @return the [Output] containing the found [UserType] data
    /// @throws NotFoundException if no [UserType] is found with the given [UserTypeId]
    public Output execute(Input input) {
        return userTypeGateway.findById(input.id())
                .map(userType -> new Output(
                        userType.getId(),
                        userType.getName().getValue(),
                        userType.getDescription().getValue(),
                        userType.getRole()
                ))
                .orElseThrow(() -> new NotFoundException("UserType", input.id().getValue().toString()));
    }
}
