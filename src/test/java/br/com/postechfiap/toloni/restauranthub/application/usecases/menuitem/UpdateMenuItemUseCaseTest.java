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
class UpdateMenuItemUseCaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private UpdateMenuItemUseCase useCase;

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
    @DisplayName("Should update MenuItem name successfully")
    void shouldUpdateMenuItemNameSuccessfully() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, "Bacon Burger", null, null, null, null, null, ownerId);
        var updatedMenuItem = new MenuItem(menuItemId, MenuItemName.of("Bacon Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.ofBRL(new BigDecimal("19.90")), false,
                MenuItemImagePath.of("/images/classic-burger.jpg"), restaurantId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantIdAndIdNot(MenuItemName.of("Bacon Burger"), restaurantId, menuItemId)).thenReturn(false);
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(updatedMenuItem);

        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.name()).isEqualTo("Bacon Burger");
    }

    @Test
    @DisplayName("Should update MenuItem description successfully")
    void shouldUpdateMenuItemDescriptionSuccessfully() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, "New description", null, null, null, null, ownerId);
        var updatedMenuItem = new MenuItem(menuItemId, MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("New description"),
                MenuItemPrice.ofBRL(new BigDecimal("19.90")), false,
                MenuItemImagePath.of("/images/classic-burger.jpg"), restaurantId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(updatedMenuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.description()).isEqualTo("New description");
    }

    @Test
    @DisplayName("Should update MenuItem price successfully")
    void shouldUpdateMenuItemPriceSuccessfully() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, null,
                new BigDecimal("24.90"), Currency.getInstance("BRL"), null, null, ownerId);
        var updatedMenuItem = new MenuItem(menuItemId, MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.ofBRL(new BigDecimal("24.90")), false,
                MenuItemImagePath.of("/images/classic-burger.jpg"), restaurantId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(updatedMenuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.price()).isEqualByComparingTo(new BigDecimal("24.90"));
    }

    @Test
    @DisplayName("Should update MenuItem dineInOnly successfully")
    void shouldUpdateMenuItemDineInOnlySuccessfully() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, null, null, null, true, null, ownerId);
        var updatedMenuItem = new MenuItem(menuItemId, MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.ofBRL(new BigDecimal("19.90")), true,
                MenuItemImagePath.of("/images/classic-burger.jpg"), restaurantId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(updatedMenuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.dineInOnly()).isTrue();
    }

    @Test
    @DisplayName("Should update MenuItem imagePath successfully")
    void shouldUpdateMenuItemImagePathSuccessfully() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, null, null, null, null, "/images/new.jpg", ownerId);
        var updatedMenuItem = new MenuItem(menuItemId, MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.ofBRL(new BigDecimal("19.90")), false,
                MenuItemImagePath.of("/images/new.jpg"), restaurantId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(updatedMenuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.imagePath()).isEqualTo("/images/new.jpg");
    }

    @Test
    @DisplayName("Should update all fields successfully")
    void shouldUpdateAllFieldsSuccessfully() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, "Bacon Burger", "Double patty with bacon",
                new BigDecimal("24.90"), Currency.getInstance("BRL"), true, "/images/bacon-burger.jpg", ownerId);
        var updatedMenuItem = new MenuItem(menuItemId, MenuItemName.of("Bacon Burger"),
                MenuItemDescription.of("Double patty with bacon"),
                MenuItemPrice.ofBRL(new BigDecimal("24.90")), true,
                MenuItemImagePath.of("/images/bacon-burger.jpg"), restaurantId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantIdAndIdNot(MenuItemName.of("Bacon Burger"), restaurantId, menuItemId)).thenReturn(false);
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(updatedMenuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.name()).isEqualTo("Bacon Burger");
        assertThat(output.description()).isEqualTo("Double patty with bacon");
        assertThat(output.price()).isEqualByComparingTo(new BigDecimal("24.90"));
        assertThat(output.dineInOnly()).isTrue();
        assertThat(output.imagePath()).isEqualTo("/images/bacon-burger.jpg");
    }

    @Test
    @DisplayName("Should not check name uniqueness when name is null")
    void shouldNotCheckNameUniquenessWhenNameIsNull() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, "New description", null, null, null, null, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(menuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        useCase.execute(input);

        verify(menuItemGateway, never()).existsByNameAndRestaurantIdAndIdNot(any(), any(), any());
    }

    @Test
    @DisplayName("Should not update price when only price is provided without currency")
    void shouldNotUpdatePriceWhenOnlyPriceIsProvidedWithoutCurrency() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, null,
                new BigDecimal("24.90"), null, null, null, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(menuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        var output = useCase.execute(input);

        assertThat(output.price()).isEqualByComparingTo(new BigDecimal("19.90"));
    }

    @Test
    @DisplayName("Should call gateway save once")
    void shouldCallGatewaySaveOnce() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, "New description", null, null, null, null, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.save(any(MenuItem.class))).thenReturn(menuItem);
        doNothing().when(authorizationService).validateMenuItemOwnership(restaurant, ownerId);

        useCase.execute(input);

        verify(menuItemGateway, times(1)).save(any(MenuItem.class));
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when MenuItem is not found")
    void shouldThrowNotFoundExceptionWhenMenuItemIsNotFound() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, "New description", null, null, null, null, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(menuItemId.getValue().toString());
    }

    @Test
    @DisplayName("Should throw NotFoundException when Restaurant is not found")
    void shouldThrowNotFoundExceptionWhenRestaurantIsNotFound() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, "New description", null, null, null, null, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(restaurantId.getValue().toString());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not the owner")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, "New description", null, null, null, null, differentOwnerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        doThrow(new UnauthorizedException("Only the owner can update items of the restaurant."))
                .when(authorizationService).validateMenuItemOwnership(restaurant, differentOwnerId);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only the owner can update items of the restaurant.");
    }

    @Test
    @DisplayName("Should not call save when requester is not the owner")
    void shouldNotCallSaveWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, "New description", null, null, null, null, differentOwnerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        doThrow(new UnauthorizedException("Only the owner can update items of the restaurant."))
                .when(authorizationService).validateMenuItemOwnership(restaurant, differentOwnerId);


        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UnauthorizedException.class);

        verify(menuItemGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when name already exists in Restaurant")
    void shouldThrowAlreadyExistsExceptionWhenNameAlreadyExistsInRestaurant() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, "Bacon Burger", null, null, null, null, null, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantIdAndIdNot(MenuItemName.of("Bacon Burger"), restaurantId, menuItemId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Bacon Burger");
    }

    @Test
    @DisplayName("Should not call save when name already exists in Restaurant")
    void shouldNotCallSaveWhenNameAlreadyExistsInRestaurant() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, "Bacon Burger", null, null, null, null, null, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.existsByNameAndRestaurantIdAndIdNot(MenuItemName.of("Bacon Burger"), restaurantId, menuItemId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class);

        verify(menuItemGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DomainException when name is blank")
    void shouldThrowDomainExceptionWhenNameIsBlank() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, "", null, null, null, null, null, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("MenuItem name is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when price is negative")
    void shouldThrowDomainExceptionWhenPriceIsNegative() {
        var input = new UpdateMenuItemUseCase.Input(menuItemId, null, null,
                new BigDecimal("-1.00"), Currency.getInstance("BRL"), null, null, ownerId);

        when(menuItemGateway.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("MenuItem price must not be negative.");
    }
}