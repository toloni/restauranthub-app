package br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant;

import br.com.postechfiap.toloni.restauranthub.application.gateways.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantWithOwnerName;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
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
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FindRestaurantByIdUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @InjectMocks
    private FindRestaurantByIdUseCase useCase;

    private RestaurantId id;
    private UserId ownerId;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        id = RestaurantId.generate();
        ownerId = UserId.generate();
        restaurant = new Restaurant(
                id,
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
    @DisplayName("Should find Restaurant by id successfully")
    void shouldFindRestaurantByIdSuccessfully() {
        var enriched = new RestaurantWithOwnerName(restaurant, "John Doe");

        when(restaurantGateway.findByIdWithOwnerName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindRestaurantByIdUseCase.Input(id));

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name()).isEqualTo(restaurant.getName());
        assertThat(output.address()).isEqualTo(restaurant.getAddress());
        assertThat(output.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(output.openingHours()).isEqualTo(restaurant.getOpeningHours());
        assertThat(output.ownerId()).isEqualTo(ownerId);
        assertThat(output.ownerName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should map Restaurant to Output correctly")
    void shouldMapRestaurantToOutputCorrectly() {
        var enriched = new RestaurantWithOwnerName(restaurant, "John Doe");

        when(restaurantGateway.findByIdWithOwnerName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindRestaurantByIdUseCase.Input(id));

        assertThat(output.id()).isEqualTo(restaurant.getId());
        assertThat(output.name().getValue()).isEqualTo("The Great Burger");
        assertThat(output.address().getValue()).isEqualTo("123 Main St, Springfield");
        assertThat(output.cuisineType().getValue()).isEqualTo("American");
        assertThat(output.openingHours().getValue()).isEqualTo("Mon-Fri 9am-10pm");
        assertThat(output.ownerId()).isEqualTo(ownerId);
        assertThat(output.ownerName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return ownerName in output")
    void shouldReturnOwnerNameInOutput() {
        var enriched = new RestaurantWithOwnerName(restaurant, "John Doe");

        when(restaurantGateway.findByIdWithOwnerName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindRestaurantByIdUseCase.Input(id));

        assertThat(output.ownerName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return null ownerName when owner is not found")
    void shouldReturnNullOwnerNameWhenOwnerIsNotFound() {
        var enriched = new RestaurantWithOwnerName(restaurant, null);

        when(restaurantGateway.findByIdWithOwnerName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindRestaurantByIdUseCase.Input(id));

        assertThat(output.ownerName()).isNull();
    }

    @Test
    @DisplayName("Should call gateway findByIdWithOwnerName with correct id")
    void shouldCallGatewayFindByIdWithOwnerNameWithCorrectId() {
        var enriched = new RestaurantWithOwnerName(restaurant, "John Doe");

        when(restaurantGateway.findByIdWithOwnerName(id)).thenReturn(Optional.of(enriched));

        useCase.execute(new FindRestaurantByIdUseCase.Input(id));

        verify(restaurantGateway, times(1)).findByIdWithOwnerName(id);
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when Restaurant is not found")
    void shouldThrowNotFoundExceptionWhenRestaurantIsNotFound() {
        when(restaurantGateway.findByIdWithOwnerName(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new FindRestaurantByIdUseCase.Input(id)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(id.getValue().toString());
    }

    @Test
    @DisplayName("Should not return output when Restaurant is not found")
    void shouldNotReturnOutputWhenRestaurantIsNotFound() {
        when(restaurantGateway.findByIdWithOwnerName(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new FindRestaurantByIdUseCase.Input(id)))
                .isInstanceOf(NotFoundException.class);
    }
}