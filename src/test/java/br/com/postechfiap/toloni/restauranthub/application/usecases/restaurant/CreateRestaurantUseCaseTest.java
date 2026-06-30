package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateRestaurantUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserTypeGateway userTypeGateway;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private CreateRestaurantUseCase useCase;

    private UserId ownerId;
    private UserTypeId userTypeId;
    private User owner;
    private UserType restaurantOwnerType;
    private CreateRestaurantUseCase.Input input;

    @BeforeEach
    void setUp() {
        ownerId = UserId.generate();
        userTypeId = UserTypeId.generate();
        owner = new User(
                ownerId,
                UserName.of("John Doe"),
                UserEmail.of("john@example.com"),
                UserPassword.of("password123"),
                userTypeId
        );
        restaurantOwnerType = new UserType(
                userTypeId,
                UserTypeName.of("Restaurant Owner"),
                UserTypeDescription.of("Owns and manages a restaurant"),
                UserRole.RESTAURANT_OWNER
        );
        input = new CreateRestaurantUseCase.Input(
                "The Great Burger",
                "123 Main St, Springfield",
                "American",
                "Mon-Fri 9am-10pm",
                ownerId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create Restaurant successfully")
    void shouldCreateRestaurantSuccessfully() {
        var restaurant = new Restaurant(
                RestaurantId.generate(),
                RestaurantName.of(input.name()),
                RestaurantAddress.of(input.address()),
                RestaurantCuisineType.of(input.cuisineType()),
                RestaurantOpeningHours.of(input.openingHours()),
                ownerId
        );

        when(restaurantGateway.existsByName(RestaurantName.of(input.name()))).thenReturn(false);
        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(restaurant);

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();
        assertThat(output.name()).isEqualTo(input.name());
        assertThat(output.address()).isEqualTo(input.address());
        assertThat(output.cuisineType()).isEqualTo(input.cuisineType());
        assertThat(output.openingHours()).isEqualTo(input.openingHours());
        assertThat(output.ownerId()).isEqualTo(ownerId);
    }

    @Test
    @DisplayName("Should call gateway save once")
    void shouldCallGatewaySaveOnce() {
        var restaurant = new Restaurant(
                RestaurantId.generate(),
                RestaurantName.of(input.name()),
                RestaurantAddress.of(input.address()),
                RestaurantCuisineType.of(input.cuisineType()),
                RestaurantOpeningHours.of(input.openingHours()),
                ownerId
        );

        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(restaurant);

        useCase.execute(input);

        verify(restaurantGateway, times(1)).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should call existsByName with correct name")
    void shouldCallExistsByNameWithCorrectName() {
        var restaurant = new Restaurant(
                RestaurantId.generate(),
                RestaurantName.of(input.name()),
                RestaurantAddress.of(input.address()),
                RestaurantCuisineType.of(input.cuisineType()),
                RestaurantOpeningHours.of(input.openingHours()),
                ownerId
        );

        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(restaurant);

        useCase.execute(input);

        verify(restaurantGateway, times(1)).existsByName(RestaurantName.of(input.name()));
    }

    @Test
    @DisplayName("Should return output with generated id")
    void shouldReturnOutputWithGeneratedId() {
        var restaurant = new Restaurant(
                RestaurantId.generate(),
                RestaurantName.of(input.name()),
                RestaurantAddress.of(input.address()),
                RestaurantCuisineType.of(input.cuisineType()),
                RestaurantOpeningHours.of(input.openingHours()),
                ownerId
        );

        when(restaurantGateway.save(any(Restaurant.class))).thenReturn(restaurant);

        var output = useCase.execute(input);

        assertThat(output.id()).isNotNull();
        assertThat(output.id().getValue()).isNotNull();
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw AlreadyExistsException when Restaurant name already exists")
    void shouldThrowAlreadyExistsExceptionWhenRestaurantNameAlreadyExists() {
        when(restaurantGateway.existsByName(RestaurantName.of(input.name()))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining(input.name());
    }

    @Test
    @DisplayName("Should not call save when Restaurant name already exists")
    void shouldNotCallSaveWhenRestaurantNameAlreadyExists() {
        when(restaurantGateway.existsByName(RestaurantName.of(input.name()))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class);

        verify(restaurantGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when owner User is not found")
    void shouldThrowNotFoundExceptionWhenOwnerUserIsNotFound() {
        when(restaurantGateway.existsByName(RestaurantName.of(input.name()))).thenReturn(false);
        doThrow(new NotFoundException("User", ownerId.getValue().toString()))
                .when(authorizationService).validateRestaurantOwnerRole(ownerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ownerId.getValue().toString());
    }

    @Test
    @DisplayName("Should throw NotFoundException when UserType is not found")
    void shouldThrowNotFoundExceptionWhenUserTypeIsNotFound() {
        when(restaurantGateway.existsByName(RestaurantName.of(input.name()))).thenReturn(false);
        doThrow(new NotFoundException("UserType", userTypeId.getValue().toString()))
                .when(authorizationService).validateRestaurantOwnerRole(ownerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(userTypeId.getValue().toString());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when User is not a Restaurant Owner")
    void shouldThrowUnauthorizedExceptionWhenUserIsNotRestaurantOwner() {
        when(restaurantGateway.existsByName(RestaurantName.of(input.name()))).thenReturn(false);
        doThrow(new UnauthorizedException("User is not a Restaurant Owner."))
                .when(authorizationService).validateRestaurantOwnerRole(ownerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User is not a Restaurant Owner.");
    }

    @Test
    @DisplayName("Should not call save when User is not a Restaurant Owner")
    void shouldNotCallSaveWhenUserIsNotRestaurantOwner() {
        when(restaurantGateway.existsByName(RestaurantName.of(input.name()))).thenReturn(false);
        doThrow(new UnauthorizedException("User is not a Restaurant Owner."))
                .when(authorizationService).validateRestaurantOwnerRole(ownerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class);

        verify(restaurantGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DomainException when name is blank")
    void shouldThrowDomainExceptionWhenNameIsBlank() {
        var invalidInput = new CreateRestaurantUseCase.Input(
                "",
                "123 Main St, Springfield",
                "American",
                "Mon-Fri 9am-10pm",
                ownerId
        );

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Restaurant name is required.");
    }

    @DisplayName("Should throw DomainException when address is blank")
    void shouldThrowDomainExceptionWhenAddressIsBlank() {
        var invalidInput = new CreateRestaurantUseCase.Input(
                "The Great Burger",
                "",
                "American",
                "Mon-Fri 9am-10pm",
                ownerId
        );

        when(restaurantGateway.existsByName(any())).thenReturn(false);
        doNothing().when(authorizationService).validateRestaurantOwnerRole(ownerId);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Restaurant address is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when cuisineType is blank")
    void shouldThrowDomainExceptionWhenCuisineTypeIsBlank() {
        var invalidInput = new CreateRestaurantUseCase.Input(
                "The Great Burger",
                "123 Main St, Springfield",
                "",
                "Mon-Fri 9am-10pm",
                ownerId
        );

        when(restaurantGateway.existsByName(any())).thenReturn(false);
        doNothing().when(authorizationService).validateRestaurantOwnerRole(ownerId);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Restaurant cuisine type is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when openingHours is blank")
    void shouldThrowDomainExceptionWhenOpeningHoursIsBlank() {
        var invalidInput = new CreateRestaurantUseCase.Input(
                "The Great Burger",
                "123 Main St, Springfield",
                "American",
                "",
                ownerId
        );

        when(restaurantGateway.existsByName(any())).thenReturn(false);
        doNothing().when(authorizationService).validateRestaurantOwnerRole(ownerId);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Restaurant opening hours is required.");
    }
}