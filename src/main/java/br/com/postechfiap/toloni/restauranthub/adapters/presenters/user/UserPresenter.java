package br.com.postechfiap.toloni.restauranthub.adapters.presenters.user;

import br.com.postechfiap.toloni.restauranthub.application.usecases.user.CreateUserUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.FindAllUsersUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.FindUserByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.UpdateUserUseCase;

/// Presenter that converts [User] use case outputs into [UserViewModel].
///
/// Decouples the adapter layer from use case output types, producing
/// a uniform view model suitable for any presentation layer.
public class UserPresenter {

    /// Converts a [CreateUserUseCase.Output] to a [UserViewModel].
    ///
    /// @param output the output from the create use case
    /// @return the corresponding [UserViewModel]
    public UserViewModel present(CreateUserUseCase.Output output) {
        return new UserViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.email().getValue(),
                output.userTypeId().getValue(),
                null
        );
    }

    /// Converts an [UpdateUserUseCase.Output] to a [UserViewModel].
    ///
    /// @param output the output from the update use case
    /// @return the corresponding [UserViewModel]
    public UserViewModel present(UpdateUserUseCase.Output output) {
        return new UserViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.email().getValue(),
                output.userTypeId().getValue(),
                null
        );
    }

    /// Converts a [FindUserByIdUseCase.Output] to a [UserViewModel].
    ///
    /// @param output the output from the find-by-id use case (includes user type name)
    /// @return the corresponding [UserViewModel]
    public UserViewModel present(FindUserByIdUseCase.Output output) {
        return new UserViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.email().getValue(),
                output.userTypeId().getValue(),
                output.userTypeName()
        );
    }

    /// Converts a [FindAllUsersUseCase.Output] to a [UserViewModel].
    ///
    /// @param output the output from the find-all use case (includes user type name)
    /// @return the corresponding [UserViewModel]
    public UserViewModel present(FindAllUsersUseCase.Output output) {
        return new UserViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.email().getValue(),
                output.userTypeId().getValue(),
                output.userTypeName()
        );
    }
}
