package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.restaurant;

import br.com.postechfiap.toloni.restauranthub.adapters.controllers.RestaurantController;
import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.DeleteRestaurantUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.FindAllRestaurantsUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.FindRestaurantByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.*;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/// REST controller that implements [RestaurantApi] for [Restaurant] operations.
///
/// Receives HTTP requests, delegates to [RestaurantController], and maps
/// the results to [RestaurantResponse].
@RestController
public class RestaurantRestController implements RestaurantApi {

    private final RestaurantController restaurantController;

    /// @param restaurantController the [RestaurantController] adapter
    public RestaurantRestController(RestaurantController restaurantController) {
        this.restaurantController = restaurantController;
    }

    @Override
    public RestaurantResponse create(@RequestHeader("X-User-Id") String userId,
                                     @RequestBody RestaurantRequest request) {
        return RestaurantResponse.from(restaurantController.create(
                request.toCreateInput(UserId.of(userId))
        ));
    }

    @Override
    public RestaurantResponse update(@PathVariable String id,
                                     @RequestHeader("X-User-Id") String userId,
                                     @RequestBody RestaurantRequest request) {
        return RestaurantResponse.from(restaurantController.update(
                request.toUpdateInput(RestaurantId.of(id), UserId.of(userId))
        ));
    }

    @Override
    public RestaurantTransferOwnershipResponse transferOwnership(@PathVariable String id,
                                     @RequestHeader("X-User-Id") String requesterId,
                                     @RequestBody RestaurantTransferOwnershipRequest request) {
        return RestaurantTransferOwnershipResponse.from(restaurantController.transferOwnership(
                request.toUpdateOwnershipInput(
                        RestaurantId.of(id),
                        UserId.of(requesterId),
                        UserId.of(request.newOwnerId()))
        ));
    }

    @Override
    public void delete(@PathVariable String id,
                       @RequestHeader("X-User-Id") String userId) {
        restaurantController.delete(new DeleteRestaurantUseCase.Input(
                RestaurantId.of(id),
                UserId.of(userId)
        ));
    }

    @Override
    public RestaurantResponse findById(@PathVariable String id) {
        return RestaurantResponse.from(restaurantController.findById(
                new FindRestaurantByIdUseCase.Input(RestaurantId.of(id))
        ));
    }

    @Override
    public Page<RestaurantResponse> findAll(
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

        var output = restaurantController.findAll(
                new FindAllRestaurantsUseCase.Input(PageRequest.of(page, size, filters, sorts))
        );

        var content = output.getContent().stream()
                .map(RestaurantResponse::from)
                .toList();

        return Page.of(content, output.getPageNumber(), output.getPageSize(), output.getTotalElements());
    }
}
