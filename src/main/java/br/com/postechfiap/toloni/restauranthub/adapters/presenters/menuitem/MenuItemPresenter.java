package br.com.postechfiap.toloni.restauranthub.adapters.presenters.menuitem;

import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.CreateMenuItemUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.FindAllMenuItemsUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.FindMenuItemByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.UpdateMenuItemUseCase;

/// Presenter that converts [MenuItem] use case outputs into [MenuItemViewModel].
///
/// Decouples the adapter layer from use case output types, producing
/// a uniform view model suitable for any presentation layer.
public class MenuItemPresenter {

    /// Converts a [CreateMenuItemUseCase.Output] to a [MenuItemViewModel].
    ///
    /// @param output the output from the create use case
    /// @return the corresponding [MenuItemViewModel]
    public MenuItemViewModel present(CreateMenuItemUseCase.Output output) {
        return new MenuItemViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.description().getValue(),
                output.price().getAmount(),
                output.price().getCurrency(),
                output.dineInOnly(),
                output.imagePath().getValue(),
                output.restaurantId().getValue(),
                null
        );
    }

    /// Converts an [UpdateMenuItemUseCase.Output] to a [MenuItemViewModel].
    ///
    /// @param output the output from the update use case
    /// @return the corresponding [MenuItemViewModel]
    public MenuItemViewModel present(UpdateMenuItemUseCase.Output output) {
        return new MenuItemViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.description().getValue(),
                output.price().getAmount(),
                output.price().getCurrency(),
                output.dineInOnly(),
                output.imagePath().getValue(),
                output.restaurantId().getValue(),
                null
        );
    }

    /// Converts a [FindMenuItemByIdUseCase.Output] to a [MenuItemViewModel].
    ///
    /// @param output the output from the find-by-id use case (includes restaurant name)
    /// @return the corresponding [MenuItemViewModel]
    public MenuItemViewModel present(FindMenuItemByIdUseCase.Output output) {
        return new MenuItemViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.description().getValue(),
                output.price().getAmount(),
                output.price().getCurrency(),
                output.dineInOnly(),
                output.imagePath().getValue(),
                output.restaurantId().getValue(),
                output.restaurantName()
        );
    }

    /// Converts a [FindAllMenuItemsUseCase.Output] to a [MenuItemViewModel].
    ///
    /// @param output the output from the find-all use case (includes restaurant name)
    /// @return the corresponding [MenuItemViewModel]
    public MenuItemViewModel present(FindAllMenuItemsUseCase.Output output) {
        return new MenuItemViewModel(
                output.id().getValue(),
                output.name().getValue(),
                output.description().getValue(),
                output.price().getAmount(),
                output.price().getCurrency(),
                output.dineInOnly(),
                output.imagePath().getValue(),
                output.restaurantId().getValue(),
                output.restaurantName()
        );
    }
}
