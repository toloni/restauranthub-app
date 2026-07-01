package br.com.postechfiap.toloni.restauranthub.adapters.controllers;

import br.com.postechfiap.toloni.restauranthub.adapters.presenters.menuitem.MenuItemPresenter;
import br.com.postechfiap.toloni.restauranthub.adapters.presenters.menuitem.MenuItemViewModel;
import br.com.postechfiap.toloni.restauranthub.application.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.application.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.*;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class MenuItemControllerTest {

    @Mock
    private CreateMenuItemUseCase createMenuItemUseCase;

    @Mock
    private UpdateMenuItemUseCase updateMenuItemUseCase;

    @Mock
    private DeleteMenuItemUseCase deleteMenuItemUseCase;

    @Mock
    private FindMenuItemByIdUseCase findMenuItemByIdUseCase;

    @Mock
    private FindAllMenuItemsUseCase findAllMenuItemsUseCase;

    @Mock
    private MenuItemPresenter menuItemPresenter;

    @InjectMocks
    private MenuItemController controller;

    private MenuItemId menuItemId;
    private RestaurantId restaurantId;
    private UserId ownerId;

    @BeforeEach
    void setUp() {
        menuItemId = MenuItemId.generate();
        restaurantId = RestaurantId.generate();
        ownerId = UserId.generate();
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate create to CreateMenuItemUseCase and present result")
    void shouldDelegateCreateToCreateMenuItemUseCase() {
        var input = new CreateMenuItemUseCase.Input(
                "Classic Burger",
                "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"),
                Currency.getInstance("BRL"),
                false,
                "/images/classic-burger.jpg",
                restaurantId,
                ownerId
        );
        var output = new CreateMenuItemUseCase.Output(
                menuItemId,
                MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.of(new BigDecimal("19.90"), Currency.getInstance("BRL")),
                false,
                MenuItemImagePath.of("/images/classic-burger.jpg"),
                restaurantId
        );
        var viewModel = new MenuItemViewModel(
                menuItemId.getValue(), "Classic Burger", "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"), Currency.getInstance("BRL"), false,
                "/images/classic-burger.jpg", restaurantId.getValue(), null
        );

        when(createMenuItemUseCase.execute(input)).thenReturn(output);
        when(menuItemPresenter.present(output)).thenReturn(viewModel);

        var result = controller.create(input);

        assertThat(result).isEqualTo(viewModel);
        verify(createMenuItemUseCase, times(1)).execute(input);
        verify(menuItemPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate update to UpdateMenuItemUseCase and present result")
    void shouldDelegateUpdateToUpdateMenuItemUseCase() {
        var input = new UpdateMenuItemUseCase.Input(
                menuItemId, "Bacon Burger", null, null, null, null, null, ownerId
        );
        var output = new UpdateMenuItemUseCase.Output(
                menuItemId,
                MenuItemName.of("Bacon Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.of(new BigDecimal("19.90"), Currency.getInstance("BRL")),
                false,
                MenuItemImagePath.of("/images/classic-burger.jpg"),
                restaurantId
        );
        var viewModel = new MenuItemViewModel(
                menuItemId.getValue(), "Bacon Burger", "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"), Currency.getInstance("BRL"), false,
                "/images/classic-burger.jpg", restaurantId.getValue(), null
        );

        when(updateMenuItemUseCase.execute(input)).thenReturn(output);
        when(menuItemPresenter.present(output)).thenReturn(viewModel);

        var result = controller.update(input);

        assertThat(result).isEqualTo(viewModel);
        verify(updateMenuItemUseCase, times(1)).execute(input);
        verify(menuItemPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate delete to DeleteMenuItemUseCase")
    void shouldDelegateDeleteToDeleteMenuItemUseCase() {
        var input = new DeleteMenuItemUseCase.Input(menuItemId, ownerId);

        doNothing().when(deleteMenuItemUseCase).execute(input);

        controller.delete(input);

        verify(deleteMenuItemUseCase, times(1)).execute(input);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate findById to FindMenuItemByIdUseCase and present result")
    void shouldDelegateFindByIdToFindMenuItemByIdUseCase() {
        var input = new FindMenuItemByIdUseCase.Input(menuItemId);
        var output = new FindMenuItemByIdUseCase.Output(
                menuItemId,
                MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.of(new BigDecimal("19.90"), Currency.getInstance("BRL")),
                false,
                MenuItemImagePath.of("/images/classic-burger.jpg"),
                restaurantId,
                "The Great Burger"
        );
        var viewModel = new MenuItemViewModel(
                menuItemId.getValue(), "Classic Burger", "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"), Currency.getInstance("BRL"), false,
                "/images/classic-burger.jpg", restaurantId.getValue(), "The Great Burger"
        );

        when(findMenuItemByIdUseCase.execute(input)).thenReturn(output);
        when(menuItemPresenter.present(output)).thenReturn(viewModel);

        var result = controller.findById(input);

        assertThat(result).isEqualTo(viewModel);
        verify(findMenuItemByIdUseCase, times(1)).execute(input);
        verify(menuItemPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate findAll to FindAllMenuItemsUseCase and present results")
    void shouldDelegateFindAllToFindAllMenuItemsUseCase() {
        var pageRequest = PageRequest.of(0, 10);
        var input = new FindAllMenuItemsUseCase.Input(null, pageRequest);
        var output = new FindAllMenuItemsUseCase.Output(
                menuItemId,
                MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.of(new BigDecimal("19.90"), Currency.getInstance("BRL")),
                false,
                MenuItemImagePath.of("/images/classic-burger.jpg"),
                restaurantId,
                "The Great Burger"
        );
        var viewModel = new MenuItemViewModel(
                menuItemId.getValue(), "Classic Burger", "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"), Currency.getInstance("BRL"), false,
                "/images/classic-burger.jpg", restaurantId.getValue(), "The Great Burger"
        );
        var useCasePage = Page.of(List.of(output), 0, 10, 1L);
        var expectedPage = Page.of(List.of(viewModel), 0, 10, 1L);

        when(findAllMenuItemsUseCase.execute(input)).thenReturn(useCasePage);
        when(menuItemPresenter.present(output)).thenReturn(viewModel);

        var result = controller.findAll(input);

        assertThat(result).isEqualTo(expectedPage);
        verify(findAllMenuItemsUseCase, times(1)).execute(input);
        verify(menuItemPresenter, times(1)).present(output);
    }

    @Test
    @DisplayName("Should delegate findAll with restaurantId filter to FindAllMenuItemsUseCase")
    void shouldDelegateFindAllWithRestaurantIdFilterToFindAllMenuItemsUseCase() {
        var pageRequest = PageRequest.of(0, 10);
        var input = new FindAllMenuItemsUseCase.Input(restaurantId, pageRequest);
        var page = Page.<FindAllMenuItemsUseCase.Output>of(List.of(), 0, 10, 0L);
        var expectedPage = Page.<MenuItemViewModel>of(List.of(), 0, 10, 0L);

        when(findAllMenuItemsUseCase.execute(input)).thenReturn(page);

        var result = controller.findAll(input);

        assertThat(result).isEqualTo(expectedPage);
        verify(findAllMenuItemsUseCase, times(1)).execute(input);
    }
}
