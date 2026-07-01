package br.com.postechfiap.toloni.restauranthub.application.usecases.user;

import br.com.postechfiap.toloni.restauranthub.application.gateways.UserGateway;
import br.com.postechfiap.toloni.restauranthub.application.gateways.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Use case responsible for creating a new [User].
///
/// Orchestrates the domain rules and persistence contract to ensure
/// a [User] is created only when all invariants are satisfied.
///
/// ## Flow
/// 1. Checks whether a [User] with the given email already exists
/// 2. Creates the [User] entity with the provided attributes
/// 3. Persists and returns the created [User]
///
/// ## Exceptions
/// - Throws [AlreadyExistsException] if a [User] with the given email already exists
public class CreateUserUseCase {

    private final UserGateway userGateway;
    private final UserTypeGateway userTypeGateway;

    /// @param userGateway the gateway for [User] persistence operations
    public CreateUserUseCase(UserGateway userGateway, UserTypeGateway userTypeGateway) {
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
    }

    /// Input data required to create a [User].
    ///
    /// @param name       the name of the user
    /// @param email      the email of the user
    /// @param password   the password of the user
    /// @param userTypeId the [UserTypeId] referencing the type of the user
    public record Input(String name, String email, String password, UserTypeId userTypeId) {
    }

    /// Output data returned after creating a [User].
    ///
    /// @param id         the generated [UserId]
    /// @param name       the name of the created user
    /// @param email      the email of the created user
    /// @param userTypeId the [UserTypeId] of the created user
    public record Output(UserId id, UserName name, UserEmail email, UserTypeId userTypeId) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to create a [User]
    /// @return the [Output] containing the created [User] data
    /// @throws AlreadyExistsException if a [User] with the given email already exists
    public Output execute(Input input) {
        var email = UserEmail.of(input.email());

        if (userGateway.existsByEmail(email))
            throw new AlreadyExistsException("Email", email.getValue());

        if (input.userTypeId() == null)
            throw new DomainException("User must have a type.");

        if (!userTypeGateway.existsById(input.userTypeId()))
            throw new NotFoundException("UserType", input.userTypeId().getValue().toString());

        var user = new User(
                UserId.generate(),
                UserName.of(input.name()),
                email,
                UserPassword.of(input.password()),
                input.userTypeId()
        );

        var saved = userGateway.save(user);

        return new Output(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getUserTypeId()
        );
    }
}
