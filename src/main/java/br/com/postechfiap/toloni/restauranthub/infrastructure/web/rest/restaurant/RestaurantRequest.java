package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.restaurant;

import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.CreateRestaurantUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.UpdateRestaurantUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Represents the HTTP request body for [Restaurant] operations.
///
/// @param name         the name of the restaurant
/// @param address      the address of the restaurant
/// @param cuisineType  the cuisine type of the restaurant
/// @param openingHours the opening hours of the restaurant
public record RestaurantRequest(String name, String address, String cuisineType, String openingHours) {

    /// Converts this request to a [CreateRestaurantUseCase.Input].
    ///
    /// @param ownerId the [UserId] of the authenticated user
    /// @return a new [CreateRestaurantUseCase.Input] with the data from this request
    public CreateRestaurantUseCase.Input toCreateInput(UserId ownerId) {
        return new CreateRestaurantUseCase.Input(
                name,
                address,
                cuisineType,
                openingHours,
                ownerId
        );
    }

    /// Converts this request to an [UpdateRestaurantUseCase.Input].
    ///
    /// @param id      the [RestaurantId] of the restaurant to update
    /// @param ownerId the [UserId] of the authenticated user
    /// @return a new [UpdateRestaurantUseCase.Input] with the data from this request
    public UpdateRestaurantUseCase.Input toUpdateInput(RestaurantId id, UserId ownerId) {
        return new UpdateRestaurantUseCase.Input(
                id,
                name,
                address,
                cuisineType,
                openingHours,
                ownerId
        );
    }

}