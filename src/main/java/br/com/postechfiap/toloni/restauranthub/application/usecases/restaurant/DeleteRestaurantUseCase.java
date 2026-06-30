package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Use case responsible for deleting a [Restaurant] by its identifier.
///
/// Ensures that only the owner of the [Restaurant] can delete it,
/// and that it has no associated [MenuItem] instances.
///
/// ## Flow
/// 1. Finds the [Restaurant] by its identifier
/// 2. Ensures the requester is the owner of the [Restaurant]
/// 3. Checks whether the [Restaurant] has any associated [MenuItem] instances
/// 4. Delegates the deletion to the persistence layer
///
/// ## Exceptions
/// - Throws [NotFoundException]     if no [Restaurant] is found with the given [RestaurantId]
/// - Throws [UnauthorizedException] if the requester is not the owner of the [Restaurant]
/// - Throws [EntityInUseException]  if the [Restaurant] has associated [MenuItem] instances
public class DeleteRestaurantUseCase {

    private final RestaurantGateway restaurantGateway;
    private final MenuItemGateway menuItemGateway;
    private final AuthorizationService authorizationService;

    /// @param restaurantGateway the gateway for [Restaurant] persistence operations
    /// @param menuItemGateway   the gateway for [MenuItem] persistence operations
    public DeleteRestaurantUseCase(
            RestaurantGateway restaurantGateway,
            MenuItemGateway menuItemGateway,
            AuthorizationService authorizationService) {
        this.restaurantGateway = restaurantGateway;
        this.menuItemGateway = menuItemGateway;
        this.authorizationService = authorizationService;
    }

    /// Input data required to delete a [Restaurant].
    ///
    /// @param id      the [RestaurantId] of the restaurant to delete
    /// @param ownerId the [UserId] of the authenticated user
    public record Input(RestaurantId id, UserId ownerId) {}

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to delete a [Restaurant]
    /// @throws NotFoundException     if no [Restaurant] is found with the given [RestaurantId]
    /// @throws UnauthorizedException if the requester is not the owner of the [Restaurant]
    /// @throws EntityInUseException  if the [Restaurant] has associated [MenuItem] instances
    public void execute(Input input) {
        var restaurant = restaurantGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("Restaurant", input.id().getValue().toString()));

        authorizationService.validateRestaurantOwnership(restaurant, input.ownerId());

        menuItemGateway.deleteAllByRestaurantId(input.id());

        restaurantGateway.deleteById(input.id());
    }
}
