package br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem;

import br.com.postechfiap.toloni.restauranthub.application.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.application.gateways.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.application.gateways.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItem;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

import java.math.BigDecimal;
import java.util.Currency;

/// Use case responsible for updating an existing [MenuItem].
///
/// Orchestrates the domain rules and persistence contract to ensure
/// a [MenuItem] is updated only when all invariants are satisfied.
///
/// ## Flow
/// 1. Finds the [MenuItem] by its identifier
/// 2. Finds the [Restaurant] to validate ownership
/// 3. Ensures the requester is the owner of the [Restaurant]
/// 4. Checks whether another [MenuItem] with the given name already exists in the [Restaurant]
/// 5. Updates the [MenuItem] entity with the provided attributes
/// 6. Persists and returns the updated [MenuItem]
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [MenuItem] or [Restaurant] is found
/// - Throws [UnauthorizedException] if the requester is not the owner of the [Restaurant]
/// - Throws [AlreadyExistsException] if another [MenuItem] with the given name already exists in the [Restaurant]
public class UpdateMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final AuthorizationService authorizationService;

    /// @param menuItemGateway   the gateway for [MenuItem] persistence operations
    /// @param restaurantGateway the gateway for [Restaurant] persistence operations
    public UpdateMenuItemUseCase(
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway,
            AuthorizationService authorizationService) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.authorizationService = authorizationService;
    }

    /// Input data required to update a [MenuItem].
    ///
    /// @param id          the [MenuItemId] of the menu item to update
    /// @param name        the new name, or `null` to keep the current value
    /// @param description the new description, or `null` to keep the current value
    /// @param price       the new price, or `null` to keep the current value
    /// @param currency    the new currency, or `null` to keep the current value
    /// @param dineInOnly  the new dine-in only flag, or `null` to keep the current value
    /// @param imagePath   the new image path, or `null` to keep the current value
    /// @param ownerId     the [UserId] of the authenticated user
    public record Input(MenuItemId id, String name, String description, BigDecimal price,
                        Currency currency, Boolean dineInOnly, String imagePath, UserId ownerId) {
    }

    /// Output data returned after updating a [MenuItem].
    ///
    /// @param id           the [MenuItemId] of the updated menu item
    /// @param name         the name of the updated menu item
    /// @param description  the description of the updated menu item
    /// @param price        the price of the updated menu item
    /// @param currency     the currency of the price
    /// @param dineInOnly   whether this item is available for dine-in only
    /// @param imagePath    the image path of the updated menu item
    /// @param restaurantId the [RestaurantId] of the restaurant this item belongs to
    public record Output(MenuItemId id, MenuItemName name, MenuItemDescription description, MenuItemPrice price,
                         boolean dineInOnly, MenuItemImagePath imagePath, RestaurantId restaurantId) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to update a [MenuItem]
    /// @return the [Output] containing the updated [MenuItem] data
    /// @throws NotFoundException      if no [MenuItem] or [Restaurant] is found
    /// @throws UnauthorizedException  if the requester is not the owner of the [Restaurant]
    /// @throws AlreadyExistsException if another [MenuItem] with the given name already exists in the [Restaurant]
    public Output execute(Input input) {
        var menuItem = menuItemGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("MenuItem", input.id().getValue().toString()));

        var restaurant = restaurantGateway.findById(menuItem.getRestaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurant", menuItem.getRestaurantId().getValue().toString()));

        authorizationService.validateMenuItemOwnership(restaurant, input.ownerId());

        if (input.name() != null) {
            var name = MenuItemName.of(input.name());
            if (menuItemGateway.existsByNameAndRestaurantIdAndIdNot(name, menuItem.getRestaurantId(), input.id()))
                throw new AlreadyExistsException("MenuItem", name.getValue());
        }

        var newPrice = input.price() != null && input.currency() != null
                ? MenuItemPrice.of(input.price(), input.currency())
                : null;

        menuItem.update(
                input.name() != null ? MenuItemName.of(input.name()) : null,
                input.description() != null ? MenuItemDescription.of(input.description()) : null,
                newPrice,
                input.dineInOnly(),
                input.imagePath() != null ? MenuItemImagePath.of(input.imagePath()) : null
        );

        var saved = menuItemGateway.save(menuItem);

        return toOutput(saved);
    }

    private Output toOutput(MenuItem menuItem) {
        return new Output(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isDineInOnly(),
                menuItem.getImagePath(),
                menuItem.getRestaurantId()
        );
    }
}
