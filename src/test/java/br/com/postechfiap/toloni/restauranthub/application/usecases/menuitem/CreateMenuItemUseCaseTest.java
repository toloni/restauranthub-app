package br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItem;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
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
import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateMenuItemUseCaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private CreateMenuItemUseCase useCase;

    private RestaurantId restaurantId;
    private UserId ownerId;
    private Restaurant restaurant;
    private CreateMenuItemUseCase.Input input;

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
        input = new CreateMenuItemUseCase.Input(
                "Classic Burger",
                "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"),
                Currency.getInstance("BRL"),
                false,
                "/images/classic-burger.jpg",
                restaurantId,
                ownerId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create MenuItem successfully")
    void shouldCreateMenuItemSuccessfully() {
        var menuItem = new MenuItem(
                MenuItemId.generate(),
                MenuItemName.of(input.name()),
                MenuItemDescription.of(input.description()),
                MenuItemPrice.of(input.price(), input.currency()),
                input.dineInOnly(),
                MenuItemImagePath.of(input.imagePath()),
                restaurantId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantId(MenuItemName.of(input.name()), restaurantId)).thenReturn(false);
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(menuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, input.ownerId());

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();
        assertThat(output.name()).isEqualTo(input.name());
        assertThat(output.description()).isEqualTo(input.description());
        assertThat(output.price()).isEqualByComparingTo(input.price());
        assertThat(output.currency()).isEqualTo(input.currency());
        assertThat(output.dineInOnly()).isEqualTo(input.dineInOnly());
        assertThat(output.imagePath()).isEqualTo(input.imagePath());
        assertThat(output.restaurantId()).isEqualTo(restaurantId);
    }

    @Test
    @DisplayName("Should create MenuItem with dineInOnly true")
    void shouldCreateMenuItemWithDineInOnlyTrue() {
        var dineInInput = new CreateMenuItemUseCase.Input(
                "Classic Burger",
                "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"),
                Currency.getInstance("BRL"),
                true,
                "/images/classic-burger.jpg",
                restaurantId,
                ownerId
        );
        var menuItem = new MenuItem(
                MenuItemId.generate(),
                MenuItemName.of(dineInInput.name()),
                MenuItemDescription.of(dineInInput.description()),
                MenuItemPrice.of(dineInInput.price(), dineInInput.currency()),
                dineInInput.dineInOnly(),
                MenuItemImagePath.of(dineInInput.imagePath()),
                restaurantId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantId(any(), any())).thenReturn(false);
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(menuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, input.ownerId());

        var output = useCase.execute(dineInInput);

        assertThat(output.dineInOnly()).isTrue();
    }

    @Test
    @DisplayName("Should create MenuItem without imagePath")
    void shouldCreateMenuItemWithoutImagePath() {
        var noImageInput = new CreateMenuItemUseCase.Input(
                "Classic Burger",
                "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"),
                Currency.getInstance("BRL"),
                false,
                null,
                restaurantId,
                ownerId
        );
        var menuItem = new MenuItem(
                MenuItemId.generate(),
                MenuItemName.of(noImageInput.name()),
                MenuItemDescription.of(noImageInput.description()),
                MenuItemPrice.of(noImageInput.price(), noImageInput.currency()),
                noImageInput.dineInOnly(),
                MenuItemImagePath.of(null),
                restaurantId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantId(any(), any())).thenReturn(false);
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(menuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, input.ownerId());

        var output = useCase.execute(noImageInput);

        assertThat(output.imagePath()).isNull();
    }

    @Test
    @DisplayName("Should call gateway save once")
    void shouldCallGatewaySaveOnce() {
        var menuItem = new MenuItem(
                MenuItemId.generate(),
                MenuItemName.of(input.name()),
                MenuItemDescription.of(input.description()),
                MenuItemPrice.of(input.price(), input.currency()),
                input.dineInOnly(),
                MenuItemImagePath.of(input.imagePath()),
                restaurantId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantId(any(), any())).thenReturn(false);
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(menuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, input.ownerId());

        useCase.execute(input);

        verify(menuItemGateway, times(1)).save(any(MenuItem.class));
    }

    @Test
    @DisplayName("Should return output with generated id")
    void shouldReturnOutputWithGeneratedId() {
        var menuItem = new MenuItem(
                MenuItemId.generate(),
                MenuItemName.of(input.name()),
                MenuItemDescription.of(input.description()),
                MenuItemPrice.of(input.price(), input.currency()),
                input.dineInOnly(),
                MenuItemImagePath.of(input.imagePath()),
                restaurantId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantId(any(), any())).thenReturn(false);
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(menuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, input.ownerId());

        var output = useCase.execute(input);

        assertThat(output.id()).isNotNull();
        assertThat(output.id().getValue()).isNotNull();
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when Restaurant is not found")
    void shouldThrowNotFoundExceptionWhenRestaurantIsNotFound() {
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(restaurantId.getValue().toString());
    }

    @Test
    @DisplayName("Should not call save when Restaurant is not found")
    void shouldNotCallSaveWhenRestaurantIsNotFound() {
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(menuItemGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not the owner")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var unauthorizedInput = new CreateMenuItemUseCase.Input(
                "Classic Burger",
                "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"),
                Currency.getInstance("BRL"),
                false,
                "/images/classic-burger.jpg",
                restaurantId,
                differentOwnerId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        doThrow(new UnauthorizedException("Only the owner can add items to the restaurant."))
                .when(authorizationService).validateMenuItemOwnership(restaurant, differentOwnerId);

        assertThatThrownBy(() -> useCase.execute(unauthorizedInput))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only the owner can add items to the restaurant.");
    }

    @Test
    @DisplayName("Should not call save when requester is not the owner")
    void shouldNotCallSaveWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var unauthorizedInput = new CreateMenuItemUseCase.Input(
                "Classic Burger",
                "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"),
                Currency.getInstance("BRL"),
                false,
                "/images/classic-burger.jpg",
                restaurantId,
                differentOwnerId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        doThrow(new UnauthorizedException("Only the owner can add items to the restaurant."))
                .when(authorizationService).validateMenuItemOwnership(restaurant, differentOwnerId);

        assertThatThrownBy(() -> useCase.execute(unauthorizedInput))
                .isInstanceOf(UnauthorizedException.class);

        verify(menuItemGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when MenuItem name already exists in Restaurant")
    void shouldThrowAlreadyExistsExceptionWhenMenuItemNameAlreadyExistsInRestaurant() {
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantId(MenuItemName.of(input.name()), restaurantId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining(input.name());
    }

    @Test
    @DisplayName("Should not call save when MenuItem name already exists in Restaurant")
    void shouldNotCallSaveWhenMenuItemNameAlreadyExistsInRestaurant() {
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantId(MenuItemName.of(input.name()), restaurantId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class);

        verify(menuItemGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DomainException when name is blank")
    void shouldThrowDomainExceptionWhenNameIsBlank() {
        var invalidInput = new CreateMenuItemUseCase.Input(
                "",
                "Juicy beef patty with lettuce and tomato",
                new BigDecimal("19.90"),
                Currency.getInstance("BRL"),
                false,
                "/images/classic-burger.jpg",
                restaurantId,
                ownerId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("MenuItem name is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when description is blank")
    void shouldThrowDomainExceptionWhenDescriptionIsBlank() {
        var invalidInput = new CreateMenuItemUseCase.Input(
                "Classic Burger",
                "",
                new BigDecimal("19.90"),
                Currency.getInstance("BRL"),
                false,
                "/images/classic-burger.jpg",
                restaurantId,
                ownerId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantId(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("MenuItem description is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when price is negative")
    void shouldThrowDomainExceptionWhenPriceIsNegative() {
        var invalidInput = new CreateMenuItemUseCase.Input(
                "Classic Burger",
                "Juicy beef patty with lettuce and tomato",
                new BigDecimal("-1.00"),
                Currency.getInstance("BRL"),
                false,
                "/images/classic-burger.jpg",
                restaurantId,
                ownerId
        );

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantId(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("MenuItem price must not be negative.");
    }
}