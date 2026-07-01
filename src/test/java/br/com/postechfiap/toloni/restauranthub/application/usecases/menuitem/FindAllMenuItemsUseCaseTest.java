package br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem;

import br.com.postechfiap.toloni.restauranthub.application.gateways.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.application.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.application.pagination.PageFilter;
import br.com.postechfiap.toloni.restauranthub.application.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.application.pagination.PageSort;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItem;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemWithRestaurantName;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FindAllMenuItemsUseCaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;

    @InjectMocks
    private FindAllMenuItemsUseCase useCase;

    private RestaurantId restaurantId;
    private MenuItem burgerMenuItem;
    private MenuItem friesMenuItem;

    @BeforeEach
    void setUp() {
        restaurantId = RestaurantId.generate();
        burgerMenuItem = new MenuItem(
                MenuItemId.generate(),
                MenuItemName.of("Classic Burger"),
                MenuItemDescription.of("Juicy beef patty with lettuce and tomato"),
                MenuItemPrice.ofBRL(new BigDecimal("19.90")),
                false,
                MenuItemImagePath.of("/images/classic-burger.jpg"),
                restaurantId
        );
        friesMenuItem = new MenuItem(
                MenuItemId.generate(),
                MenuItemName.of("Truffle Fries"),
                MenuItemDescription.of("Crispy fries seasoned with truffle oil"),
                MenuItemPrice.ofBRL(new BigDecimal("14.90")),
                true,
                MenuItemImagePath.of("/images/truffle-fries.jpg"),
                restaurantId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of MenuItems")
    void shouldReturnPaginatedListOfMenuItems() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new MenuItemWithRestaurantName(burgerMenuItem, "The Great Burger"),
                new MenuItemWithRestaurantName(friesMenuItem, "The Great Burger")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        assertThat(output).isNotNull();
        assertThat(output.content()).hasSize(2);
        assertThat(output.totalElements()).isEqualTo(2L);
        assertThat(output.pageNumber()).isZero();
        assertThat(output.pageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should map MenuItem to Output correctly")
    void shouldMapMenuItemToOutputCorrectly() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(new MenuItemWithRestaurantName(burgerMenuItem, "The Great Burger"));
        var page = Page.of(enrichedList, 0, 10, 1L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        var first = output.content().getFirst();
        assertThat(first.id()).isEqualTo(burgerMenuItem.getId());
        assertThat(first.name()).isEqualTo(burgerMenuItem.getName());
        assertThat(first.description()).isEqualTo(burgerMenuItem.getDescription());
        assertThat(first.price()).isEqualTo(burgerMenuItem.getPrice());
        assertThat(first.dineInOnly()).isEqualTo(burgerMenuItem.isDineInOnly());
        assertThat(first.imagePath()).isEqualTo(burgerMenuItem.getImagePath());
        assertThat(first.restaurantId()).isEqualTo(restaurantId);
        assertThat(first.restaurantName()).isEqualTo("The Great Burger");
    }

    @Test
    @DisplayName("Should return restaurantName in output")
    void shouldReturnRestaurantNameInOutput() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new MenuItemWithRestaurantName(burgerMenuItem, "The Great Burger"),
                new MenuItemWithRestaurantName(friesMenuItem, "The Great Burger")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        assertThat(output.content().getFirst().restaurantName()).isEqualTo("The Great Burger");
        assertThat(output.content().get(1).restaurantName()).isEqualTo("The Great Burger");
    }

    @Test
    @DisplayName("Should filter by restaurantId when provided")
    void shouldFilterByRestaurantIdWhenProvided() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(new MenuItemWithRestaurantName(burgerMenuItem, "The Great Burger"));
        var page = Page.of(enrichedList, 0, 10, 1L);

        when(menuItemGateway.findAllWithRestaurantName(restaurantId, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(restaurantId, pageRequest));

        assertThat(output.content()).hasSize(1);
        assertThat(output.content().getFirst().restaurantId()).isEqualTo(restaurantId);
        verify(menuItemGateway, times(1)).findAllWithRestaurantName(restaurantId, pageRequest);
    }

    @Test
    @DisplayName("Should return all items when restaurantId is null")
    void shouldReturnAllItemsWhenRestaurantIdIsNull() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new MenuItemWithRestaurantName(burgerMenuItem, "The Great Burger"),
                new MenuItemWithRestaurantName(friesMenuItem, "The Great Burger")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        assertThat(output.content()).hasSize(2);
        verify(menuItemGateway, times(1)).findAllWithRestaurantName(null, pageRequest);
    }

    @Test
    @DisplayName("Should return empty page when no MenuItems exist")
    void shouldReturnEmptyPageWhenNoMenuItemsExist() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.<MenuItemWithRestaurantName>of(List.of(), 0, 10, 0L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        assertThat(output.content()).isEmpty();
        assertThat(output.totalElements()).isZero();
        assertThat(output.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should return correct page metadata")
    void shouldReturnCorrectPageMetadata() {
        var pageRequest = PageRequest.of(1, 5);
        var enrichedList = List.of(new MenuItemWithRestaurantName(burgerMenuItem, "The Great Burger"));
        var page = Page.of(enrichedList, 1, 5, 6L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        assertThat(output.pageNumber()).isEqualTo(1);
        assertThat(output.pageSize()).isEqualTo(5);
        assertThat(output.totalElements()).isEqualTo(6L);
        assertThat(output.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return first page correctly")
    void shouldReturnFirstPageCorrectly() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new MenuItemWithRestaurantName(burgerMenuItem, "The Great Burger"),
                new MenuItemWithRestaurantName(friesMenuItem, "The Great Burger")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        assertThat(output.isFirst()).isTrue();
        assertThat(output.isLast()).isTrue();
        assertThat(output.hasNext()).isFalse();
        assertThat(output.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName("Should call gateway findAllWithRestaurantName with correct pageRequest")
    void shouldCallGatewayFindAllWithRestaurantNameWithCorrectPageRequest() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.<MenuItemWithRestaurantName>of(List.of(), 0, 10, 0L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        verify(menuItemGateway, times(1)).findAllWithRestaurantName(null, pageRequest);
    }

    @Test
    @DisplayName("Should return paginated list with filters")
    void shouldReturnPaginatedListWithFilters() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(PageFilter.of("name", "Burger")),
                List.of());
        var enrichedList = List.of(new MenuItemWithRestaurantName(burgerMenuItem, "The Great Burger"));
        var page = Page.of(enrichedList, 0, 10, 1L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        assertThat(output.content()).hasSize(1);
        assertThat(output.content().getFirst().name().getValue()).isEqualTo("Classic Burger");
    }

    @Test
    @DisplayName("Should return paginated list with sorting")
    void shouldReturnPaginatedListWithSorting() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(),
                List.of(PageSort.asc("name")));
        var enrichedList = List.of(
                new MenuItemWithRestaurantName(burgerMenuItem, "The Great Burger"),
                new MenuItemWithRestaurantName(friesMenuItem, "The Great Burger")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(menuItemGateway.findAllWithRestaurantName(null, pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllMenuItemsUseCase.Input(null, pageRequest));

        assertThat(output.content().getFirst().name().getValue()).isEqualTo("Classic Burger");
        assertThat(output.content().get(1).name().getValue()).isEqualTo("Truffle Fries");
    }
}