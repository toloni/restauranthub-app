package br.com.postechfiap.toloni.restauranthub.adapters.presenters.usertype;

import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.CreateUserTypeUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.FindAllUserTypesUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.FindUserTypeByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.UpdateUserTypeUseCase;

/// Presenter that converts [UserType] use case outputs into [UserTypeViewModel].
///
/// Decouples the adapter layer from use case output types, producing
/// a uniform view model suitable for any presentation layer.
public class UserTypePresenter {

    /// Converts a [CreateUserTypeUseCase.Output] to a [UserTypeViewModel].
    ///
    /// @param output the output from the create use case
    /// @return the corresponding [UserTypeViewModel]
    public UserTypeViewModel present(CreateUserTypeUseCase.Output output) {
        return new UserTypeViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.description().getValue(),
                output.role()
        );
    }

    /// Converts an [UpdateUserTypeUseCase.Output] to a [UserTypeViewModel].
    ///
    /// @param output the output from the update use case
    /// @return the corresponding [UserTypeViewModel]
    public UserTypeViewModel present(UpdateUserTypeUseCase.Output output) {
        return new UserTypeViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.description().getValue(),
                output.role()
        );
    }

    /// Converts a [FindUserTypeByIdUseCase.Output] to a [UserTypeViewModel].
    ///
    /// @param output the output from the find-by-id use case
    /// @return the corresponding [UserTypeViewModel]
    public UserTypeViewModel present(FindUserTypeByIdUseCase.Output output) {
        return new UserTypeViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.description().getValue(),
                output.role()
        );
    }

    /// Converts a [FindAllUserTypesUseCase.Output] to a [UserTypeViewModel].
    ///
    /// @param output the output from the find-all use case
    /// @return the corresponding [UserTypeViewModel]
    public UserTypeViewModel present(FindAllUserTypesUseCase.Output output) {
        return new UserTypeViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.description().getValue(),
                output.role()
        );
    }
}
