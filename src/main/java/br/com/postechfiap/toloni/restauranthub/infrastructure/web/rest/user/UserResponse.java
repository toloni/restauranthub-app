package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.user;

import br.com.postechfiap.toloni.restauranthub.application.usecases.user.CreateUserUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.FindAllUsersUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.FindUserByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.UpdateUserUseCase;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

/// Represents the HTTP response body for [User] operations.
///
/// @param id           the unique identifier of the user
/// @param name         the name of the user
/// @param email        the email of the user
/// @param userTypeId   the unique identifier of the user type
/// @param userTypeName the name of the user type
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(UUID id, String name, String email, UUID userTypeId, String userTypeName) {

    public static UserResponse from(CreateUserUseCase.Output output) {
        return new UserResponse(
                output.id().getValue(),
                output.name(),
                output.email(),
                output.userTypeId().getValue(),
                null
        );
    }

    public static UserResponse from(UpdateUserUseCase.Output output) {
        return new UserResponse(
                output.id().getValue(),
                output.name(),
                output.email(),
                output.userTypeId().getValue(),
                null
        );
    }

    public static UserResponse from(FindUserByIdUseCase.Output output) {
        return new UserResponse(
                output.id().getValue(),
                output.name(),
                output.email(),
                output.userTypeId().getValue(),
                output.userTypeName()
        );
    }

    public static UserResponse from(FindAllUsersUseCase.Output output) {
        return new UserResponse(
                output.id().getValue(),
                output.name(),
                output.email(),
                output.userTypeId().getValue(),
                output.userTypeName()
        );
    }
}