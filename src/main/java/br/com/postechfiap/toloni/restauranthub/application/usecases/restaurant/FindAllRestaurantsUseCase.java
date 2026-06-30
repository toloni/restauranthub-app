package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Use case responsible for retrieving a paginated list of [Restaurant] instances.
///
/// ## Flow
/// 1. Receives a [PageRequest] with pagination, filter, and sort parameters
/// 2. Retrieves a paginated list of [Restaurant] instances enriched with owner name
/// 3. Maps and returns them as a [Page] of [Output]
public class FindAllRestaurantsUseCase {

    private final RestaurantGateway restaurantGateway;

    /// @param restaurantGateway the gateway for [Restaurant] persistence operations
    public FindAllRestaurantsUseCase(RestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    /// Input data required to retrieve a paginated list of [Restaurant] instances.
    ///
    /// @param pageRequest the [PageRequest] carrying page number, size, filters, and sorting
    public record Input(PageRequest pageRequest) {
    }

    /// Output data returned for each [Restaurant].
    ///
    /// @param id           the [RestaurantId] of the restaurant
    /// @param name         the name of the restaurant
    /// @param address      the address of the restaurant
    /// @param cuisineType  the cuisine type of the restaurant
    /// @param openingHours the opening hours of the restaurant
    /// @param ownerId      the [UserId] of the owner of the restaurant
    /// @param ownerName    the name of the owner of the restaurant
    public record Output(RestaurantId id, String name, String address, String cuisineType,
                         String openingHours, UserId ownerId, String ownerName) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data carrying the [PageRequest]
    /// @return a [Page] of [Output] containing the paginated restaurants
    public Page<Output> execute(Input input) {
        var page = restaurantGateway.findAllWithOwnerName(input.pageRequest());

        var content = page.getContent()
                .stream()
                .map(enriched -> new Output(
                        enriched.restaurant().getId(),
                        enriched.restaurant().getName().getValue(),
                        enriched.restaurant().getAddress().getValue(),
                        enriched.restaurant().getCuisineType().getValue(),
                        enriched.restaurant().getOpeningHours().getValue(),
                        enriched.restaurant().getOwnerId(),
                        enriched.ownerName()
                ))
                .toList();

        return Page.of(content, page.getPageNumber(), page.getPageSize(), page.getTotalElements());
    }
}
