package br.com.postechfiap.toloni.restauranthub.adapters.controllers;

import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;

/// Adapter that bridges any entry point to the [MenuItem] use cases.
///
/// Receives input from the presentation layer — regardless of protocol
/// (HTTP, gRPC, CLI, messaging) — delegates to the appropriate use case,
/// and returns the result.
public class MenuItemController {

    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;
    private final FindMenuItemByIdUseCase findMenuItemByIdUseCase;
    private final FindAllMenuItemsUseCase findAllMenuItemsUseCase;

    /// @param createMenuItemUseCase   the use case for creating a [MenuItem]
    /// @param updateMenuItemUseCase   the use case for updating a [MenuItem]
    /// @param deleteMenuItemUseCase   the use case for deleting a [MenuItem]
    /// @param findMenuItemByIdUseCase the use case for finding a [MenuItem] by its identifier
    /// @param findAllMenuItemsUseCase the use case for retrieving all [MenuItem] instances
    public MenuItemController(
            CreateMenuItemUseCase createMenuItemUseCase,
            UpdateMenuItemUseCase updateMenuItemUseCase,
            DeleteMenuItemUseCase deleteMenuItemUseCase,
            FindMenuItemByIdUseCase findMenuItemByIdUseCase,
            FindAllMenuItemsUseCase findAllMenuItemsUseCase) {
        this.createMenuItemUseCase = createMenuItemUseCase;
        this.updateMenuItemUseCase = updateMenuItemUseCase;
        this.deleteMenuItemUseCase = deleteMenuItemUseCase;
        this.findMenuItemByIdUseCase = findMenuItemByIdUseCase;
        this.findAllMenuItemsUseCase = findAllMenuItemsUseCase;
    }

    /// Creates a new [MenuItem].
    ///
    /// @param input the [CreateMenuItemUseCase.Input] data
    /// @return the [CreateMenuItemUseCase.Output] containing the created [MenuItem] data
    /// @throws NotFoundException      if no [Restaurant] is found with the given [RestaurantId]
    /// @throws UnauthorizedException  if the requester is not the owner of the [Restaurant]
    /// @throws AlreadyExistsException if a [MenuItem] with the given name already exists in the [Restaurant]
    public CreateMenuItemUseCase.Output create(CreateMenuItemUseCase.Input input) {
        return createMenuItemUseCase.execute(input);
    }

    /// Updates an existing [MenuItem].
    ///
    /// @param input the [UpdateMenuItemUseCase.Input] data
    /// @return the [UpdateMenuItemUseCase.Output] containing the updated [MenuItem] data
    /// @throws NotFoundException      if no [MenuItem] or [Restaurant] is found
    /// @throws UnauthorizedException  if the requester is not the owner of the [Restaurant]
    /// @throws AlreadyExistsException if another [MenuItem] with the given name already exists in the [Restaurant]
    public UpdateMenuItemUseCase.Output update(UpdateMenuItemUseCase.Input input) {
        return updateMenuItemUseCase.execute(input);
    }

    /// Deletes a [MenuItem] by its identifier.
    ///
    /// @param input the [DeleteMenuItemUseCase.Input] data
    /// @throws NotFoundException     if no [MenuItem] or [Restaurant] is found
    /// @throws UnauthorizedException if the requester is not the owner of the [Restaurant]
    public void delete(DeleteMenuItemUseCase.Input input) {
        deleteMenuItemUseCase.execute(input);
    }

    /// Finds a [MenuItem] by its identifier.
    ///
    /// @param input the [FindMenuItemByIdUseCase.Input] data
    /// @return the [FindMenuItemByIdUseCase.Output] containing the found [MenuItem] data
    /// @throws NotFoundException if no [MenuItem] is found with the given [MenuItemId]
    public FindMenuItemByIdUseCase.Output findById(FindMenuItemByIdUseCase.Input input) {
        return findMenuItemByIdUseCase.execute(input);
    }

    /// Retrieves a paginated list of [MenuItem] instances.
    ///
    /// @param input the [FindAllMenuItemsUseCase.Input] carrying the [PageRequest]
    /// @return a [Page] of [FindAllMenuItemsUseCase.Output]
    public Page<FindAllMenuItemsUseCase.Output> findAll(FindAllMenuItemsUseCase.Input input) {
        return findAllMenuItemsUseCase.execute(input);
    }
}
