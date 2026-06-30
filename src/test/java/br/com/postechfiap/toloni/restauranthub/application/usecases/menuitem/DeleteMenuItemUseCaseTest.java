package br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItem;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class DeleteMenuItemUseCaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private DeleteMenuItemUseCase useCase;

    private MenuItemId menuItemId;
    private RestaurantId restaurantId;
    private UserId ownerId;
    private MenuItem menuItem;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        menuItemId = MenuItemId.generate();
        restaurantId = RestaurantId.generate();
        ownerId = UserId.generate();
        menuItem = new MenuItem(
                menuItemId,
                MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.ofBRL(new BigDecimal("19.90")),
                false,
                MenuItemImagePath.of("/images/classic-burger.jpg"),
                restaurantId
        );
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
    @DisplayName("Should delete MenuItem successfully")
    void shouldDeleteMenuItemSuccessfully() {
        var input = new DeleteMenuItemUseCase.Input(menuItemId, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        useCase.execute(input);

        verify(menuItemGateway, times(1)).deleteById(menuItemId);
    }

    @Test
    @DisplayName("Should call findById with correct menuItemId")
    void shouldCallFindByIdWithCorrectMenuItemId() {
        var input = new DeleteMenuItemUseCase.Input(menuItemId, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        useCase.execute(input);

        verify(menuItemGateway, times(1)).findById(menuItemId);
    }

    @Test
    @DisplayName("Should call restaurant findById with correct restaurantId")
    void shouldCallRestaurantFindByIdWithCorrectRestaurantId() {
        var input = new DeleteMenuItemUseCase.Input(menuItemId, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        useCase.execute(input);

        verify(restaurantGateway, times(1)).findById(restaurantId);
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when MenuItem is not found")
    void shouldThrowNotFoundExceptionWhenMenuItemIsNotFound() {
        var input = new DeleteMenuItemUseCase.Input(menuItemId, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(menuItemId.getValue().toString());
    }

    @Test
    @DisplayName("Should not call deleteById when MenuItem is not found")
    void shouldNotCallDeleteByIdWhenMenuItemIsNotFound() {
        var input = new DeleteMenuItemUseCase.Input(menuItemId, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(menuItemGateway, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when Restaurant is not found")
    void shouldThrowNotFoundExceptionWhenRestaurantIsNotFound() {
        var input = new DeleteMenuItemUseCase.Input(menuItemId, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(restaurantId.getValue().toString());
    }

    @Test
    @DisplayName("Should not call deleteById when Restaurant is not found")
    void shouldNotCallDeleteByIdWhenRestaurantIsNotFound() {
        var input = new DeleteMenuItemUseCase.Input(menuItemId, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(menuItemGateway, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not the owner")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var input = new DeleteMenuItemUseCase.Input(menuItemId, differentOwnerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doThrow(new UnauthorizedException("Only the owner can delete items of the restaurant."))
                .when(authorizationService).validateMenuItemOwnership(restaurant, differentOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only the owner can delete items of the restaurant.");
    }

    @Test
    @DisplayName("Should not call deleteById when requester is not the owner")
    void shouldNotCallDeleteByIdWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var input = new DeleteMenuItemUseCase.Input(menuItemId, differentOwnerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doThrow(new UnauthorizedException("Only the owner can delete items of the restaurant."))
                .when(authorizationService).validateMenuItemOwnership(restaurant, differentOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class);

        verify(menuItemGateway, never()).deleteById(any());
    }
}