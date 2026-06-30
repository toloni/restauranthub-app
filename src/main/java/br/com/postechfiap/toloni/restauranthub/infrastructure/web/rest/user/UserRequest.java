package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.user;

import br.com.postechfiap.toloni.restauranthub.application.usecases.user.CreateUserUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.UpdateUserUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Represents the HTTP request body for [User] operations.
///
/// @param name       the name of the user
/// @param email      the email of the user
/// @param password   the password of the user
/// @param userTypeId the unique identifier of the user type
public record UserRequest(String name, String email, String password, String userTypeId) {

    /// Converts this request to a [CreateUserUseCase.Input].
    ///
    /// @return a new [CreateUserUseCase.Input] with the data from this request
    public CreateUserUseCase.Input toCreateInput() {
        return new CreateUserUseCase.Input(
                name,
                email,
                password,
                userTypeId == null ? null : UserTypeId.of(userTypeId)
        );
    }

    /// Converts this request to an [UpdateUserUseCase.Input].
    ///
    /// @param id the [UserId] of the user to update
    /// @return a new [UpdateUserUseCase.Input] with the data from this request
    public UpdateUserUseCase.Input toUpdateInput(UserId id) {
        return new UpdateUserUseCase.Input(
                id,
                name,
                email,
                password,
                userTypeId == null ? null : UserTypeId.of(userTypeId)
        );
    }
}
