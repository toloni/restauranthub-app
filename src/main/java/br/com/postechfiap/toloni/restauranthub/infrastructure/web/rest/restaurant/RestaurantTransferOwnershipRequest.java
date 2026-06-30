package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.restaurant;

import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.TransferRestaurantOwnershipUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Represents the HTTP request body for [Restaurant] ownership transfer operations.
///
/// @param newOwnerId the unique identifier of the new owner
public record RestaurantTransferOwnershipRequest(String newOwnerId) {

    /// Converts this request to a [TransferRestaurantOwnershipUseCase.Input].
    ///
    /// @param id          the [RestaurantId] of the restaurant to transfer
    /// @param requesterId the [UserId] of the authenticated user performing the operation
    /// @param newOwnerId  the [UserId] of the new owner
    /// @return a new [TransferRestaurantOwnershipUseCase.Input] with the data from this request
    public TransferRestaurantOwnershipUseCase.Input toUpdateOwnershipInput(
            RestaurantId id,
            UserId requesterId,
            UserId newOwnerId) {
        return new TransferRestaurantOwnershipUseCase.Input(
                id,
                requesterId,
                newOwnerId
        );
    }
}