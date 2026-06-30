package br.com.postechfiap.toloni.restauranthub.adapters.controllers;

import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;

/// Adapter that bridges any entry point to the [Restaurant] use cases.
///
/// Receives input from the presentation layer — regardless of protocol
/// (HTTP, gRPC, CLI, messaging) — delegates to the appropriate use case,
/// and returns the result.
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;
    private final FindRestaurantByIdUseCase findRestaurantByIdUseCase;
    private final FindAllRestaurantsUseCase findAllRestaurantsUseCase;
    private final TransferRestaurantOwnershipUseCase transferRestaurantOwnershipUseCase;

    /// @param createRestaurantUseCase   the use case for creating a [Restaurant]
    /// @param updateRestaurantUseCase   the use case for updating a [Restaurant]
    /// @param deleteRestaurantUseCase   the use case for deleting a [Restaurant]
    /// @param findRestaurantByIdUseCase the use case for finding a [Restaurant] by its identifier
    /// @param findAllRestaurantsUseCase the use case for retrieving all [Restaurant] instances
    public RestaurantController(
            CreateRestaurantUseCase createRestaurantUseCase,
            UpdateRestaurantUseCase updateRestaurantUseCase,
            DeleteRestaurantUseCase deleteRestaurantUseCase,
            FindRestaurantByIdUseCase findRestaurantByIdUseCase,
            FindAllRestaurantsUseCase findAllRestaurantsUseCase,
            TransferRestaurantOwnershipUseCase transferRestaurantOwnershipUseCase) {
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.updateRestaurantUseCase = updateRestaurantUseCase;
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
        this.findRestaurantByIdUseCase = findRestaurantByIdUseCase;
        this.findAllRestaurantsUseCase = findAllRestaurantsUseCase;
        this.transferRestaurantOwnershipUseCase = transferRestaurantOwnershipUseCase;
    }

    /// Creates a new [Restaurant].
    ///
    /// @param input the [CreateRestaurantUseCase.Input] data
    /// @return the [CreateRestaurantUseCase.Output] containing the created [Restaurant] data
    /// @throws AlreadyExistsException if a [Restaurant] with the given name already exists
    /// @throws NotFoundException      if no [User] is found with the given [UserId]
    public CreateRestaurantUseCase.Output create(CreateRestaurantUseCase.Input input) {
        return createRestaurantUseCase.execute(input);
    }

    /// Updates an existing [Restaurant].
    ///
    /// @param input the [UpdateRestaurantUseCase.Input] data
    /// @return the [UpdateRestaurantUseCase.Output] containing the updated [Restaurant] data
    /// @throws NotFoundException      if no [Restaurant] or [User] is found
    /// @throws AlreadyExistsException if another [Restaurant] with the given name already exists
    public UpdateRestaurantUseCase.Output update(UpdateRestaurantUseCase.Input input) {
        return updateRestaurantUseCase.execute(input);
    }

    /// Deletes a [Restaurant] by its identifier.
    ///
    /// This operation is idempotent — if no [Restaurant] is found, completes silently.
    ///
    /// @param input the [DeleteRestaurantUseCase.Input] data
    public void delete(DeleteRestaurantUseCase.Input input) {
        deleteRestaurantUseCase.execute(input);
    }

    /// Finds a [Restaurant] by its identifier.
    ///
    /// @param input the [FindRestaurantByIdUseCase.Input] data
    /// @return the [FindRestaurantByIdUseCase.Output] containing the found [Restaurant] data
    /// @throws NotFoundException if no [Restaurant] is found with the given [RestaurantId]
    public FindRestaurantByIdUseCase.Output findById(FindRestaurantByIdUseCase.Input input) {
        return findRestaurantByIdUseCase.execute(input);
    }

    /// Retrieves a paginated list of [Restaurant] instances.
    ///
    /// @param input the [FindAllRestaurantsUseCase.Input] carrying the [PageRequest]
    /// @return a [Page] of [FindAllRestaurantsUseCase.Output]
    public Page<FindAllRestaurantsUseCase.Output> findAll(FindAllRestaurantsUseCase.Input input) {
        return findAllRestaurantsUseCase.execute(input);
    }

    /// Transfers the ownership of a [Restaurant] to a new owner.
    ///
    /// This operation is restricted to users with the [UserRole#ADMIN] role.
    /// The new owner must have the [UserRole#RESTAURANT_OWNER] role.
    ///
    /// @param input the [TransferRestaurantOwnershipUseCase.Input] data
    /// @return the [TransferRestaurantOwnershipUseCase.Output] containing the new owner identifier
    /// @throws UnauthorizedException if the requester is not an admin or the new owner is not a restaurant owner
    /// @throws NotFoundException     if no [Restaurant] or [User] is found
    public TransferRestaurantOwnershipUseCase.Output transferOwnership(
            TransferRestaurantOwnershipUseCase.Input input) {
        return transferRestaurantOwnershipUseCase.execute(input);
    }
}
