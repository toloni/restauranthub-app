package br.com.postechfiap.toloni.restauranthub.application.usecases.user;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Use case responsible for retrieving a [User] by its identifier.
///
/// ## Flow
/// 1. Searches for a [User] with the given [UserId]
/// 2. Returns the [User] data if found
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [User] is found with the given [UserId]
public class FindUserByIdUseCase {

    private final UserGateway userGateway;

    /// @param userGateway the gateway for [User] persistence operations
    public FindUserByIdUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    /// Input data required to find a [User].
    ///
    /// @param id the [UserId] of the user to find
    public record Input(UserId id) {
    }

    /// Output data returned for the found [User].
    ///
    /// @param id         the [UserId] of the user
    /// @param name       the name of the user
    /// @param email      the email of the user
    /// @param userTypeId the [UserTypeId] of the user
    public record Output(UserId id, String name, String email, UserTypeId userTypeId, String userTypeName) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to find a [User]
    /// @return the [Output] containing the found [User] data
    /// @throws NotFoundException if no [User] is found with the given [UserId]
    public Output execute(Input input) {
        return userGateway.findByIdWithUserTypeName(input.id())
                .map(enriched -> new Output(
                        enriched.user().getId(),
                        enriched.user().getName().getValue(),
                        enriched.user().getEmail().getValue(),
                        enriched.user().getUserTypeId(),
                        enriched.userTypeName()
                ))
                .orElseThrow(() -> new NotFoundException("User", input.id().getValue().toString()));
    }
}