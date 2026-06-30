package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;

/// Use case responsible for creating a new [Restaurant].
///
/// Orchestrates the domain rules and persistence contract to ensure
/// a [Restaurant] is created only when all invariants are satisfied.
///
/// ## Flow
/// 1. Checks whether a [Restaurant] with the given name already exists
/// 2. Finds the owner [User] by its identifier
/// 3. Finds the [UserType] of the owner to validate the role
/// 4. Ensures the owner has the [UserRole#RESTAURANT_OWNER] role
/// 5. Creates the [Restaurant] entity with the provided attributes
/// 6. Persists and returns the created [Restaurant]
///
/// ## Exceptions
/// - Throws [AlreadyExistsException] if a [Restaurant] with the given name already exists
/// - Throws [NotFoundException] if no [User] is found with the given [UserId]
/// - Throws [NotFoundException] if no [UserType] is found for the owner
/// - Throws [DomainException] if the owner does not have the [UserRole#RESTAURANT_OWNER] role
public class CreateRestaurantUseCase {

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;
    private final UserTypeGateway userTypeGateway;
    private final AuthorizationService authorizationService;

    /// @param restaurantGateway the gateway for [Restaurant] persistence operations
    /// @param userGateway       the gateway for [User] persistence operations
    /// @param userTypeGateway   the gateway for [UserType] persistence operations
    public CreateRestaurantUseCase(RestaurantGateway restaurantGateway,
                                   UserGateway userGateway,
                                   UserTypeGateway userTypeGateway,
                                   AuthorizationService authorizationService) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
        this.authorizationService = authorizationService;
    }

    /// Input data required to create a [Restaurant].
    ///
    /// @param name         the name of the restaurant
    /// @param address      the address of the restaurant
    /// @param cuisineType  the cuisine type of the restaurant
    /// @param openingHours the opening hours of the restaurant
    /// @param ownerId      the [UserId] referencing the owner of the restaurant
    public record Input(String name, String address, String cuisineType, String openingHours, UserId ownerId) {
    }

    /// Output data returned after creating a [Restaurant].
    ///
    /// @param id           the generated [RestaurantId]
    /// @param name         the name of the created restaurant
    /// @param address      the address of the created restaurant
    /// @param cuisineType  the cuisine type of the created restaurant
    /// @param openingHours the opening hours of the created restaurant
    /// @param ownerId      the [UserId] of the owner of the created restaurant
    public record Output(RestaurantId id, String name, String address, String cuisineType, String openingHours,
                         UserId ownerId) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to create a [Restaurant]
    /// @return the [Output] containing the created [Restaurant] data
    /// @throws AlreadyExistsException if a [Restaurant] with the given name already exists
    /// @throws NotFoundException      if no [User] or [UserType] is found
    /// @throws DomainException        if the owner does not have the [UserRole#RESTAURANT_OWNER] role
    public Output execute(Input input) {
        var name = RestaurantName.of(input.name());

        if (restaurantGateway.existsByName(name))
            throw new AlreadyExistsException("Restaurant", name.getValue());

        authorizationService.validateRestaurantOwnerRole(input.ownerId());

        var restaurant = new Restaurant(
                RestaurantId.generate(),
                name,
                RestaurantAddress.of(input.address()),
                RestaurantCuisineType.of(input.cuisineType()),
                RestaurantOpeningHours.of(input.openingHours()),
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
