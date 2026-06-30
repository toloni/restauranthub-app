package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;

/// Use case responsible for updating an existing [Restaurant].
///
/// Orchestrates the domain rules and persistence contract to ensure
/// a [Restaurant] is updated only when all invariants are satisfied.
///
/// ## Flow
/// 1. Finds the [Restaurant] by its identifier
/// 2. Ensures the requester is the owner of the [Restaurant]
/// 3. Checks whether another [Restaurant] with the given name already exists
/// 4. Updates the [Restaurant] entity with the provided attributes
/// 5. Persists and returns the updated [Restaurant]
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [Restaurant] is found with the given [RestaurantId]
/// - Throws [DomainException] if the requester is not the owner of the [Restaurant]
/// - Throws [AlreadyExistsException] if another [Restaurant] with the given name already exists
public class UpdateRestaurantUseCase {

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;
    private final AuthorizationService authorizationService;

    /// @param restaurantGateway the gateway for [Restaurant] persistence operations
    /// @param userGateway       the gateway for [User] persistence operations
    public UpdateRestaurantUseCase(
            RestaurantGateway restaurantGateway,
            UserGateway userGateway,
            AuthorizationService authorizationService) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
        this.authorizationService = authorizationService;
    }

    /// Input data required to update a [Restaurant].
    ///
    /// @param id           the [RestaurantId] of the restaurant to update
    /// @param name         the new name, or `null` to keep the current value
    /// @param address      the new address, or `null` to keep the current value
    /// @param cuisineType  the new cuisine type, or `null` to keep the current value
    /// @param openingHours the new opening hours, or `null` to keep the current value
    /// @param ownerId      the [UserId] of the requester, used to validate ownership
    public record Input(RestaurantId id, String name, String address, String cuisineType, String openingHours,
                        UserId ownerId) {
    }

    /// Output data returned after updating a [Restaurant].
    ///
    /// @param id           the [RestaurantId] of the updated restaurant
    /// @param name         the name of the updated restaurant
    /// @param address      the address of the updated restaurant
    /// @param cuisineType  the cuisine type of the updated restaurant
    /// @param openingHours the opening hours of the updated restaurant
    /// @param ownerId      the [UserId] of the owner of the updated restaurant
    public record Output(RestaurantId id, String name, String address, String cuisineType, String openingHours,
                         UserId ownerId) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to update a [Restaurant]
    /// @return the [Output] containing the updated [Restaurant] data
    /// @throws NotFoundException      if no [Restaurant] is found with the given [RestaurantId]
    /// @throws DomainException        if the requester is not the owner of the [Restaurant]
    /// @throws AlreadyExistsException if another [Restaurant] with the given name already exists
    public Output execute(Input input) {

        var restaurant = restaurantGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("Restaurant", input.id().getValue().toString()));

        authorizationService.validateRestaurantOwnership(restaurant, input.ownerId());

        if (!userGateway.existsById(input.ownerId()))
            throw new NotFoundException("User", input.ownerId().getValue().toString());

        if (input.name() != null) {
            var name = RestaurantName.of(input.name());
            if (restaurantGateway.existsByNameAndIdNot(name, input.id()))
                throw new AlreadyExistsException("Restaurant", name.getValue());
        }

        restaurant.update(
                input.name() != null ? RestaurantName.of(input.name()) : null,
                input.address() != null ? RestaurantAddress.of(input.address()) : null,
                input.cuisineType() != null ? RestaurantCuisineType.of(input.cuisineType()) : null,
                input.openingHours() != null ? RestaurantOpeningHours.of(input.openingHours()) : null,
                input.ownerId()
        );

        var saved = restaurantGateway.save(restaurant);

        return new Output(
                saved.getId(),
                saved.getName().getValue(),
                saved.getAddress().getValue(),
                saved.getCuisineType().getValue(),
                saved.getOpeningHours().getValue(),
                saved.getOwnerId()
        );
    }
}
