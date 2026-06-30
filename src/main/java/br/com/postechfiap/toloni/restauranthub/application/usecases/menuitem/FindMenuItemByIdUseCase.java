package br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItem;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.MenuItemId;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.Currency;

/// Use case responsible for retrieving a [MenuItem] by its identifier.
///
/// ## Flow
/// 1. Searches for a [MenuItem] with the given [MenuItemId]
/// 2. Returns the [MenuItem] data if found
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [MenuItem] is found with the given [MenuItemId]
public class FindMenuItemByIdUseCase {

    private final MenuItemGateway menuItemGateway;

    /// @param menuItemGateway the gateway for [MenuItem] persistence operations
    public FindMenuItemByIdUseCase(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    /// Input data required to find a [MenuItem].
    ///
    /// @param id the [MenuItemId] of the menu item to find
    public record Input(MenuItemId id) {
    }

    /// Output data returned for the found [MenuItem].
    ///
    /// @param id           the [MenuItemId] of the menu item
    /// @param name         the name of the menu item
    /// @param description  the description of the menu item
    /// @param price        the price of the menu item
    /// @param currency     the currency of the price
    /// @param dineInOnly   whether this item is available for dine-in only
    /// @param imagePath    the image path of the menu item
    /// @param restaurantId the [RestaurantId] of the restaurant this item belongs to
    public record Output(MenuItemId id, String name, String description, BigDecimal price,
                         Currency currency, boolean dineInOnly, String imagePath,
                         RestaurantId restaurantId, String restaurantName) {}

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to find a [MenuItem]
    /// @return the [Output] containing the found [MenuItem] data
    /// @throws NotFoundException if no [MenuItem] is found with the given [MenuItemId]
    public Output execute(Input input) {
        return menuItemGateway.findByIdWithRestaurantName(input.id())
                .map(enriched -> new Output(
                        enriched.menuItem().getId(),
                        enriched.menuItem().getName().getValue(),
                        enriched.menuItem().getDescription().getValue(),
                        enriched.menuItem().getPrice().getAmount(),
                        enriched.menuItem().getPrice().getCurrency(),
                        enriched.menuItem().isDineInOnly(),
                        enriched.menuItem().getImagePath().getValue(),
                        enriched.menuItem().getRestaurantId(),
                        enriched.restaurantName()
                ))
                .orElseThrow(() -> new NotFoundException("MenuItem", input.id().getValue().toString()));
    }
}
