package br.com.postechfiap.toloni.restauranthub.adapters.controllers;

import br.com.postechfiap.toloni.restauranthub.application.usecases.user.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;

/// Adapter that bridges any entry point to the [User] use cases.
///
/// Receives input from the presentation layer — regardless of protocol
/// (HTTP, gRPC, CLI, messaging) — delegates to the appropriate use case,
/// and returns the result.
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;

    /// @param createUserUseCase   the use case for creating a [User]
    /// @param updateUserUseCase   the use case for updating a [User]
    /// @param deleteUserUseCase   the use case for deleting a [User]
    /// @param findUserByIdUseCase the use case for finding a [User] by its identifier
    /// @param findAllUsersUseCase the use case for retrieving all [User] instances
    public UserController(
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase,
            FindUserByIdUseCase findUserByIdUseCase,
            FindAllUsersUseCase findAllUsersUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.findAllUsersUseCase = findAllUsersUseCase;
    }

    /// Creates a new [User].
    ///
    /// @param input the [CreateUserUseCase.Input] data
    /// @return the [CreateUserUseCase.Output] containing the created [User] data
    /// @throws AlreadyExistsException if a [User] with the given email already exists
    public CreateUserUseCase.Output create(CreateUserUseCase.Input input) {
        return createUserUseCase.execute(input);
    }

    /// Updates an existing [User].
    ///
    /// @param input the [UpdateUserUseCase.Input] data
    /// @return the [UpdateUserUseCase.Output] containing the updated [User] data
    /// @throws NotFoundException      if no [User] is found with the given [UserId]
    /// @throws AlreadyExistsException if another [User] with the given email already exists
    public UpdateUserUseCase.Output update(UpdateUserUseCase.Input input) {
        return updateUserUseCase.execute(input);
    }

    /// Deletes a [User] by its identifier.
    ///
    /// This operation is idempotent — if no [User] is found, completes silently.
    ///
    /// @param input the [DeleteUserUseCase.Input] data
    public void delete(DeleteUserUseCase.Input input) {
        deleteUserUseCase.execute(input);
    }

    /// Finds a [User] by its identifier.
    ///
    /// @param input the [FindUserByIdUseCase.Input] data
    /// @return the [FindUserByIdUseCase.Output] containing the found [User] data
    /// @throws NotFoundException if no [User] is found with the given [UserId]
    public FindUserByIdUseCase.Output findById(FindUserByIdUseCase.Input input) {
        return findUserByIdUseCase.execute(input);
    }

    /// Retrieves a paginated list of [User] instances.
    ///
    /// @param input the [FindAllUsersUseCase.Input] carrying the [PageRequest]
    /// @return a [Page] of [FindAllUsersUseCase.Output]
    public Page<FindAllUsersUseCase.Output> findAll(FindAllUsersUseCase.Input input) {
        return findAllUsersUseCase.execute(input);
    }
}
