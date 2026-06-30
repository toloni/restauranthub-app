package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class DeleteRestaurantUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private MenuItemGateway menuItemGateway;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private DeleteRestaurantUseCase useCase;

    private RestaurantId restaurantId;
    private UserId ownerId;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurantId = RestaurantId.generate();
        ownerId = UserId.generate();
        restaurant = new Restaurant(
                restaurantId,
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                ownerId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete Restaurant and all its MenuItems successfully")
    void shouldDeleteRestaurantAndAllItsMenuItemsSuccessfully() {
        var input = new DeleteRestaurantUseCase.Input(restaurantId, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        useCase.execute(input);

        verify(menuItemGateway, times(1)).deleteAllByRestaurantId(restaurantId);
        verify(restaurantGateway, times(1)).deleteById(restaurantId);
    }

    @Test
    @DisplayName("Should delete MenuItems before deleting Restaurant")
    void shouldDeleteMenuItemsBeforeDeletingRestaurant() {
        var input = new DeleteRestaurantUseCase.Input(restaurantId, ownerId);
        var order = inOrder(menuItemGateway, restaurantGateway);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        useCase.execute(input);

        order.verify(menuItemGateway).deleteAllByRestaurantId(restaurantId);
        order.verify(restaurantGateway).deleteById(restaurantId);
    }

    @Test
    @DisplayName("Should call findById with correct id")
    void shouldCallFindByIdWithCorrectId() {
        var input = new DeleteRestaurantUseCase.Input(restaurantId, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        useCase.execute(input);

        verify(restaurantGateway, times(1)).findById(restaurantId);
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when Restaurant is not found")
    void shouldThrowNotFoundExceptionWhenRestaurantIsNotFound() {
        var input = new DeleteRestaurantUseCase.Input(restaurantId, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(restaurantId.getValue().toString());
    }

    @Test
    @DisplayName("Should not delete MenuItems when Restaurant is not found")
    void shouldNotDeleteMenuItemsWhenRestaurantIsNotFound() {
        var input = new DeleteRestaurantUseCase.Input(restaurantId, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(menuItemGateway, never()).deleteAllByRestaurantId(any());
        verify(restaurantGateway, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not the owner")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var input = new DeleteRestaurantUseCase.Input(restaurantId, differentOwnerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doThrow(new UnauthorizedException("Only the owner can delete the restaurant."))
                .when(authorizationService).validateRestaurantOwnership(restaurant, differentOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only the owner can delete the restaurant.");
    }

    @Test
    @DisplayName("Should not delete MenuItems when requester is not the owner")
    void shouldNotDeleteMenuItemsWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var input = new DeleteRestaurantUseCase.Input(restaurantId, differentOwnerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doThrow(new UnauthorizedException("Only the owner can delete the restaurant."))
                .when(authorizationService).validateRestaurantOwnership(restaurant, differentOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class);

        verify(menuItemGateway, never()).deleteAllByRestaurantId(any());
        verify(restaurantGateway, never()).deleteById(any());
    }
}