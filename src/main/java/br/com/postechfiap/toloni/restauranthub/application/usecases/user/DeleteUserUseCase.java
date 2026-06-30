package br.com.postechfiap.toloni.restauranthub.application.usecases.user;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Use case responsible for deleting a [User] by its identifier.
///
/// Ensures the [User] is not associated with any [Restaurant] before deleting.
///
/// ## Flow
/// 1. Checks whether the [User] is associated with any [Restaurant]
/// 2. Delegates the deletion to the persistence layer
///
/// ## Exceptions
/// - Throws [EntityInUseException] if the [User] is associated with a [Restaurant]
public class DeleteUserUseCase {

    private final UserGateway userGateway;
    private final RestaurantGateway restaurantGateway;

    /// @param userGateway       the gateway for [User] persistence operations
    /// @param restaurantGateway the gateway for [Restaurant] persistence operations
    public DeleteUserUseCase(UserGateway userGateway, RestaurantGateway restaurantGateway) {
        this.userGateway = userGateway;
        this.restaurantGateway = restaurantGateway;
    }

    /// Input data required to delete a [User].
    ///
    /// @param id the [UserId] of the user to delete
    public record Input(UserId id) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to delete a [User]
    /// @throws EntityInUseException if the [User] is associated with a [Restaurant]
    public void execute(Input input) {
        if (restaurantGateway.existsByOwnerId(input.id()))
            throw new EntityInUseException("User", input.id().getValue().toString());

        userGateway.deleteById(input.id());
    }
}
