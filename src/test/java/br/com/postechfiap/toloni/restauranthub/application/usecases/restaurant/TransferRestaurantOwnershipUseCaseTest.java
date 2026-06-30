package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferRestaurantOwnershipUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private TransferRestaurantOwnershipUseCase useCase;

    private RestaurantId restaurantId;
    private UserId requesterId;
    private UserId currentOwnerId;
    private UserId newOwnerId;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurantId = RestaurantId.generate();
        requesterId = UserId.generate();
        currentOwnerId = UserId.generate();
        newOwnerId = UserId.generate();
        restaurant = new Restaurant(
                restaurantId,
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                currentOwnerId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should transfer ownership successfully")
    void shouldTransferOwnershipSuccessfully() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);
        var updatedRestaurant = new Restaurant(
                restaurantId,
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                newOwnerId
        );

        doNothing().when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.ownerId()).isEqualTo(newOwnerId);
    }

    @Test
    @DisplayName("Should call validateRestaurantTransferOwnership with correct ids")
    void shouldCallValidateRestaurantTransferOwnershipWithCorrectIds() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);
        var updatedRestaurant = new Restaurant(
                restaurantId,
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                newOwnerId
        );

        doNothing().when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        useCase.execute(input);

        verify(authorizationService, times(1)).validateRestaurantTransferOwnership(requesterId, newOwnerId);
    }

    @Test
    @DisplayName("Should call gateway save once")
    void shouldCallGatewaySaveOnce() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);
        var updatedRestaurant = new Restaurant(
                restaurantId,
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                newOwnerId
        );

        doNothing().when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        useCase.execute(input);

        verify(restaurantGateway, times(1)).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should call findById with correct restaurantId")
    void shouldCallFindByIdWithCorrectRestaurantId() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);
        var updatedRestaurant = new Restaurant(
                restaurantId,
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                newOwnerId
        );

        doNothing().when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        useCase.execute(input);

        verify(restaurantGateway, times(1)).findById(restaurantId);
    }

    @Test
    @DisplayName("Should return output with new ownerId")
    void shouldReturnOutputWithNewOwnerId() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);
        var updatedRestaurant = new Restaurant(
                restaurantId,
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                newOwnerId
        );

        doNothing().when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        var output = useCase.execute(input);

        assertThat(output.ownerId()).isEqualTo(newOwnerId);
        assertThat(output.ownerId()).isNotEqualTo(currentOwnerId);
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not authorized")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsNotAuthorized() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);

        doThrow(new UnauthorizedException("Only an admin can transfer restaurant ownership."))
                .when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only an admin can transfer restaurant ownership.");
    }

    @Test
    @DisplayName("Should not call findById when requester is not authorized")
    void shouldNotCallFindByIdWhenRequesterIsNotAuthorized() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);

        doThrow(new UnauthorizedException("Only an admin can transfer restaurant ownership."))
                .when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class);

        verify(restaurantGateway, never()).findById(any());
    }

    @Test
    @DisplayName("Should not call save when requester is not authorized")
    void shouldNotCallSaveWhenRequesterIsNotAuthorized() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);

        doThrow(new UnauthorizedException("Only an admin can transfer restaurant ownership."))
                .when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class);

        verify(restaurantGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when new owner is not a Restaurant Owner")
    void shouldThrowUnauthorizedExceptionWhenNewOwnerIsNotRestaurantOwner() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);

        doThrow(new UnauthorizedException("New owner must have the RESTAURANT_OWNER role."))
                .when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("New owner must have the RESTAURANT_OWNER role.");
    }

    @Test
    @DisplayName("Should throw NotFoundException when Restaurant is not found")
    void shouldThrowNotFoundExceptionWhenRestaurantIsNotFound() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);

        doNothing().when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(restaurantId.getValue().toString());
    }

    @Test
    @DisplayName("Should not call save when Restaurant is not found")
    void shouldNotCallSaveWhenRestaurantIsNotFound() {
        var input = new TransferRestaurantOwnershipUseCase.Input(restaurantId, requesterId, newOwnerId);

        doNothing().when(authorizationService).validateRestaurantTransferOwnership(requesterId, newOwnerId);
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(restaurantGateway, never()).save(any());
    }
}