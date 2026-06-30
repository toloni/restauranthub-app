package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Use case responsible for deleting a [UserType] by its identifier.
///
/// Ensures the [UserType] is not associated with any [User] before deleting.
///
/// ## Flow
/// 1. Checks whether any [User] is associated with the given [UserTypeId]
/// 2. Delegates the deletion to the persistence layer
///
/// ## Exceptions
/// - Throws [EntityInUseException] if any [User] is associated with the given [UserTypeId]
public class DeleteUserTypeUseCase {

    private final UserTypeGateway userTypeGateway;
    private final UserGateway userGateway;

    /// @param userTypeGateway the gateway for [UserType] persistence operations
    /// @param userGateway     the gateway for [User] persistence operations
    public DeleteUserTypeUseCase(UserTypeGateway userTypeGateway, UserGateway userGateway) {
        this.userTypeGateway = userTypeGateway;
        this.userGateway = userGateway;
    }

    /// Input data required to delete a [UserType].
    ///
    /// @param id the [UserTypeId] of the user type to delete
    public record Input(UserTypeId id) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to delete a [UserType]
    /// @throws EntityInUseException if any [User] is associated with the given [UserTypeId]
    public void execute(Input input) {
        if (userGateway.existsByUserTypeId(input.id()))
            throw new EntityInUseException("UserType", input.id().getValue().toString());

        userTypeGateway.deleteById(input.id());
    }
}
