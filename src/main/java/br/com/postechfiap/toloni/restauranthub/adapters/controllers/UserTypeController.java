package br.com.postechfiap.toloni.restauranthub.adapters.controllers;

import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;

/// Adapter that bridges any entry point to the [UserType] use cases.
///
/// Receives input from the presentation layer — regardless of protocol
/// (HTTP, gRPC, CLI, messaging) — delegates to the appropriate use case,
/// and returns the result.
public class UserTypeController {

    private final CreateUserTypeUseCase createUserTypeUseCase;
    private final UpdateUserTypeUseCase updateUserTypeUseCase;
    private final DeleteUserTypeUseCase deleteUserTypeUseCase;
    private final FindUserTypeByIdUseCase findUserTypeByIdUseCase;
    private final FindAllUserTypesUseCase findAllUserTypesUseCase;

    /// @param createUserTypeUseCase   the use case for creating a [UserType]
    /// @param updateUserTypeUseCase   the use case for updating a [UserType]
    /// @param deleteUserTypeUseCase   the use case for deleting a [UserType]
    /// @param findUserTypeByIdUseCase the use case for finding a [UserType] by its identifier
    /// @param findAllUserTypesUseCase the use case for retrieving all [UserType] instances
    public UserTypeController(
            CreateUserTypeUseCase createUserTypeUseCase,
            UpdateUserTypeUseCase updateUserTypeUseCase,
            DeleteUserTypeUseCase deleteUserTypeUseCase,
            FindUserTypeByIdUseCase findUserTypeByIdUseCase,
            FindAllUserTypesUseCase findAllUserTypesUseCase) {
        this.createUserTypeUseCase = createUserTypeUseCase;
        this.updateUserTypeUseCase = updateUserTypeUseCase;
        this.deleteUserTypeUseCase = deleteUserTypeUseCase;
        this.findUserTypeByIdUseCase = findUserTypeByIdUseCase;
        this.findAllUserTypesUseCase = findAllUserTypesUseCase;
    }

    /// Creates a new [UserType].
    ///
    /// @param input the [CreateUserTypeUseCase.Input] data
    /// @return the [CreateUserTypeUseCase.Output] containing the created [UserType] data
    /// @throws AlreadyExistsException if a [UserType] with the given role already exists
    public CreateUserTypeUseCase.Output create(CreateUserTypeUseCase.Input input) {
        return createUserTypeUseCase.execute(input);
    }

    /// Updates an existing [UserType].
    ///
    /// @param input the [UpdateUserTypeUseCase.Input] data
    /// @return the [UpdateUserTypeUseCase.Output] containing the updated [UserType] data
    /// @throws NotFoundException      if no [UserType] is found with the given [UserTypeId]
    /// @throws AlreadyExistsException if another [UserType] with the given role already exists
    public UpdateUserTypeUseCase.Output update(UpdateUserTypeUseCase.Input input) {
        return updateUserTypeUseCase.execute(input);
    }

    /// Deletes a [UserType] by its identifier.
    ///
    /// This operation is idempotent — if no [UserType] is found, completes silently.
    ///
    /// @param input the [DeleteUserTypeUseCase.Input] data
    public void delete(DeleteUserTypeUseCase.Input input) {
        deleteUserTypeUseCase.execute(input);
    }

    /// Finds a [UserType] by its identifier.
    ///
    /// @param input the [FindUserTypeByIdUseCase.Input] data
    /// @return the [FindUserTypeByIdUseCase.Output] containing the found [UserType] data
    /// @throws NotFoundException if no [UserType] is found with the given [UserTypeId]
    public FindUserTypeByIdUseCase.Output findById(FindUserTypeByIdUseCase.Input input) {
        return findUserTypeByIdUseCase.execute(input);
    }

    /// Retrieves a paginated list of [UserType] instances.
    ///
    /// @param input the [FindAllUserTypesUseCase.Input] carrying the [PageRequest]
    /// @return a [Page] of [FindAllUserTypesUseCase.Output]
    public Page<FindAllUserTypesUseCase.Output> findAll(FindAllUserTypesUseCase.Input input) {
        return findAllUserTypesUseCase.execute(input);
    }
}
