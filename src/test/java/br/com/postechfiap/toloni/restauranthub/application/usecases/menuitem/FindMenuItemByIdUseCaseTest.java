package br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItem;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemWithRestaurantName;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
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
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FindMenuItemByIdUseCaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;

    @InjectMocks
    private FindMenuItemByIdUseCase useCase;

    private MenuItemId id;
    private RestaurantId restaurantId;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        id = MenuItemId.generate();
        restaurantId = RestaurantId.generate();
        menuItem = new MenuItem(
                id,
                MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.ofBRL(new BigDecimal("19.90")),
                false,
                MenuItemImagePath.of("/images/classic-burger.jpg"),
                restaurantId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should find MenuItem by id successfully")
    void shouldFindMenuItemByIdSuccessfully() {
        var enriched = new MenuItemWithRestaurantName(menuItem, "The Great Burger");

        when(menuItemGateway.findByIdWithRestaurantName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindMenuItemByIdUseCase.Input(id));

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name()).isEqualTo(menuItem.getName().getValue());
        assertThat(output.description()).isEqualTo(menuItem.getDescription().getValue());
        assertThat(output.price()).isEqualByComparingTo(menuItem.getPrice().getAmount());
        assertThat(output.currency()).isEqualTo(menuItem.getPrice().getCurrency());
        assertThat(output.dineInOnly()).isEqualTo(menuItem.isDineInOnly());
        assertThat(output.imagePath()).isEqualTo(menuItem.getImagePath().getValue());
        assertThat(output.restaurantId()).isEqualTo(restaurantId);
        assertThat(output.restaurantName()).isEqualTo("The Great Burger");
    }

    @Test
    @DisplayName("Should map MenuItem to Output correctly")
    void shouldMapMenuItemToOutputCorrectly() {
        var enriched = new MenuItemWithRestaurantName(menuItem, "The Great Burger");

        when(menuItemGateway.findByIdWithRestaurantName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindMenuItemByIdUseCase.Input(id));

        assertThat(output.id()).isEqualTo(menuItem.getId());
        assertThat(output.name()).isEqualTo("Classic Burger");
        assertThat(output.description()).isEqualTo("Juicy beef patty with lettuce and tomato");
        assertThat(output.price()).isEqualByComparingTo(new BigDecimal("19.90"));
        assertThat(output.currency()).isEqualTo(Currency.getInstance("BRL"));
        assertThat(output.dineInOnly()).isFalse();
        assertThat(output.imagePath()).isEqualTo("/images/classic-burger.jpg");
        assertThat(output.restaurantId()).isEqualTo(restaurantId);
        assertThat(output.restaurantName()).isEqualTo("The Great Burger");
    }

    @Test
    @DisplayName("Should return restaurantName in output")
    void shouldReturnRestaurantNameInOutput() {
        var enriched = new MenuItemWithRestaurantName(menuItem, "The Great Burger");

        when(menuItemGateway.findByIdWithRestaurantName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindMenuItemByIdUseCase.Input(id));

        assertThat(output.restaurantName()).isEqualTo("The Great Burger");
    }

    @Test
    @DisplayName("Should return null restaurantName when Restaurant is not found")
    void shouldReturnNullRestaurantNameWhenRestaurantIsNotFound() {
        var enriched = new MenuItemWithRestaurantName(menuItem, null);

        when(menuItemGateway.findByIdWithRestaurantName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindMenuItemByIdUseCase.Input(id));

        assertThat(output.restaurantName()).isNull();
    }

    @Test
    @DisplayName("Should return dineInOnly true when MenuItem is dine-in only")
    void shouldReturnDineInOnlyTrueWhenMenuItemIsDineInOnly() {
        var dineInMenuItem = new MenuItem(
                id,
                MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.ofBRL(new BigDecimal("19.90")),
                true,
                MenuItemImagePath.of("/images/classic-burger.jpg"),
                restaurantId
        );
        var enriched = new MenuItemWithRestaurantName(dineInMenuItem, "The Great Burger");

        when(menuItemGateway.findByIdWithRestaurantName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindMenuItemByIdUseCase.Input(id));

        assertThat(output.dineInOnly()).isTrue();
    }

    @Test
    @DisplayName("Should call gateway findByIdWithRestaurantName with correct id")
    void shouldCallGatewayFindByIdWithRestaurantNameWithCorrectId() {
        var enriched = new MenuItemWithRestaurantName(menuItem, "The Great Burger");

        when(menuItemGateway.findByIdWithRestaurantName(id)).thenReturn(Optional.of(enriched));

        useCase.execute(new FindMenuItemByIdUseCase.Input(id));

        verify(menuItemGateway, times(1)).findByIdWithRestaurantName(id);
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when MenuItem is not found")
    void shouldThrowNotFoundExceptionWhenMenuItemIsNotFound() {
        when(menuItemGateway.findByIdWithRestaurantName(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new FindMenuItemByIdUseCase.Input(id)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(id.getValue().toString());
    }

    @Test
    @DisplayName("Should not return output when MenuItem is not found")
    void shouldNotReturnOutputWhenMenuItemIsNotFound() {
        when(menuItemGateway.findByIdWithRestaurantName(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new FindMenuItemByIdUseCase.Input(id)))
                .isInstanceOf(NotFoundException.class);
    }
}