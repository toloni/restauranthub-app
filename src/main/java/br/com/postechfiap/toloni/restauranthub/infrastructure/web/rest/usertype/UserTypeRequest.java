package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.usertype;

import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.CreateUserTypeUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.UpdateUserTypeUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Represents the HTTP request body for [UserType] operations.
///
/// @param name        the name of the user type
/// @param description the description of the user type
/// @param role        the [UserRole] of the user type
public record UserTypeRequest(String name, String description, UserRole role) {

    /// Converts this request to a [CreateUserTypeUseCase.Input].
    ///
    /// @return a new [CreateUserTypeUseCase.Input] with the data from this request
    public CreateUserTypeUseCase.Input toCreateInput() {
        return new CreateUserTypeUseCase.Input(name, description, role);
    }

    /// Converts this request to an [UpdateUserTypeUseCase.Input].
    ///
    /// @param id the [UserTypeId] of the user type to update
    /// @return a new [UpdateUserTypeUseCase.Input] with the data from this request
    public UpdateUserTypeUseCase.Input toUpdateInput(UserTypeId id) {
        return new UpdateUserTypeUseCase.Input(id, name, description, role);
    }
}
