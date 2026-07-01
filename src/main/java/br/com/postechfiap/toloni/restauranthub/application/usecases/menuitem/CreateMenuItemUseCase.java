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

/// Use case responsible for creating a new [MenuItem].
///
/// Orchestrates the domain rules and persistence contract to ensure
/// a [MenuItem] is created only when all invariants are satisfied.
///
/// ## Flow
/// 1. Finds the [Restaurant] by its identifier
/// 2. Ensures the requester is the owner of the [Restaurant]
/// 3. Checks whether a [MenuItem] with the given name already exists in the [Restaurant]
/// 4. Creates the [MenuItem] entity with the provided attributes
/// 5. Persists and returns the created [MenuItem]
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [Restaurant] is found with the given [RestaurantId]
/// - Throws [UnauthorizedException] if the requester is not the owner of the [Restaurant]
/// - Throws [AlreadyExistsException] if a [MenuItem] with the given name already exists in the [Restaurant]
public class CreateMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final AuthorizationService authorizationService;

    /// @param menuItemGateway   the gateway for [MenuItem] persistence operations
    /// @param restaurantGateway the gateway for [Restaurant] persistence operations
    public CreateMenuItemUseCase(
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway,
            AuthorizationService authorizationService) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.authorizationService = authorizationService;
    }

    /// Input data required to create a [MenuItem].
    ///
    /// @param name         the name of the menu item
    /// @param description  the description of the menu item
    /// @param price        the price of the menu item
    /// @param currency     the currency of the price
    /// @param dineInOnly   whether this item is available for dine-in only
    /// @param imagePath    the image path of the menu item, may be null
    /// @param restaurantId the [RestaurantId] of the restaurant this item belongs to
    /// @param ownerId      the [UserId] of the authenticated user
    public record Input(String name, String description, BigDecimal price, Currency currency,
                        boolean dineInOnly, String imagePath, RestaurantId restaurantId, UserId ownerId) {
    }

    /// Output data returned after creating a [MenuItem].
    ///
    /// @param id           the generated [MenuItemId]
    /// @param name         the name of the created menu item
    /// @param description  the description of the created menu item
    /// @param price        the price of the created menu item
    /// @param currency     the currency of the price
    /// @param dineInOnly   whether this item is available for dine-in only
    /// @param imagePath    the image path of the created menu item
    /// @param restaurantId the [RestaurantId] of the restaurant this item belongs to
    public record Output(MenuItemId id, MenuItemName name, MenuItemDescription description, MenuItemPrice price,
                         boolean dineInOnly, MenuItemImagePath imagePath, RestaurantId restaurantId) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to create a [MenuItem]
    /// @return the [Output] containing the created [MenuItem] data
    /// @throws NotFoundException      if no [Restaurant] is found with the given [RestaurantId]
    /// @throws UnauthorizedException  if the requester is not the owner of the [Restaurant]
    /// @throws AlreadyExistsException if a [MenuItem] with the given name already exists in the [Restaurant]
    public Output execute(Input input) {
        var restaurant = restaurantGateway.findById(input.restaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurant", input.restaurantId().getValue().toString()));

        authorizationService.validateMenuItemOwnership(restaurant, input.ownerId());

        var name = MenuItemName.of(input.name());

        if (menuItemGateway.existsByNameAndRestaurantId(name, input.restaurantId()))
            throw new AlreadyExistsException("MenuItem", name.getValue());

        var menuItem = new MenuItem(
                MenuItemId.generate(),
                name,
                MenuItemDescription.of(input.description()),
                MenuItemPrice.of(input.price(), input.currency()),
                input.dineInOnly(),
                MenuItemImagePath.of(input.imagePath()),
                input.restaurantId()
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
