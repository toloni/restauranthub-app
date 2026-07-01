package br.com.postechfiap.toloni.restauranthub.adapters.controllers;

import br.com.postechfiap.toloni.restauranthub.adapters.presenters.restaurant.RestaurantPresenter;
import br.com.postechfiap.toloni.restauranthub.adapters.presenters.restaurant.RestaurantViewModel;
import br.com.postechfiap.toloni.restauranthub.adapters.presenters.restaurant.TransferOwnershipViewModel;
import br.com.postechfiap.toloni.restauranthub.application.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.application.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock
    private CreateRestaurantUseCase createRestaurantUseCase;

    @Mock
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @Mock
    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    @Mock
    private FindRestaurantByIdUseCase findRestaurantByIdUseCase;

    @Mock
    private FindAllRestaurantsUseCase findAllRestaurantsUseCase;

    @Mock
    private TransferRestaurantOwnershipUseCase transferRestaurantOwnershipUseCase;

    @Mock
    private RestaurantPresenter restaurantPresenter;

    @InjectMocks
    private RestaurantController controller;

    private RestaurantId restaurantId;
    private UserId ownerId;
    private UserId requesterId;
    private UserId newOwnerId;

    @BeforeEach
    void setUp() {
        restaurantId = RestaurantId.generate();
        ownerId = UserId.generate();
        requesterId = UserId.generate();
        newOwnerId = UserId.generate();
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate create to CreateRestaurantUseCase and present result")
    void shouldDelegateCreateToCreateRestaurantUseCase() {
        var input = new CreateRestaurantUseCase.Input(
                "The Great Burger", "123 Main St, Springfield", "American", "Mon-Fri 9am-10pm", ownerId
        );
        var output = new CreateRestaurantUseCase.Output(
                restaurantId, RestaurantName.of("The Great Burger"), RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"), RestaurantOpeningHours.of("Mon-Fri 9am-10pm"), ownerId
        );
        var viewModel = new RestaurantViewModel(
                restaurantId.getValue(), "The Great Burger", "123 Main St, Springfield",
                "American", "Mon-Fri 9am-10pm", ownerId.getValue(), null
        );

        when(createRestaurantUseCase.execute(input)).thenReturn(output);
        when(restaurantPresenter.present(output)).thenReturn(viewModel);

        var result = controller.create(input);

        assertThat(result).isEqualTo(viewModel);
        verify(createRestaurantUseCase, times(1)).execute(input);
        verify(restaurantPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate update to UpdateRestaurantUseCase and present result")
    void shouldDelegateUpdateToUpdateRestaurantUseCase() {
        var input = new UpdateRestaurantUseCase.Input(
                restaurantId, "New Burger", null, null, null, ownerId
        );
        var output = new UpdateRestaurantUseCase.Output(
                restaurantId, RestaurantName.of("New Burger"), RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"), RestaurantOpeningHours.of("Mon-Fri 9am-10pm"), ownerId
        );
        var viewModel = new RestaurantViewModel(
                restaurantId.getValue(), "New Burger", "123 Main St, Springfield",
                "American", "Mon-Fri 9am-10pm", ownerId.getValue(), null
        );

        when(updateRestaurantUseCase.execute(input)).thenReturn(output);
        when(restaurantPresenter.present(output)).thenReturn(viewModel);

        var result = controller.update(input);

        assertThat(result).isEqualTo(viewModel);
        verify(updateRestaurantUseCase, times(1)).execute(input);
        verify(restaurantPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate delete to DeleteRestaurantUseCase")
    void shouldDelegateDeleteToDeleteRestaurantUseCase() {
        var input = new DeleteRestaurantUseCase.Input(restaurantId, ownerId);

        doNothing().when(deleteRestaurantUseCase).execute(input);

        controller.delete(input);

        verify(deleteRestaurantUseCase, times(1)).execute(input);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate findById to FindRestaurantByIdUseCase and present result")
    void shouldDelegateFindByIdToFindRestaurantByIdUseCase() {
        var input = new FindRestaurantByIdUseCase.Input(restaurantId);
        var output = new FindRestaurantByIdUseCase.Output(
                restaurantId, RestaurantName.of("The Great Burger"), RestaurantAddress.of("123 Main St, Springfield"), RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"), ownerId, "John Doe"
        );
        var viewModel = new RestaurantViewModel(
                restaurantId.getValue(), "The Great Burger", "123 Main St, Springfield",
                "American", "Mon-Fri 9am-10pm", ownerId.getValue(), "John Doe"
        );

        when(findRestaurantByIdUseCase.execute(input)).thenReturn(output);
        when(restaurantPresenter.present(output)).thenReturn(viewModel);

        var result = controller.findById(input);

        assertThat(result).isEqualTo(viewModel);
        verify(findRestaurantByIdUseCase, times(1)).execute(input);
        verify(restaurantPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate findAll to FindAllRestaurantsUseCase and present results")
    void shouldDelegateFindAllToFindAllRestaurantsUseCase() {
        var pageRequest = PageRequest.of(0, 10);
        var input = new FindAllRestaurantsUseCase.Input(pageRequest);
        var output = new FindAllRestaurantsUseCase.Output(
                restaurantId, RestaurantName.of("The Great Burger"), RestaurantAddress.of("123 Main St, Springfield"), RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"), ownerId, "John Doe"
        );
        var viewModel = new RestaurantViewModel(
                restaurantId.getValue(), "The Great Burger", "123 Main St, Springfield",
                "American", "Mon-Fri 9am-10pm", ownerId.getValue(), "John Doe"
        );
        var useCasePage = Page.of(List.of(output), 0, 10, 1L);
        var expectedPage = Page.of(List.of(viewModel), 0, 10, 1L);

        when(findAllRestaurantsUseCase.execute(input)).thenReturn(useCasePage);
        when(restaurantPresenter.present(output)).thenReturn(viewModel);

        var result = controller.findAll(input);

        assertThat(result).isEqualTo(expectedPage);
        verify(findAllRestaurantsUseCase, times(1)).execute(input);
        verify(restaurantPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // transferOwnership
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate transferOwnership to TransferRestaurantOwnershipUseCase and present result")
    void shouldDelegateTransferOwnershipToTransferRestaurantOwnershipUseCase() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);
        var output = new TransferRestaurantOwnershipUseCase.Output(newOwnerId);
        var viewModel = new TransferOwnershipViewModel(newOwnerId.getValue());

        when(transferRestaurantOwnershipUseCase.execute(input)).thenReturn(output);
        when(restaurantPresenter.present(output)).thenReturn(viewModel);

        var result = controller.transferOwnership(input);

        assertThat(result).isEqualTo(viewModel);
        verify(transferRestaurantOwnershipUseCase, times(1)).execute(input);
        verify(restaurantPresenter, times(1)).present(output);
    }
}
