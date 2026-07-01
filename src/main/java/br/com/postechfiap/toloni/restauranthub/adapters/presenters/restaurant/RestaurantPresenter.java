package br.com.postechfiap.toloni.restauranthub.adapters.presenters.restaurant;

import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.*;

/// Presenter that converts [Restaurant] use case outputs into [RestaurantViewModel].
///
/// Decouples the adapter layer from use case output types, producing
/// a uniform view model suitable for any presentation layer (REST, gRPC, etc.).
public class RestaurantPresenter {

    /// Converts a [CreateRestaurantUseCase.Output] to a [RestaurantViewModel].
    ///
    /// @param output the output from the create use case
    /// @return the corresponding [RestaurantViewModel]
    public RestaurantViewModel present(CreateRestaurantUseCase.Output output) {
        return new RestaurantViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.address().getValue(),
                output.cuisineType().getValue(),
                output.openingHours().getValue(),
                output.ownerId().getValue(),
                null
        );
    }

    /// Converts an [UpdateRestaurantUseCase.Output] to a [RestaurantViewModel].
    ///
    /// @param output the output from the update use case
    /// @return the corresponding [RestaurantViewModel]
    public RestaurantViewModel present(UpdateRestaurantUseCase.Output output) {
        return new RestaurantViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.address().getValue(),
                output.cuisineType().getValue(),
                output.openingHours().getValue(),
                output.ownerId().getValue(),
                null
        );
    }

    /// Converts a [FindRestaurantByIdUseCase.Output] to a [RestaurantViewModel].
    ///
    /// @param output the output from the find-by-id use case (includes owner name)
    /// @return the corresponding [RestaurantViewModel]
    public RestaurantViewModel present(FindRestaurantByIdUseCase.Output output) {
        return new RestaurantViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.address().getValue(),
                output.cuisineType().getValue(),
                output.openingHours().getValue(),
                output.ownerId().getValue(),
                output.ownerName()
        );
    }

    /// Converts a [FindAllRestaurantsUseCase.Output] to a [RestaurantViewModel].
    ///
    /// @param output the output from the find-all use case (includes owner name)
    /// @return the corresponding [RestaurantViewModel]
    public RestaurantViewModel present(FindAllRestaurantsUseCase.Output output) {
        return new RestaurantViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.address().getValue(),
                output.cuisineType().getValue(),
                output.openingHours().getValue(),
                output.ownerId().getValue(),
                output.ownerName()
        );
    }

    /// Converts a [TransferRestaurantOwnershipUseCase.Output] to a [TransferOwnershipViewModel].
    ///
    /// @param output the output from the transfer-ownership use case
    /// @return the corresponding [TransferOwnershipViewModel]
    public TransferOwnershipViewModel present(TransferRestaurantOwnershipUseCase.Output output) {
        return new TransferOwnershipViewModel(output.ownerId().getValue());
    }
}
