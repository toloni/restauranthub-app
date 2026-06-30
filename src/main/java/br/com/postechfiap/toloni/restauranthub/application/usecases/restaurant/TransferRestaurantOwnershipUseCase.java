package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Use case responsible for updating the owner of a [Restaurant].
///
/// Access validation and owner eligibility rules are delegated to the
/// [AuthorizationService].
///
/// ## Flow
/// 1. Validates that the requester can assign the new owner
/// 2. Finds the [Restaurant] by its identifier
/// 3. Updates the restaurant owner
/// 4. Persists the changes
/// 5. Returns the updated owner identifier
///
/// ## Exceptions
/// - Throws [UnauthorizedException] when the requester is not allowed
///   to assign the new owner
/// - Throws [NotFoundException] if no [Restaurant] is found with the
///   given [RestaurantId]
public class TransferRestaurantOwnershipUseCase {

    private final RestaurantGateway restaurantGateway;
    private final AuthorizationService authorizationService;

    /// Creates a new instance of the use case.
    ///
    /// @param restaurantGateway    the gateway responsible for [Restaurant] persistence operations
    /// @param authorizationService the service responsible for authorization and ownership validation
    public TransferRestaurantOwnershipUseCase(RestaurantGateway restaurantGateway,
                                              AuthorizationService authorizationService) {
        this.restaurantGateway = restaurantGateway;
        this.authorizationService = authorizationService;
    }

    /// Input data required to update the owner of a [Restaurant].
    ///
    /// @param restaurantId the identifier of the restaurant
    /// @param newOwnerId   the identifier of the new owner
    /// @param requesterId  the identifier of the authenticated user performing the operation
    public record Input(RestaurantId restaurantId, UserId requesterId, UserId newOwnerId) {
    }

    /// Result of the ownership update operation.
    ///
    /// @param ownerId the identifier of the assigned owner
    public record Output(UserId ownerId) {
    }

    /// Executes the ownership update operation.
    ///
    /// @param input the required operation data
    /// @return the updated owner information
    /// @throws UnauthorizedException if the requester is not authorized to assign the new owner
    /// @throws NotFoundException     if the restaurant does not exist
    public Output execute(Input input) {

        authorizationService.validateRestaurantTransferOwnership(input.requesterId, input.newOwnerId);

        var restaurant = restaurantGateway.findById(input.restaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurant", input.restaurantId().getValue().toString()));

        restaurant.update(null, null, null, null, input.newOwnerId());

        var saved = restaurantGateway.save(restaurant);

        return new Output(saved.getOwnerId());
    }
}
