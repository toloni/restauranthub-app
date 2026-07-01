package br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem;

import br.com.postechfiap.toloni.restauranthub.application.gateways.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.application.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.application.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItem;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;

/// Use case responsible for retrieving a paginated list of [MenuItem] instances.
///
/// ## Flow
/// 1. Receives a [PageRequest] with pagination, filter, and sort parameters
/// 2. Retrieves a paginated list of [MenuItem] instances from the persistence layer
/// 3. Maps and returns them as a [Page] of [Output]
public class FindAllMenuItemsUseCase {

    private final MenuItemGateway menuItemGateway;

    /// @param menuItemGateway the gateway for [MenuItem] persistence operations
    public FindAllMenuItemsUseCase(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    /// Input data required to retrieve a paginated list of [MenuItem] instances.
    ///
    /// @param restaurantId the [RestaurantId] to filter by, or `null` for all items
    /// @param pageRequest  the [PageRequest] carrying page number, size, filters, and sorting
    public record Input(RestaurantId restaurantId, PageRequest pageRequest) {
    }

    /// Output data returned for each [MenuItem].
    ///
    /// @param id           the [MenuItemId] of the menu item
    /// @param name         the name of the menu item
    /// @param description  the description of the menu item
    /// @param price        the price of the menu item
    /// @param currency     the currency of the price
    /// @param dineInOnly   whether this item is available for dine-in only
    /// @param imagePath    the image path of the menu item
    /// @param restaurantId the [RestaurantId] of the restaurant this item belongs to
    public record Output(MenuItemId id, MenuItemName name, MenuItemDescription description, MenuItemPrice price,
                         boolean dineInOnly, MenuItemImagePath imagePath,
                         RestaurantId restaurantId, String restaurantName) {
    }


    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data carrying the [PageRequest]
    /// @return a [Page] of [Output] containing the paginated menu items
    public Page<Output> execute(Input input) {
        var page = menuItemGateway.findAllWithRestaurantName(input.restaurantId(), input.pageRequest());

        var content = page.content()
                .stream()
                .map(enriched -> new Output(
                        enriched.menuItem().getId(),
                        enriched.menuItem().getName(),
                        enriched.menuItem().getDescription(),
                        enriched.menuItem().getPrice(),
                        enriched.menuItem().isDineInOnly(),
                        enriched.menuItem().getImagePath(),
                        enriched.menuItem().getRestaurantId(),
                        enriched.restaurantName()
                ))
                .toList();

        return Page.of(content, page.pageNumber(), page.pageSize(), page.totalElements());
    }
}
