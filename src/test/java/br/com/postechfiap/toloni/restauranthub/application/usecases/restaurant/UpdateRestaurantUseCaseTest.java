package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateRestaurantUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private UpdateRestaurantUseCase useCase;

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
    @DisplayName("Should update Restaurant name successfully")
    void shouldUpdateRestaurantNameSuccessfully() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, "New Burger", null, null, null, ownerId);
        var updatedRestaurant = new Restaurant(restaurantId, RestaurantName.of("New Burger"),
                RestaurantAddress.of("123 Main St, Springfield"), RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"), ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.existsByNameAndIdNot(RestaurantName.of("New Burger"), restaurantId)).thenReturn(false);
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.name()).isEqualTo("New Burger");
    }

    @Test
    @DisplayName("Should update Restaurant address successfully")
    void shouldUpdateRestaurantAddressSuccessfully() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, "New Address", null, null, ownerId);
        var updatedRestaurant = new Restaurant(restaurantId, RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("New Address"), RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"), ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.address()).isEqualTo("New Address");
    }

    @Test
    @DisplayName("Should update Restaurant cuisineType successfully")
    void shouldUpdateRestaurantCuisineTypeSuccessfully() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, null, "Italian", null, ownerId);
        var updatedRestaurant = new Restaurant(restaurantId, RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"), RestaurantCuisineType.of("Italian"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"), ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.cuisineType()).isEqualTo("Italian");
    }

    @Test
    @DisplayName("Should update Restaurant openingHours successfully")
    void shouldUpdateRestaurantOpeningHoursSuccessfully() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, null, null, "Tue-Sun 11am-11pm", ownerId);
        var updatedRestaurant = new Restaurant(restaurantId, RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"), RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Tue-Sun 11am-11pm"), ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.openingHours()).isEqualTo("Tue-Sun 11am-11pm");
    }

    @Test
    @DisplayName("Should update all fields successfully")
    void shouldUpdateAllFieldsSuccessfully() {
        var input = new UpdateRestaurantUseCase.Input(
                restaurantId, "La Bella Italia", "456 Olive Ave", "Italian", "Tue-Sun 11am-11pm", ownerId);
        var updatedRestaurant = new Restaurant(restaurantId, RestaurantName.of("La Bella Italia"),
                RestaurantAddress.of("456 Olive Ave"), RestaurantCuisineType.of("Italian"),
                RestaurantOpeningHours.of("Tue-Sun 11am-11pm"), ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.existsByNameAndIdNot(RestaurantName.of("La Bella Italia"), restaurantId)).thenReturn(false);
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.name()).isEqualTo("La Bella Italia");
        assertThat(output.address()).isEqualTo("456 Olive Ave");
        assertThat(output.cuisineType()).isEqualTo("Italian");
        assertThat(output.openingHours()).isEqualTo("Tue-Sun 11am-11pm");
    }

    @Test
    @DisplayName("Should not check name uniqueness when name is null")
    void shouldNotCheckNameUniquenessWhenNameIsNull() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, "New Address", null, null, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(restaurant);
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        useCase.execute(input);

        verify(restaurantGateway, never()).existsByNameAndIdNot(any(), any());
    }

    @Test
    @DisplayName("Should call gateway save once")
    void shouldCallGatewaySaveOnce() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, "New Address", null, null, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(restaurant);
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        useCase.execute(input);

        verify(restaurantGateway, times(1)).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should map updated Restaurant to Output correctly")
    void shouldMapUpdatedRestaurantToOutputCorrectly() {
        var input = new UpdateRestaurantUseCase.Input(
                restaurantId, "La Bella Italia", "456 Olive Ave", "Italian", "Tue-Sun 11am-11pm", ownerId);
        var updatedRestaurant = new Restaurant(restaurantId, RestaurantName.of("La Bella Italia"),
                RestaurantAddress.of("456 Olive Ave"), RestaurantCuisineType.of("Italian"),
                RestaurantOpeningHours.of("Tue-Sun 11am-11pm"), ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.existsByNameAndIdNot(RestaurantName.of("La Bella Italia"), restaurantId)).thenReturn(false);
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(updatedRestaurant);
        doNothing().when(authorizationService).validateRestaurantOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.id()).isEqualTo(restaurantId);
        assertThat(output.name()).isEqualTo("La Bella Italia");
        assertThat(output.address()).isEqualTo("456 Olive Ave");
        assertThat(output.cuisineType()).isEqualTo("Italian");
        assertThat(output.openingHours()).isEqualTo("Tue-Sun 11am-11pm");
        assertThat(output.ownerId()).isEqualTo(ownerId);
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when Restaurant is not found")
    void shouldThrowNotFoundExceptionWhenRestaurantIsNotFound() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, "New Address", null, null, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(restaurantId.getValue().toString());
    }

    @Test
    @DisplayName("Should not call save when Restaurant is not found")
    void shouldNotCallSaveWhenRestaurantIsNotFound() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, "New Address", null, null, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(restaurantGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not the owner")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, "New Address", null, null, differentOwnerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        doThrow(new UnauthorizedException("Only the owner can update the restaurant."))
                .when(authorizationService).validateRestaurantOwnership(restaurant, differentOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only the owner can update the restaurant.");
    }

    @Test
    @DisplayName("Should not call save when requester is not the owner")
    void shouldNotCallSaveWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, "New Address", null, null, differentOwnerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        doThrow(new UnauthorizedException("Only the owner can update the restaurant."))
                .when(authorizationService).validateRestaurantOwnership(restaurant, differentOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class);

        verify(restaurantGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when owner User is not found")
    void shouldThrowNotFoundExceptionWhenOwnerUserIsNotFound() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, "New Address", null, null, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ownerId.getValue().toString());
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when name already exists in another Restaurant")
    void shouldThrowAlreadyExistsExceptionWhenNameAlreadyExistsInAnotherRestaurant() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, "La Bella Italia", null, null, null, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.existsByNameAndIdNot(RestaurantName.of("La Bella Italia"), restaurantId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("La Bella Italia");
    }

    @Test
    @DisplayName("Should not call save when name already exists in another Restaurant")
    void shouldNotCallSaveWhenNameAlreadyExistsInAnotherRestaurant() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, "La Bella Italia", null, null, null, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);
        when(restaurantGateway.existsByNameAndIdNot(RestaurantName.of("La Bella Italia"), restaurantId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class);

        verify(restaurantGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DomainException when address is blank")
    void shouldThrowDomainExceptionWhenAddressIsBlank() {
        var input = new UpdateRestaurantUseCase.Input(restaurantId, null, "", null, null, ownerId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userGateway.existsById(ownerId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Restaurant address is required.");
    }
}