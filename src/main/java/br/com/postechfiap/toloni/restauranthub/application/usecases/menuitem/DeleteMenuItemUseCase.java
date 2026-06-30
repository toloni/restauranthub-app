package br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.MenuItemId;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Use case responsible for deleting a [MenuItem] by its identifier.
///
/// Ensures that only the owner of the [Restaurant] can delete its items.
///
/// ## Flow
/// 1. Finds the [MenuItem] by its identifier
/// 2. Finds the [Restaurant] to validate ownership
/// 3. Ensures the requester is the owner of the [Restaurant]
/// 4. Delegates the deletion to the persistence layer
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [MenuItem] or [Restaurant] is found
/// - Throws [UnauthorizedException] if the requester is not the owner of the [Restaurant]
public class DeleteMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final AuthorizationService authorizationService;

    /// @param menuItemGateway   the gateway for [MenuItem] persistence operations
    /// @param restaurantGateway the gateway for [Restaurant] persistence operations
    public DeleteMenuItemUseCase(
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway,
            AuthorizationService authorizationService) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.authorizationService = authorizationService;
    }

    /// Input data required to delete a [MenuItem].
    ///
    /// @param id      the [MenuItemId] of the menu item to delete
    /// @param ownerId the [UserId] of the authenticated user
    public record Input(MenuItemId id, UserId ownerId) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to delete a [MenuItem]
    /// @throws NotFoundException     if no [MenuItem] or [Restaurant] is found
    /// @throws UnauthorizedException if the requester is not the owner of the [Restaurant]
    public void execute(Input input) {
        var menuItem = menuItemGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("MenuItem", input.id().getValue().toString()));

        var restaurant = restaurantGateway.findById(menuItem.getRestaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurant", menuItem.getRestaurantId().getValue().toString()));

        authorizationService.validateMenuItemOwnership(restaurant, input.ownerId());

        menuItemGateway.deleteById(input.id());
    }
}
