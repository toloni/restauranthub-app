package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.menuitem;

import br.com.postechfiap.toloni.restauranthub.adapters.controllers.MenuItemController;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.DeleteMenuItemUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.FindAllMenuItemsUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.FindMenuItemByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.MenuItemId;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.*;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/// REST controller that implements [MenuItemApi] for [MenuItem] operations.
///
/// Receives HTTP requests, delegates to [MenuItemController], and maps
/// the results to [MenuItemResponse].
@RestController
public class MenuItemRestController implements MenuItemApi {

    private final MenuItemController menuItemController;

    /// @param menuItemController the [MenuItemController] adapter
    public MenuItemRestController(MenuItemController menuItemController) {
        this.menuItemController = menuItemController;
    }

    @Override
    public MenuItemResponse create(@RequestHeader("X-User-Id") String userId,
                                   @RequestBody MenuItemRequest request) {
        return MenuItemResponse.from(menuItemController.create(
                request.toCreateInput(UserId.of(userId))
        ));
    }

    @Override
    public MenuItemResponse update(@PathVariable String id,
                                   @RequestHeader("X-User-Id") String userId,
                                   @RequestBody MenuItemRequest request) {
        return MenuItemResponse.from(menuItemController.update(
                request.toUpdateInput(MenuItemId.of(id), UserId.of(userId))
        ));
    }

    @Override
    public void delete(@PathVariable String id,
                       @RequestHeader("X-User-Id") String userId) {
        menuItemController.delete(new DeleteMenuItemUseCase.Input(
                MenuItemId.of(id),
                UserId.of(userId)
        ));
    }

    @Override
    public MenuItemResponse findById(@PathVariable String id) {
        return MenuItemResponse.from(menuItemController.findById(
                new FindMenuItemByIdUseCase.Input(MenuItemId.of(id))
        ));
    }

    @Override
    public Page<MenuItemResponse> findAll(
            @RequestParam(required = false) String restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String filterValue) {

        var sorts = sort != null
                ? List.<PageSort>of(PageSort.of(sort, direction != null
                ? SortDirection.valueOf(direction.toUpperCase())
                : SortDirection.ASC))
                : List.<PageSort>of();

        var filters = filter != null && filterValue != null
                ? List.<PageFilter>of(PageFilter.of(filter, filterValue))
                : List.<PageFilter>of();

        var resolvedRestaurantId = restaurantId != null ? RestaurantId.of(restaurantId) : null;

        var output = menuItemController.findAll(
                new FindAllMenuItemsUseCase.Input(
                        resolvedRestaurantId,
                        PageRequest.of(page, size, filters, sorts)
                )
        );

        var content = output.getContent().stream()
                .map(MenuItemResponse::from)
                .toList();

        return Page.of(content, output.getPageNumber(), output.getPageSize(), output.getTotalElements());
    }
}
