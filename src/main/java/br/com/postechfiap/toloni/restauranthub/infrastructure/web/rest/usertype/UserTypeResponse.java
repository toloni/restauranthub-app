package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.usertype;

import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.CreateUserTypeUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.FindAllUserTypesUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.FindUserTypeByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.UpdateUserTypeUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;

import java.util.UUID;

public record UserTypeResponse(UUID id, String name, String description, UserRole role) {

    public static UserTypeResponse from(CreateUserTypeUseCase.Output output) {
        return new UserTypeResponse(output.id().getValue(), output.name(), output.description(), output.role());
    }

    public static UserTypeResponse from(UpdateUserTypeUseCase.Output output) {
        return new UserTypeResponse(output.id().getValue(), output.name(), output.description(), output.role());
    }

    public static UserTypeResponse from(FindUserTypeByIdUseCase.Output output) {
        return new UserTypeResponse(output.id().getValue(), output.name(), output.description(), output.role());
    }

    public static UserTypeResponse from(FindAllUserTypesUseCase.Output output) {
        return new UserTypeResponse(output.id().getValue(), output.name(), output.description(), output.role());
    }
}
