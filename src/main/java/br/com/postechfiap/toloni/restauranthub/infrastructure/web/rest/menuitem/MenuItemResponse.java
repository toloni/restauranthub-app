package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.menuitem;

import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.CreateMenuItemUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.FindAllMenuItemsUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.FindMenuItemByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.UpdateMenuItemUseCase;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

/// Represents the HTTP response body for [MenuItem] operations.
///
/// @param id           the unique identifier of the menu item
/// @param name         the name of the menu item
/// @param description  the description of the menu item
/// @param price        the price of the menu item
/// @param currency     the currency code of the price
/// @param dineInOnly   whether this item is available for dine-in only
/// @param imagePath    the image path of the menu item
/// @param restaurantId the unique identifier of the restaurant this item belongs to
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MenuItemResponse(UUID id, String name, String description, BigDecimal price,
                               String currency, boolean dineInOnly, String imagePath,
                               UUID restaurantId, String restaurantName) {

    public static MenuItemResponse from(CreateMenuItemUseCase.Output output) {
        return new MenuItemResponse(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.price(),
                output.currency().getCurrencyCode(),
                output.dineInOnly(),
                output.imagePath(),
                output.restaurantId().getValue(),
                null
        );
    }

    public static MenuItemResponse from(UpdateMenuItemUseCase.Output output) {
        return new MenuItemResponse(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.price(),
                output.currency().getCurrencyCode(),
                output.dineInOnly(),
                output.imagePath(),
                output.restaurantId().getValue(),
                null
        );
    }

    public static MenuItemResponse from(FindMenuItemByIdUseCase.Output output) {
        return new MenuItemResponse(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.price(),
                output.currency().getCurrencyCode(),
                output.dineInOnly(),
                output.imagePath(),
                output.restaurantId().getValue(),
                output.restaurantName()
        );
    }

    public static MenuItemResponse from(FindAllMenuItemsUseCase.Output output) {
        return new MenuItemResponse(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.price(),
                output.currency().getCurrencyCode(),
                output.dineInOnly(),
                output.imagePath(),
                output.restaurantId().getValue(),
                output.restaurantName()
        );
    }
}
