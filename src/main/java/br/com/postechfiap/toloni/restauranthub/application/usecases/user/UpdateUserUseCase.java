package br.com.postechfiap.toloni.restauranthub.application.usecases.user;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Use case responsible for updating an existing [User].
///
/// Orchestrates the domain rules and persistence contract to ensure
/// a [User] is updated only when all invariants are satisfied.
///
/// ## Flow
/// 1. Checks whether a [User] with the given [UserId] exists
/// 2. Checks whether another [User] with the given email already exists
/// 3. Updates the [User] entity with the provided attributes
/// 4. Persists and returns the updated [User]
///
/// ## Exceptions
/// - Throws [NotFoundException] if no [User] is found with the given [UserId]
/// - Throws [AlreadyExistsException] if another [User] with the given email already exists
public class UpdateUserUseCase {

    private final UserGateway userGateway;
    private final UserTypeGateway userTypeGateway;

    public UpdateUserUseCase(UserGateway userGateway, UserTypeGateway userTypeGateway) {
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
    }

    /// Input data required to update a [User].
    ///
    /// @param id         the [UserId] of the user to update
    /// @param name       the new name, or `null` to keep the current value
    /// @param email      the new email, or `null` to keep the current value
    /// @param password   the new password, or `null` to keep the current value
    /// @param userTypeId the new [UserTypeId], or `null` to keep the current value
    public record Input(UserId id, String name, String email, String password, UserTypeId userTypeId) {
    }

    /// Output data returned after updating a [User].
    ///
    /// @param id         the [UserId] of the updated user
    /// @param name       the name of the updated user
    /// @param email      the email of the updated user
    /// @param userTypeId the [UserTypeId] of the updated user
    public record Output(UserId id, String name, String email, UserTypeId userTypeId) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data required to update a [User]
    /// @return the [Output] containing the updated [User] data
    /// @throws NotFoundException      if no [User] is found with the given [UserId]
    /// @throws AlreadyExistsException if another [User] with the given email already exists
    public Output execute(Input input) {
        var user = userGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("User", input.id().getValue().toString()));

        if (input.email() != null) {
            var email = UserEmail.of(input.email());
            if (userGateway.existsByEmailAndIdNot(email, input.id()))
                throw new AlreadyExistsException("Email", email.getValue());
        }

        if (input.userTypeId() != null && !userTypeGateway.existsById(input.userTypeId()))
            throw new NotFoundException("UserType", input.userTypeId().getValue().toString());

        user.update(
                input.name() != null ? UserName.of(input.name()) : null,
                input.email() != null ? UserEmail.of(input.email()) : null,
                input.password() != null ? UserPassword.of(input.password()) : null,
                input.userTypeId()
        );

        var saved = userGateway.save(user);

        return new Output(
                saved.getId(),
                saved.getName().getValue(),
                saved.getEmail().getValue(),
                saved.getUserTypeId()
        );
    }
}
