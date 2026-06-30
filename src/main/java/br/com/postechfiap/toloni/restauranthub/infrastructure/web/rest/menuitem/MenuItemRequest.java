package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.menuitem;

import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.CreateMenuItemUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.UpdateMenuItemUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.MenuItemId;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

import java.math.BigDecimal;
import java.util.Currency;

/// Represents the HTTP request body for [MenuItem] operations.
///
/// @param name        the name of the menu item
/// @param description the description of the menu item
/// @param price       the price of the menu item
/// @param currency    the currency code of the price (e.g. BRL, USD)
/// @param dineInOnly  whether this item is available for dine-in only
/// @param imagePath   the image path of the menu item, may be null
public record MenuItemRequest(String name, String description, BigDecimal price,
                              String currency, Boolean dineInOnly, String imagePath, String restaurantId) {

    /// Converts this request to a [CreateMenuItemUseCase.Input].
    ///
    /// @param restaurantId the [RestaurantId] of the restaurant this item belongs to
    /// @param ownerId      the [UserId] of the authenticated user
    /// @return a new [CreateMenuItemUseCase.Input] with the data from this request
    public CreateMenuItemUseCase.Input toCreateInput(UserId ownerId) {
        return new CreateMenuItemUseCase.Input(
                name,
                description,
                price,
                currency == null ? null : Currency.getInstance(currency),
                dineInOnly,
                imagePath,
                RestaurantId.of(restaurantId),
                ownerId
        );
    }

    /// Converts this request to an [UpdateMenuItemUseCase.Input].
    ///
    /// @param id      the [MenuItemId] of the menu item to update
    /// @param ownerId the [UserId] of the authenticated user
    /// @return a new [UpdateMenuItemUseCase.Input] with the data from this request
    public UpdateMenuItemUseCase.Input toUpdateInput(MenuItemId id, UserId ownerId) {
        return new UpdateMenuItemUseCase.Input(
                id,
                name,
                description,
                price,
                currency != null ? Currency.getInstance(currency) : null,
                dineInOnly,
                imagePath,
                ownerId
        );
    }
}
