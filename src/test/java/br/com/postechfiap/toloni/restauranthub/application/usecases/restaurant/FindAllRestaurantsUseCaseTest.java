package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantWithOwnerName;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageFilter;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageSort;
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
class FindAllRestaurantsUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @InjectMocks
    private FindAllRestaurantsUseCase useCase;

    private UserId ownerId;
    private Restaurant burgerRestaurant;
    private Restaurant italianRestaurant;

    @BeforeEach
    void setUp() {
        ownerId = UserId.generate();
        burgerRestaurant = new Restaurant(
                RestaurantId.generate(),
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                ownerId
        );
        italianRestaurant = new Restaurant(
                RestaurantId.generate(),
                RestaurantName.of("La Bella Italia"),
                RestaurantAddress.of("456 Olive Ave, New York"),
                RestaurantCuisineType.of("Italian"),
                RestaurantOpeningHours.of("Tue-Sun 11am-11pm"),
                ownerId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of Restaurants")
    void shouldReturnPaginatedListOfRestaurants() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new RestaurantWithOwnerName(burgerRestaurant, "John Doe"),
                new RestaurantWithOwnerName(italianRestaurant, "John Doe")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(restaurantGateway.findAllWithOwnerName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllRestaurantsUseCase.Input(pageRequest));

        assertThat(output).isNotNull();
        assertThat(output.getContent()).hasSize(2);
        assertThat(output.getTotalElements()).isEqualTo(2L);
        assertThat(output.getPageNumber()).isZero();
        assertThat(output.getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should map Restaurant to Output correctly")
    void shouldMapRestaurantToOutputCorrectly() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(new RestaurantWithOwnerName(burgerRestaurant, "John Doe"));
        var page = Page.of(enrichedList, 0, 10, 1L);

        when(restaurantGateway.findAllWithOwnerName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllRestaurantsUseCase.Input(pageRequest));

        var first = output.getContent().get(0);
        assertThat(first.id()).isEqualTo(burgerRestaurant.getId());
        assertThat(first.name()).isEqualTo(burgerRestaurant.getName().getValue());
        assertThat(first.address()).isEqualTo(burgerRestaurant.getAddress().getValue());
        assertThat(first.cuisineType()).isEqualTo(burgerRestaurant.getCuisineType().getValue());
        assertThat(first.openingHours()).isEqualTo(burgerRestaurant.getOpeningHours().getValue());
        assertThat(first.ownerId()).isEqualTo(ownerId);
        assertThat(first.ownerName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return ownerName in output")
    void shouldReturnOwnerNameInOutput() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new RestaurantWithOwnerName(burgerRestaurant, "John Doe"),
                new RestaurantWithOwnerName(italianRestaurant, "Jane Doe")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(restaurantGateway.findAllWithOwnerName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllRestaurantsUseCase.Input(pageRequest));

        assertThat(output.getContent().get(0).ownerName()).isEqualTo("John Doe");
        assertThat(output.getContent().get(1).ownerName()).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Should return empty page when no Restaurants exist")
    void shouldReturnEmptyPageWhenNoRestaurantsExist() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.<RestaurantWithOwnerName>of(List.of(), 0, 10, 0L);

        when(restaurantGateway.findAllWithOwnerName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllRestaurantsUseCase.Input(pageRequest));

        assertThat(output.getContent()).isEmpty();
        assertThat(output.getTotalElements()).isZero();
        assertThat(output.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should return correct page metadata")
    void shouldReturnCorrectPageMetadata() {
        var pageRequest = PageRequest.of(1, 5);
        var enrichedList = List.of(new RestaurantWithOwnerName(burgerRestaurant, "John Doe"));
        var page = Page.of(enrichedList, 1, 5, 6L);

        when(restaurantGateway.findAllWithOwnerName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllRestaurantsUseCase.Input(pageRequest));

        assertThat(output.getPageNumber()).isEqualTo(1);
        assertThat(output.getPageSize()).isEqualTo(5);
        assertThat(output.getTotalElements()).isEqualTo(6L);
        assertThat(output.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return first page correctly")
    void shouldReturnFirstPageCorrectly() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new RestaurantWithOwnerName(burgerRestaurant, "John Doe"),
                new RestaurantWithOwnerName(italianRestaurant, "John Doe")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(restaurantGateway.findAllWithOwnerName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllRestaurantsUseCase.Input(pageRequest));

        assertThat(output.isFirst()).isTrue();
        assertThat(output.isLast()).isTrue();
        assertThat(output.hasNext()).isFalse();
        assertThat(output.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName("Should call gateway findAllWithOwnerName with correct pageRequest")
    void shouldCallGatewayFindAllWithOwnerNameWithCorrectPageRequest() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.<RestaurantWithOwnerName>of(List.of(), 0, 10, 0L);

        when(restaurantGateway.findAllWithOwnerName(pageRequest)).thenReturn(page);

        useCase.execute(new FindAllRestaurantsUseCase.Input(pageRequest));

        verify(restaurantGateway, times(1)).findAllWithOwnerName(pageRequest);
    }

    @Test
    @DisplayName("Should return paginated list with filters")
    void shouldReturnPaginatedListWithFilters() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(PageFilter.of("name", "Burger")),
                List.of());
        var enrichedList = List.of(new RestaurantWithOwnerName(burgerRestaurant, "John Doe"));
        var page = Page.of(enrichedList, 0, 10, 1L);

        when(restaurantGateway.findAllWithOwnerName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllRestaurantsUseCase.Input(pageRequest));

        assertThat(output.getContent()).hasSize(1);
        assertThat(output.getContent().get(0).name()).isEqualTo("The Great Burger");
    }

    @Test
    @DisplayName("Should return paginated list with sorting")
    void shouldReturnPaginatedListWithSorting() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(),
                List.of(PageSort.asc("name")));
        var enrichedList = List.of(
                new RestaurantWithOwnerName(italianRestaurant, "John Doe"),
                new RestaurantWithOwnerName(burgerRestaurant, "John Doe")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(restaurantGateway.findAllWithOwnerName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllRestaurantsUseCase.Input(pageRequest));

        assertThat(output.getContent().get(0).name()).isEqualTo("La Bella Italia");
        assertThat(output.getContent().get(1).name()).isEqualTo("The Great Burger");
    }
}