package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.restaurant;

import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.TransferRestaurantOwnershipUseCase;

/// Represents the HTTP response body for [Restaurant] ownership transfer operations.
///
/// @param newOwnerId the unique identifier of the new owner
public record RestaurantTransferOwnershipResponse(String newOwnerId) {

    /// Converts a [TransferRestaurantOwnershipUseCase.Output] to a `RestaurantTransferOwnershipResponse`.
    ///
    /// @param output the [TransferRestaurantOwnershipUseCase.Output] to convert
    /// @return a new `RestaurantTransferOwnershipResponse` with the data from the output
    public static RestaurantTransferOwnershipResponse from(TransferRestaurantOwnershipUseCase.Output output) {
        return new RestaurantTransferOwnershipResponse(
                output.ownerId().getValue().toString()
        );
    }
}