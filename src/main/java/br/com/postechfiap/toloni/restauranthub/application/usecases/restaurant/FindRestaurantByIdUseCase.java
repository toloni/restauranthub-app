package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.application.gateways.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Use case responsible for retrieving a [Restaurant] by its identifier.
///
/// ## Flow
/// 1. Searches for a [Restaurant] with the given [RestaurantId], enriched with the owner name
/// 2. Returns the [Restaurant] data if found
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [Restaurant] is found with the given [RestaurantId]
public class FindRestaurantByIdUseCase {

    private final RestaurantGateway restaurantGateway;

    /// @param restaurantGateway the gateway for [Restaurant] persistence operations
    public FindRestaurantByIdUseCase(RestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    /// Input data required to find a [Restaurant].
    ///
    /// @param id the [RestaurantId] of the restaurant to find
    public record Input(RestaurantId id) {
    }

    /// Output data returned for the found [Restaurant].
    ///
    /// @param id           the [RestaurantId] of the restaurant
    /// @param name         the name of the restaurant
    /// @param address      the address of the restaurant
    /// @param cuisineType  the cuisine type of the restaurant
    /// @param openingHours the opening hours of the restaurant
    /// @param ownerId      the [UserId] of the owner of the restaurant
    /// @param ownerName    the name of the owner of the restaurant
    public record Output(RestaurantId id, RestaurantName name, RestaurantAddress address,
                         RestaurantCuisineType cuisineType, RestaurantOpeningHours openingHours,
                         UserId ownerId, String ownerName) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to find a [Restaurant]
    /// @return the [Output] containing the found [Restaurant] data
    /// @throws NotFoundException if no [Restaurant] is found with the given [RestaurantId]
    public Output execute(Input input) {
        return restaurantGateway.findByIdWithOwnerName(input.id())
                .map(enriched -> new Output(
                        enriched.restaurant().getId(),
                        enriched.restaurant().getName(),
                        enriched.restaurant().getAddress(),
                        enriched.restaurant().getCuisineType(),
                        enriched.restaurant().getOpeningHours(),
                        enriched.restaurant().getOwnerId(),
                        enriched.ownerName()
                ))
                .orElseThrow(() -> new NotFoundException("Restaurant", input.id().getValue().toString()));
    }
}