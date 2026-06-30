package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.restaurant;

import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.CreateRestaurantUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.FindAllRestaurantsUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.FindRestaurantByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.UpdateRestaurantUseCase;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

/// Represents the HTTP response body for [Restaurant] operations.
///
/// @param id           the unique identifier of the restaurant
/// @param name         the name of the restaurant
/// @param address      the address of the restaurant
/// @param cuisineType  the cuisine type of the restaurant
/// @param openingHours the opening hours of the restaurant
/// @param ownerId      the unique identifier of the owner user
/// @param ownerName    the name of the owner user
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestaurantResponse(UUID id, String name, String address, String cuisineType,
                                 String openingHours, UUID ownerId, String ownerName) {

    public static RestaurantResponse from(CreateRestaurantUseCase.Output output) {
        return new RestaurantResponse(
                output.id().getValue(),
                output.name(),
                output.address(),
                output.cuisineType(),
                output.openingHours(),
                output.ownerId().getValue(),
                null
        );
    }

    public static RestaurantResponse from(UpdateRestaurantUseCase.Output output) {
        return new RestaurantResponse(
                output.id().getValue(),
                output.name(),
                output.address(),
                output.cuisineType(),
                output.openingHours(),
                output.ownerId().getValue(),
                null
        );
    }

    public static RestaurantResponse from(FindRestaurantByIdUseCase.Output output) {
        return new RestaurantResponse(
                output.id().getValue(),
                output.name(),
                output.address(),
                output.cuisineType(),
                output.openingHours(),
                output.ownerId().getValue(),
                output.ownerName()
        );
    }

    public static RestaurantResponse from(FindAllRestaurantsUseCase.Output output) {
        return new RestaurantResponse(
                output.id().getValue(),
                output.name(),
                output.address(),
                output.cuisineType(),
                output.openingHours(),
                output.ownerId().getValue(),
                output.ownerName()
        );
    }
}
